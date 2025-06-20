package edu.kit.kastel.vads.compiler.ir.node;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import org.jspecify.annotations.Nullable;

public final class Block extends Node {
    private @Nullable ExitNode exitNode;

    public Block(IrGraph graph) {
        super(graph);
        this.exitNode = null;
    }

    @Nullable public ExitNode exitNode() {
        return exitNode;
    }

    public ExitNode setExitNode(ExitNode exitNode) {
        if (this.exitNode != null) return exitNode; // The first set counts
        this.exitNode = exitNode;
        this.exitNode.updateBlockPredecessors();
        return exitNode;
    }

}
