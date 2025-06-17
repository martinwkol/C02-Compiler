package edu.kit.kastel.vads.compiler.ir.node;

public abstract sealed class UnaryOperationNode extends Node permits
        AssignNode, LogNegationNode, BitNegationNode
{
    public static int NODE = 0;

    public UnaryOperationNode(Block block, Node node) {
        super(block, node);
    }

    public Node node() {
        return this.predecessor(NODE);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UnaryOperationNode unaryOperation)) return false;
        return this.predecessor(NODE).equals(unaryOperation.predecessor(NODE));
    }

    @Override
    public int hashCode() {
        return this.predecessor(NODE).hashCode() ^ this.getClass().hashCode();
    }
}
