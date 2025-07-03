package edu.kit.kastel.vads.compiler.ir.node;

import java.util.List;

public final class CallNode extends Node {
    public final static int SIDE_EFFECT = 0;
    public final static int PARAMETERS_START = 1;

    private final String functionName;

    public CallNode(Block block, String functionName, Node sideEffect, List<Node> parameters) {
        super(block, combine(sideEffect, parameters));
        this.functionName = functionName;
    }

    public String functionName() {
        return this.functionName;
    }

    public List<? extends Node> parameters() {
        return this.predecessors().subList(PARAMETERS_START, this.predecessors().size());
    }

    private static Node[] combine(Node sideEffect, List<Node> parameters) {
        Node[] combined = new Node[parameters.size() + 1];
        combined[0] = sideEffect;
        System.arraycopy(parameters.toArray(), 0, combined, 1, parameters.size());
        return combined;
    }
}
