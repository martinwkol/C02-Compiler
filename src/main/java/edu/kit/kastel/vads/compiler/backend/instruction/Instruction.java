package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Instruction {
    protected Set<Instruction> successors;
    protected Set<Register> defines;
    protected Set<Register> uses;
    protected Set<Register> live;

    protected Instruction(Set<Register> defines, Set<Register> uses) {
        this.successors = new HashSet<>();
        this.live = new HashSet<>();
        this.defines = defines;
        this.uses = uses;
    }

    protected Instruction(Collection<Register> defines, Collection<Register> uses) {
        this.successors = new HashSet<>();
        this.live = new HashSet<>();
        this.defines = new HashSet<>();
        this.uses = new HashSet<>();

        this.defines.addAll(defines);
        this.uses.addAll(uses);
    }

    public void addSuccessor(Instruction successor) {
        successors.add(successor);
    }

    public boolean markUsedAsLive() {
        return live.addAll(uses);
    }

    public boolean deduceLiveness() {
        boolean changes = false;
        for (Instruction successor : successors) {
            for (Register liveAtSuccessor : successor.live) {
                if (!defines.contains(liveAtSuccessor)) {
                    boolean notYetLive = live.add(liveAtSuccessor);
                    changes = changes || notYetLive;
                }
            }
        }
        return changes;
    }
}
