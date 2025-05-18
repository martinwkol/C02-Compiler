package edu.kit.kastel.vads.compiler.backend.register;

import edu.kit.kastel.vads.compiler.ir.node.Block;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import edu.kit.kastel.vads.compiler.ir.node.ProjNode;
import edu.kit.kastel.vads.compiler.ir.node.ReturnNode;
import edu.kit.kastel.vads.compiler.ir.node.StartNode;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VirtualRegisterAllocator implements RegisterAllocator {
    private int id;
    private final Map<Node, VirtualRegister> registers = new HashMap<>();

    @Nullable
    public Register allocateRegister(Node node) {
        if (!needsRegister(node)) return null;

        VirtualRegister register = new VirtualRegister(this.id);
        VirtualRegister oldRegister = registers.put(node, register);

        if (oldRegister != null) return oldRegister;
        this.id++;
        return register;
    }

    @Override
    public VirtualRegister get(Node node) {
        VirtualRegister register = registers.get(node);
        if (register == null) throw new NullPointerException("Node not assigned a register");
        return register;
    }

    @Override @Nullable
    public VirtualRegister getNullable(Node node) {
        return registers.get(node);
    }

    @Override
    public Set<Node> nodes() {
        return registers.keySet();
    }

    @Override
    public int requiredStackSize() {
        return this.id * 8;
    }

    private void scan(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited);
            }
        }
        if (needsRegister(node)) {
            this.registers.put(node, new VirtualRegister(this.id++));
        }
    }

    private static boolean needsRegister(Node node) {
        return !(node instanceof ProjNode || node instanceof StartNode || node instanceof Block || node instanceof ReturnNode);
    }
}
