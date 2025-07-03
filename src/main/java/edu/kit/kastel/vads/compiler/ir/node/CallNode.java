package edu.kit.kastel.vads.compiler.ir.node;

import java.util.List;

public final class CallNode extends Node {
    private final String functionName;

    public CallNode(Block block, String functionName, List<Node> parameters, Node sideEffect) {
        super(block, combine(parameters, sideEffect));
        this.functionName = functionName;
    }

    public String functionName() {
        return this.functionName;
    }

    private static Node[] combine(List<Node> parameters, Node sideEffect) {
        Node[] combined = new Node[parameters.size() + 1];
        System.arraycopy(parameters.toArray(), 0, combined, 0, parameters.size());
        combined[parameters.size()] = sideEffect;
        return combined;
    }
}
