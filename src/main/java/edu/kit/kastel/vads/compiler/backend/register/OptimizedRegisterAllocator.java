package edu.kit.kastel.vads.compiler.backend.register;

import edu.kit.kastel.vads.compiler.ir.node.Node;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OptimizedRegisterAllocator implements RegisterAllocator {
    private final Map<Node, Register> registers = new HashMap<>();
    private int nextFreeVRId;

    public OptimizedRegisterAllocator(RegisterAllocator registerAllocator, Map<Register, Register> registerMap) {
        int maxVRId = 0;
        for (Node node : registerAllocator.nodes()) {
            Register mappedRegister = registerMap.get(registerAllocator.getNullable(node));
            if (mappedRegister == null) throw new IllegalArgumentException("registerMap misses needed register");
            if (mappedRegister instanceof VirtualRegister(int id)) {
                if (id > maxVRId) {
                    maxVRId = id;
                }
            }
            registers.put(node, mappedRegister);
        }
        nextFreeVRId = maxVRId + 1;
    }

    @Override
    public Register get(Node node) {
        Register register = registers.get(node);
        if (register == null) throw new NullPointerException("Node not assigned a register");
        return register;
    }

    @Override @Nullable
    public Register getNullable(Node node) {
        return registers.get(node);
    }

    @Override
    public Set<Node> nodes() {
        return registers.keySet();
    }

    @Override
    public int requiredStackSize() {
        return nextFreeVRId * 8;
    }
}
