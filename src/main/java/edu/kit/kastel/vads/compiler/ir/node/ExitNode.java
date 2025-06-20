package edu.kit.kastel.vads.compiler.ir.node;

public abstract sealed class ExitNode extends Node permits JumpNode, IfNode, ReturnNode {

    public ExitNode(Block block, Node sideEffect, Node... predecessors) {
        super(block, combine(sideEffect, predecessors));
    }

    public abstract void updateBlockPredecessors();

    private static Node[] combine(Node sideEffect, Node[] predecessors) {
        Node[] combined = new Node[predecessors.length + 1];
        combined[0] = sideEffect;
        System.arraycopy(predecessors, 0, combined, 1, predecessors.length);
        return combined;
    }
}
