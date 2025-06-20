package edu.kit.kastel.vads.compiler.ir.node;

public final class JumpNode extends ExitNode {
    private final Block targetBlock;

    public JumpNode(Block block, Node sideEffect, Block targetBlock) {
        super(block, sideEffect);
        this.targetBlock = targetBlock;
    }

    public Block targetBlock() {
        return targetBlock;
    }

    @Override
    public void updateBlockPredecessors() {
        this.targetBlock.addPredecessor(this.block());
    }
}
