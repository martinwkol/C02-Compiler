package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

public final class ShiftRightInstruction extends ShiftInstruction {
    public ShiftRightInstruction(Register destination) {
        super(destination);
    }
}