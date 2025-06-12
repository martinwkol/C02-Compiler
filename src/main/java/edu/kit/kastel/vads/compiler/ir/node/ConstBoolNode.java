package edu.kit.kastel.vads.compiler.ir.node;

public final class ConstBoolNode extends Node {
    private final boolean value;

    public ConstBoolNode(Block block, boolean value) {
        super(block);
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConstBoolNode constBoolNode)) return false;
        return this.value == constBoolNode.value;
    }

    @Override
    public int hashCode() {
        return this.value ? this.getClass().hashCode() : this.getClass().hashCode() ^ 1;
    }

    @Override
    protected String info() {
        return "[" + (this.value ? "true" : "false") + "]";
    }
}
