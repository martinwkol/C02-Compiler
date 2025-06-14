package edu.kit.kastel.vads.compiler.ir.node;

public final class IfNode extends Node {
    private final Block trueBlock;
    private final Block falseBlock;

    public IfNode(Block block, Node condition, Block trueBlock, Block falseBlock) {
        super(block, condition);
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    public Block trueBlock() {
        return trueBlock;
    }

    public Block falseBlock() {
        return falseBlock;
    }
}
