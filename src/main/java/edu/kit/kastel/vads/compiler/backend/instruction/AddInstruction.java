package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

public final class AddInstruction extends BinaryOperationInstruction {
    public AddInstruction(Register destination, Register source) {
        super(destination, source);
    }
}
