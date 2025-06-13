package edu.kit.kastel.vads.compiler.ir.node;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import org.jspecify.annotations.Nullable;

public final class Block extends Node {
    private @Nullable Node exitNode;

    public Block(IrGraph graph) {
        super(graph);
        this.exitNode = null;
    }

    public Block(IrGraph graph, @Nullable Node exitNode) {
        super(graph);
        this.exitNode = exitNode;
    }

    @Nullable public Node exitNode() {
        return exitNode;
    }

    public void setExitNode(Node exitNode) {
        this.exitNode = exitNode;
    }

}
