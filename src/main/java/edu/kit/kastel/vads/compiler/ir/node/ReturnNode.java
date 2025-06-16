package edu.kit.kastel.vads.compiler.ir.node;

public final class ReturnNode extends ExitNode {
    //public static final int SIDE_EFFECT = 0;
    public static final int RESULT = 0;
    public ReturnNode(Block block, Node sideEffect, Node result) {
        // TODO: SideEffect
        super(block, result);
    }

    @Override
    public void updateBlockPredecessors() {
        this.graph().endBlock().addPredecessor(this.block());
    }
}
