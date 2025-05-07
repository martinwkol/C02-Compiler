package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

import java.util.List;

public abstract class BinaryOperationInstruction extends Instruction {
    protected Register destination;
    protected Register left;
    protected Register right;

    protected BinaryOperationInstruction(Register destination, Register left, Register right) {
        super(List.of(destination), List.of(left, right));

        this.destination = destination;
        this.left = left;
        this.right = right;
    }
}
