package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

import java.util.List;

public class AssignConstInstruction extends Instruction {
    private Register destination;
    private int constant;

    public AssignConstInstruction(Register destination, int constant) {
        super(List.of(destination), List.of());

        this.destination = destination;
        this.constant = constant;
    }
}
