package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.InterferenceGraph;
import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.*;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public abstract sealed class Instruction permits
        BinaryOperationInstruction, ConstIntInstruction, CtldInstruction, DivModInstruction,
        MoveInstruction, ReturnInstruction,
        LabelInstruction
{
    protected final Set<Register> live = new HashSet<>();
    protected final Set<Register> defines = new HashSet<>();
    protected final Set<Register> uses = new HashSet<>();
    protected final Set<Instruction> successors = new HashSet<>();
    protected boolean hasImmediateSuccessor;

    public Instruction(boolean hasImmediateSuccessor) {
        this.hasImmediateSuccessor = hasImmediateSuccessor;
    }

    protected void addDefines(@Nullable Register register) {
        if (register == null) throw new IllegalArgumentException("Attempted to define null register");
        defines.add(register);
    }

    protected void addUses(@Nullable Register register) {
        if (register != null) uses.add(register);
    }

    public void addNonImmediateSuccessor(Instruction successor) {
        successors.add(successor);
    }

    public boolean markUsedAsLive() {
        return live.addAll(uses);
    }

    public boolean deduceLiveness(@Nullable Instruction next) {
        boolean changes = false;
        if (next != null && hasImmediateSuccessor) {
            changes = deduceLivenessForSuccessor(next);
        }
        for (Instruction successor : successors) {
            boolean changesForSuccessor = deduceLivenessForSuccessor(successor);
            changes = changes || changesForSuccessor;
        }
        return changes;
    }

    private boolean deduceLivenessForSuccessor(Instruction successor) {
        boolean changes = false;
        for (Register liveAtSuccessor : successor.live) {
            if (!defines.contains(liveAtSuccessor)) {
                boolean notYetLive = live.add(liveAtSuccessor);
                changes = changes || notYetLive;
            }
        }
        return changes;
    }

    public void addEdges(InterferenceGraph interferenceGraph, @Nullable Instruction next) {
        if (next != null && hasImmediateSuccessor) addEdgesForSuccessor(interferenceGraph, next);
        for (Instruction successor : successors) addEdgesForSuccessor(interferenceGraph, successor);
    }

    private void addEdgesForSuccessor(InterferenceGraph interferenceGraph, Instruction successor) {
        for (Register defined : defines) {
            for (Register liveSuccessor : successor.live) {
                if (!defined.equals(liveSuccessor)) {
                    interferenceGraph.addEdge(defined, liveSuccessor);
                }
            }
        }
    }
}
