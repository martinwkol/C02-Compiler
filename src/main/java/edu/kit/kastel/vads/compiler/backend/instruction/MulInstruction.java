package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

public final class MulInstruction extends BinaryOperationInstruction {
    public MulInstruction(Register destination, Register source) {
        super(destination, source);
    }
}
