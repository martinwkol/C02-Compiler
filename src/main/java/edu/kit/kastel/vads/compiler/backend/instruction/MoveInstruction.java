package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

import java.util.List;

public class MoveInstruction extends Instruction {
    protected Register destination;
    protected Register source;

    public MoveInstruction(Register destination, Register source) {
        super(List.of(destination), List.of(source));

        this.destination = destination;
        this.source = source;
    }
}
