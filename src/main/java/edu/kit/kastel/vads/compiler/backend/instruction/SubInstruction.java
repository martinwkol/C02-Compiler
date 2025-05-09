package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

public final class SubInstruction extends BinaryOperationInstruction {
    public SubInstruction(Register destination, Register source) {
        super(destination, source);
    }
}
