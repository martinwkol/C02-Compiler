package edu.kit.kastel.vads.compiler.backend.register;

import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RegisterMapping {
    private final Map<Register, Register> mapping;

    public RegisterMapping() {
        mapping = new HashMap<>();
    }

    public RegisterMapping(Map<Register, Register> mapping) {
        this.mapping = mapping;
    }

    public @Nullable Register put(Register key, Register value) {
        return mapping.put(key, value);
    }

    public Register get(Register register) {
        return mapping.get(register);
    }

    public int computeMaxStackUsage() {
        int maxVRId = mapping.values().stream()
                .filter(r -> r instanceof VirtualRegister)
                .map(register -> ((VirtualRegister) register).id())
                .max(Comparator.comparingInt(i -> i))
                .orElse(-1);
        return (maxVRId + 1) * 8;
    }
}
