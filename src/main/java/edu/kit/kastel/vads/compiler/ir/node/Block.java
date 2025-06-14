package edu.kit.kastel.vads.compiler.ir.node;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import org.jspecify.annotations.Nullable;

public final class Block extends Node {
    private @Nullable ExitNode exitNode;

    public Block(IrGraph graph) {
        super(graph);
        this.exitNode = null;
    }

    @Nullable public Node exitNode() {
        return exitNode;
    }

    public void setJumpExitNode(Block targetBlock) {
        setExitNode(new JumpNode(this, targetBlock));
    }

    public void setIfExitNode(Node condition, Block trueEntry, Block falseEntry) {
        setExitNode(new IfNode(this, condition, trueEntry, falseEntry));
    }

    public void setReturnExitNode(Node sideEffect, Node result) {
        setExitNode(new ReturnNode(this, sideEffect, result));
    }

    private void setExitNode(ExitNode exitNode) {
        if (this.exitNode != null) throw new RuntimeException("Attempted to override already existing exitNode");
        this.exitNode = exitNode;
        this.exitNode.updateBlockPredecessors();
    }

}
