package edu.kit.kastel.vads.compiler.ir.node;

import java.util.List;

public final class Phi extends Node {
    public Phi(Block block) {
        super(block);
    }

    public void appendOperand(Node node) {
        addPredecessor(node);
    }

    public List<? extends Node> operands() {
        return this.predecessors();
    }
}
