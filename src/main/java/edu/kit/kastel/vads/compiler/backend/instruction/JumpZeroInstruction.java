package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

public final class JumpZeroInstruction extends ConditionalJumpInstruction {
    public JumpZeroInstruction(LabelInstruction target, Register register) {
        super(target, register);
    }
}
