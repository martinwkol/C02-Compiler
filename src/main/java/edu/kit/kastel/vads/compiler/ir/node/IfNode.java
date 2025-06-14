package edu.kit.kastel.vads.compiler.ir.node;

public final class IfNode extends ExitNode {
    private final Block trueEntry;
    private final Block falseEntry;

    public IfNode(Block block, Node condition, Block trueEntry, Block falseEntry) {
        super(block, condition);
        this.trueEntry = trueEntry;
        this.falseEntry = falseEntry;
    }

    public Block trueEntry() {
        return trueEntry;
    }

    public Block falseEntry() {
        return falseEntry;
    }

    @Override
    public void updateBlockPredecessors() {
        this.trueEntry.addPredecessor(this.block());
        this.falseEntry.addPredecessor(this.block());
    }
}
