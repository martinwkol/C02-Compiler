package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.parser.ast.ProgramTree;
import edu.kit.kastel.vads.compiler.parser.visitor.RecursivePostorderVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.RecursiveVisitor;

import javax.sound.midi.VoiceStatus;

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
        this.program.accept(new VariableStatusAnalysisVisitor(), new VariableStatus());
        this.program.accept(
                new RecursivePostorderVisitor<>(new TypeAnalysis()),
                new TypeAnalysis.TypeMapping()
        );
        this.program.accept(
                new RecursivePostorderVisitor<>(new ReturnAnalysis()),
                new ReturnAnalysis.ReturnState()
        );
        this.program.accept(
                new RecursiveVisitor<>(
                        new BreakContinueAnalysis.PreorderVisitor(),
                        new BreakContinueAnalysis.PostorderVisitor()
                ),
                new BreakContinueAnalysis.Counter()
        );
        this.program.accept(new RecursivePostorderVisitor<>(
                new ForLoopStepAnalysis()),
                new Namespace<>()
        );
    }

}
