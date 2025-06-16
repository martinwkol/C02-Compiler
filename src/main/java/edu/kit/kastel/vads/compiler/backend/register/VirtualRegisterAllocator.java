package edu.kit.kastel.vads.compiler.backend.register;

import edu.kit.kastel.vads.compiler.ir.node.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

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

    public boolean isAllocated(Node node) {
        return registers.containsKey(node);
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

    public Collection<VirtualRegister> usedRegisters() {
        return registers.values();
    }

    private static boolean needsRegister(Node node) {
        return !(
            node instanceof ProjNode || node instanceof StartNode ||
            node instanceof Block || node instanceof ReturnNode ||
            node instanceof InvalidNode
        );
    }
}
