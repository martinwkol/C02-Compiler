package edu.kit.kastel.vads.compiler.backend.instruction;

public final class JumpInstruction extends Instruction {
    private final LabelInstruction target;

    public JumpInstruction(LabelInstruction target) {
        super(false);
        this.target = target;
        addNonImmediateSuccessor(target);
    }

    public LabelInstruction target() {
        return this.target;
    }
}
