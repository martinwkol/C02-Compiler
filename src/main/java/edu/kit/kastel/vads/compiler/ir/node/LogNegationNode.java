package edu.kit.kastel.vads.compiler.ir.node;

public final class LogNegationNode extends UnaryOperationNode {
    public LogNegationNode(Block block, Node negated) {
        super(block, negated);
    }
}
