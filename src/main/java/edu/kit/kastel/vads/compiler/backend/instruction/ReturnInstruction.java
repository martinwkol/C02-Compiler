package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;

import java.util.List;

public class ReturnInstruction extends Instruction {
    public ReturnInstruction() {
        super(List.of(), List.of(PhysicalRegister.ReturnRegister));
    }
}
