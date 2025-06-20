package edu.kit.kastel.vads.compiler.ir.node;

public final class IfNode extends ExitNode {
    public static final int CONDITION = 1;
    private final Block trueEntry;
    private final Block falseEntry;

    public IfNode(Block block, Node sideEffect, Node condition, Block trueEntry, Block falseEntry) {
        super(block, sideEffect, condition);
        this.trueEntry = trueEntry;
        this.falseEntry = falseEntry;
    }

    public Node condition() {
        return this.predecessor(CONDITION);
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
