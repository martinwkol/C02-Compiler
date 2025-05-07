package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

public class AddInstruction extends BinaryOperationInstruction {

    public AddInstruction(Register destination, Register left, Register right) {
        super(destination, left, right);
    }

}
