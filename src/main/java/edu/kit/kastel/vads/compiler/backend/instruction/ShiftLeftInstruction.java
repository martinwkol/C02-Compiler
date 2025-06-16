package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

public final class ShiftLeftInstruction extends ShiftInstruction {
    public ShiftLeftInstruction(Register source, Register destination) {
        super(source, destination);
    }
}