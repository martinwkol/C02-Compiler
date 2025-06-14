package edu.kit.kastel.vads.compiler.ir.node;

public abstract sealed class ExitNode extends Node permits JumpNode, IfNode, ReturnNode {
    public ExitNode(Block block, Node... predecessors) {
        super(block, predecessors);
    }

    public abstract void updateBlockPredecessors();
}
