package edu.kit.kastel.vads.compiler.ir.node;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import org.jspecify.annotations.Nullable;

public final class Block extends Node {
    private @Nullable ExitNode exitNode;

    public Block(IrGraph graph) {
        super(graph);
        this.exitNode = null;
    }

    @Nullable public ExitNode exitNode() {
        return exitNode;
    }

    public ExitNode setJumpExitNode(Block targetBlock) {
        return setExitNode(new JumpNode(this, targetBlock));
    }

    public ExitNode setIfExitNode(Node condition, Block trueEntry, Block falseEntry) {
        return setExitNode(new IfNode(this, condition, trueEntry, falseEntry));
    }

    public ExitNode setReturnExitNode(Node sideEffect, Node result) {
        return setExitNode(new ReturnNode(this, sideEffect, result));
    }

    private ExitNode setExitNode(ExitNode exitNode) {
        if (this.exitNode != null) throw new RuntimeException("Attempted to override already existing exitNode");
        this.exitNode = exitNode;
        this.exitNode.updateBlockPredecessors();
        return exitNode;
    }

}
