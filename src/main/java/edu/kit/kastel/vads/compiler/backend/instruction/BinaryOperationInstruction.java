package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

import java.util.List;

public abstract sealed class BinaryOperationInstruction extends Instruction permits AddInstruction, SubInstruction, MulInstruction {
    protected Register destination;
    protected Register source;

    protected BinaryOperationInstruction(Register destination, Register source) {
        super(List.of(destination), List.of(destination, source));

        this.destination = destination;
        this.source = source;
    }
}
