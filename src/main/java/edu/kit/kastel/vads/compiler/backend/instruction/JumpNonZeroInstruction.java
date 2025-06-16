package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;
import edu.kit.kastel.vads.compiler.ir.node.Block;

public final class JumpNonZeroInstruction extends ConditionalJumpInstruction {
    public JumpNonZeroInstruction(LabelInstruction target, Register register) {
        super(target, register);
    }
}
