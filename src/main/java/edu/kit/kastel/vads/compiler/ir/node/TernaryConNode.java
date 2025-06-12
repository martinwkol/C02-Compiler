package edu.kit.kastel.vads.compiler.ir.node;

public final class TernaryConNode extends Node {
    private final Node caseTrue;
    private final Node caseFalse;

    public TernaryConNode(Block block, Node condition, Node caseTrue, Node caseFalse) {
        super(block, condition);
        this.caseTrue = caseTrue;
        this.caseFalse = caseFalse;
    }

    public Node caseTrue() {
        return caseTrue;
    }

    public Node caseFalse() {
        return caseFalse;
    }
}
