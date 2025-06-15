package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.ir.node.Block;

public final class JumpAlwaysInstruction extends JumpInstruction {
    public JumpAlwaysInstruction(Block target) {
        super(target);
    }
}
