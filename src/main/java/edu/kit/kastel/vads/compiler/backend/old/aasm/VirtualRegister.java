package edu.kit.kastel.vads.compiler.backend.old.aasm;

import edu.kit.kastel.vads.compiler.backend.old.regalloc.Register;

public record VirtualRegister(int id) implements Register {
    @Override
    public String toString() {
        return "%" + id();
    }
}
