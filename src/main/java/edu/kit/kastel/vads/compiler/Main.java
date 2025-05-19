package edu.kit.kastel.vads.compiler;

import edu.kit.kastel.vads.compiler.backend.AssemblyGenerator;
import edu.kit.kastel.vads.compiler.backend.InstructionBlock;
import edu.kit.kastel.vads.compiler.backend.InterferenceGraph;
import edu.kit.kastel.vads.compiler.backend.register.OptimizedRegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;
import edu.kit.kastel.vads.compiler.backend.register.VirtualRegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.SsaTranslation;
import edu.kit.kastel.vads.compiler.ir.optimize.LocalValueNumbering;
import edu.kit.kastel.vads.compiler.ir.util.YCompPrinter;
import edu.kit.kastel.vads.compiler.lexer.Lexer;
import edu.kit.kastel.vads.compiler.parser.ParseException;
import edu.kit.kastel.vads.compiler.parser.Parser;
import edu.kit.kastel.vads.compiler.parser.TokenSource;
import edu.kit.kastel.vads.compiler.parser.ast.FunctionTree;
import edu.kit.kastel.vads.compiler.parser.ast.ProgramTree;
import edu.kit.kastel.vads.compiler.semantic.SemanticAnalysis;
import edu.kit.kastel.vads.compiler.semantic.SemanticException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 2) {
            System.err.println("Invalid arguments: Expected one input file and one output file");
            System.exit(3);
        }
        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);
        Path assembly = Path.of(args[1] + ".s");
        ProgramTree program = lexAndParse(input);
        try {
            new SemanticAnalysis(program).analyze();
        } catch (SemanticException e) {
            e.printStackTrace();
            System.exit(7);
            return;
        }
        List<IrGraph> graphs = new ArrayList<>();
        for (FunctionTree function : program.topLevelTrees()) {
            SsaTranslation translation = new SsaTranslation(function, new LocalValueNumbering());
            graphs.add(translation.translate());
        }

        if ("vcg".equals(System.getenv("DUMP_GRAPHS")) || "vcg".equals(System.getProperty("dumpGraphs"))) {
            Path tmp = output.toAbsolutePath().resolveSibling("graphs");
            Files.createDirectory(tmp);
            for (IrGraph graph : graphs) {
                dumpGraph(graph, tmp, "before-codegen");
            }
        }

        // TODO: generate assembly and invoke gcc instead of generating abstract assembly
        VirtualRegisterAllocator virtualRA = new VirtualRegisterAllocator();
        InstructionBlock ib = new InstructionBlock(graphs.getFirst(), virtualRA);
        ib.deduceLiveness();

        InterferenceGraph interferenceGraph = ib.buildInterferenceGraph();
        /*OptimizedRegisterAllocator optimizedRA = new OptimizedRegisterAllocator(
                virtualRA, interferenceGraph.computeRegisterAssignment()
        );*/
        RegisterMapping registerMapping = interferenceGraph.computeRegisterMapping();
        AssemblyGenerator assemblyGenerator = new AssemblyGenerator(ib, registerMapping, registerMapping.computeMaxStackSize());


        // Write assembly file
        Files.writeString(assembly, assemblyGenerator.getAssembly());

        // Compile assembly
        ProcessBuilder pb = new ProcessBuilder("gcc", assembly.toString(), "-o", output.toString());
        Process process = pb.start();
        int exitCode = process.waitFor();
        System.exit(exitCode);
    }

    private static ProgramTree lexAndParse(Path input) throws IOException {
        try {
            Lexer lexer = Lexer.forString(Files.readString(input));
            TokenSource tokenSource = new TokenSource(lexer);
            Parser parser = new Parser(tokenSource);
            return parser.parseProgram();
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(42);
            throw new AssertionError("unreachable");
        }
    }

    private static void dumpGraph(IrGraph graph, Path path, String key) throws IOException {
        Files.writeString(
            path.resolve(graph.name() + "-" + key + ".vcg"),
            YCompPrinter.print(graph)
        );
    }
}
