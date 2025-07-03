package edu.kit.kastel.vads.compiler.ir.node;

import java.util.List;

public final class CallNode extends Node {
    private final String functionName;

    public CallNode(Block block, String functionName, List<Node> parameters) {
        super(block, parameters);
        this.functionName = functionName;
    }

    public String functionName() {
        return this.functionName;
    }
}
