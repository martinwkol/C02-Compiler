package edu.kit.kastel.vads.compiler.backend.register;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegisterAllocator {
    private int nextFreeId;
    private final Map<Node, VirtualRegister> registers = new HashMap<>();

    public RegisterAllocator() {
        nextFreeId = 0;
    }

    public VirtualRegister newVirtualRegister() {
        return new VirtualRegister(nextFreeId++);
    }

    public VirtualRegister get(Node node) {
        return registers.get(node);
    }

    public void allocateRegisters(IrGraph graph) {
        Set<Node> visited = new HashSet<>();
        visited.add(graph.endBlock());
        scan(graph.endBlock(), visited);
    }

    private void scan(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited);
            }
        }
        if (needsRegister(node)) {
            this.registers.put(node, newVirtualRegister());
        }
    }

    private static boolean needsRegister(Node node) {
        return !(node instanceof ProjNode || node instanceof StartNode || node instanceof Block || node instanceof ReturnNode);
    }
}
