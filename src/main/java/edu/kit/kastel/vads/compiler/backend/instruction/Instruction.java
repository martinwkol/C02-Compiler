package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

import java.util.HashSet;
import java.util.Set;

public abstract class Instruction {
    protected Set<Instruction> successors;
    protected Set<Register> uses;
    protected Set<Register> defines;
    protected Set<Register> live;

    protected Instruction() {
        successors = new HashSet<>();
        uses = new HashSet<>();
        defines = new HashSet<>();
        live = new HashSet<>();
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
