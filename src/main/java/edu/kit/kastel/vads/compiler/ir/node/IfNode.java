package edu.kit.kastel.vads.compiler.ir.node;

public final class IfNode extends Node {
    private final Block targetBlock;

    public IfNode(Block block, Node condition, Block targetBlock) {
        super(block, condition);
        this.targetBlock = targetBlock;
    }

    public Block targetBlock() {
        return targetBlock;
    }
}
