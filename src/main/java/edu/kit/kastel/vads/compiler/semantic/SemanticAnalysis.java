package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.parser.ast.ProgramTree;
import edu.kit.kastel.vads.compiler.parser.visitor.RecursivePostorderVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.RecursiveVisitor;

public class SemanticAnalysis {

    private final ProgramTree program;

    public SemanticAnalysis(ProgramTree program) {
        this.program = program;
    }

    public void analyze() {
        this.program.accept(
                new RecursivePostorderVisitor<>(new IntegerLiteralRangeAnalysis()),
                new Namespace<>()
        );
        this.program.accept(
                new RecursiveVisitor<>(
                        new VariableStatusAnalysis.PreorderVisitor(),
                        new VariableStatusAnalysis.PostorderVisitor()
                ),
                new VariableStatusAnalysis.VariableStatus()
        );
        this.program.accept(
                new RecursivePostorderVisitor<>(new TypeAnalysis()),
                new TypeAnalysis.TypeMapping()
        );
        this.program.accept(
                new RecursivePostorderVisitor<>(new ReturnAnalysis()),
                new ReturnAnalysis.ReturnState()
        );
    }

}
