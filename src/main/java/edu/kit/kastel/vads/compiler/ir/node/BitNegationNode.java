package edu.kit.kastel.vads.compiler.ir.node;

public final class BitNegationNode extends Node {
    public static final int NEGATED = 0;

    public BitNegationNode(Block block, Node negated) {
        super(block, negated);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BitNegationNode bitNeg)) return false;
        return this.predecessor(NEGATED).equals(bitNeg.predecessor(NEGATED));
    }

    @Override
    public int hashCode() {
        return this.predecessor(NEGATED).hashCode() ^ this.getClass().hashCode();
    }
}
