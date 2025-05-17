package edu.kit.kastel.vads.compiler.backend.register;

import edu.kit.kastel.vads.compiler.ir.node.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImprovedRegisterAllocator implements RegisterAllocator {
    private final Map<Node, Register> registers = new HashMap<>();

    public ImprovedRegisterAllocator(RegisterAllocator registerAllocator, Map<Register, Register> registerMap) {
        for (Node node : registerAllocator.nodes()) {
            Register mappedRegister = registerMap.get(registerAllocator.get(node));
            if (mappedRegister == null) throw new IllegalArgumentException("registerMap misses needed register");
            registers.put(node, mappedRegister);
        }
    }

    @Override
    public Register get(Node node) {
        return registers.get(node);
    }

    @Override
    public Set<Node> nodes() {
        return registers.keySet();
    }
}
