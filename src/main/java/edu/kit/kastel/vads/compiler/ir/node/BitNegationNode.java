package edu.kit.kastel.vads.compiler.ir.node;

public final class BitNegationNode extends UnaryOperationNode {
    public BitNegationNode(Block block, Node negated) {
        super(block, negated);
    }
}
