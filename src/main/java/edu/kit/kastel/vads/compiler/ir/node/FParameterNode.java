package edu.kit.kastel.vads.compiler.ir.node;

public final class FParameterNode extends Node {
    private final int index;

    public FParameterNode(Block block, int index) {
        super(block);
        this.index = index;
    }

    public int index() {
        return this.index;
    }
}
