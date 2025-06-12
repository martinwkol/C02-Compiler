package edu.kit.kastel.vads.compiler.ir.node;

public final class CSmallerEqNode extends BinaryOperationNode {
    public CSmallerEqNode(Block block, Node left, Node right) {
        super(block, left, right);
    }
}
