package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

import java.util.List;

public abstract class BinaryOperationInstruction extends Instruction {
    protected Register left;
    protected Register right;

    protected BinaryOperationInstruction(Register left, Register right) {
        super(List.of(left), List.of(left, right));

        this.left = left;
        this.right = right;
    }
}
