package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;
import edu.kit.kastel.vads.compiler.ir.node.Block;

public final class JumpZeroInstruction extends ConditionalJumpInstruction {
    public JumpZeroInstruction(LabelInstruction target, Register register) {
        super(target, register);
    }
}
