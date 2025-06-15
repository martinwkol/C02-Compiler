package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.ir.node.Block;

public abstract sealed class JumpInstruction extends Instruction permits
        JumpAlwaysInstruction, JumpZeroInstruction, JumpNonZeroInstruction
{
    private final Block target;

    public JumpInstruction(Block target) {
        super(false);
        this.target = target;
    }

    public Block target() {
        return this.target;
    }
}
