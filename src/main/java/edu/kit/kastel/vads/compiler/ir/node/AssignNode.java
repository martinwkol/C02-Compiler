package edu.kit.kastel.vads.compiler.ir.node;

public final class AssignNode extends UnaryOperationNode {
    public AssignNode(Block block, Node node) {
        super(block, node);
    }
}
