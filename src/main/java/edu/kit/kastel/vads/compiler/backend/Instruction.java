package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.*;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class Instruction {
    private Node node;
    private Set<Register> live = new HashSet<>();
    private Set<Register> defines = new HashSet<>();
    private Set<Register> uses = new HashSet<>();
    private Set<Instruction> successors = new HashSet<>();
    private boolean hasImmediateSuccessor;

    public Instruction(Node node, RegisterAllocator registerAllocator) {
        this.node = node;
        hasImmediateSuccessor = true;

        switch (node) {
            case BinaryOperationNode bNode -> {
                binary(bNode, registerAllocator);
                if (bNode instanceof DivNode || bNode instanceof ModNode) {
                    definesDivRegisters();
                }
            }
            case ReturnNode r -> {
                defines.add(PhysicalRegister.Return);
                uses.add(registerAllocator.get(predecessorSkipProj(r, ReturnNode.RESULT)));
                hasImmediateSuccessor = false;
            }
            case Phi _ -> throw new UnsupportedOperationException("phi");
            case ConstIntNode _, Block _, ProjNode _, StartNode _ -> {}
        }
    }

    private void binary(BinaryOperationNode bNode, RegisterAllocator registerAllocator) {
        Register destination = registerAllocator.get(bNode);
        Register left = registerAllocator.get(predecessorSkipProj(bNode, BinaryOperationNode.LEFT));
        Register right = registerAllocator.get(predecessorSkipProj(bNode, BinaryOperationNode.RIGHT));
        defines.add(destination);
        uses.add(left);
        uses.add(right);
    }

    private void definesDivRegisters() {
        defines.add(PhysicalRegister.DividendLS);
        defines.add(PhysicalRegister.DividendMS);
        defines.add(PhysicalRegister.Quotient);
        defines.add(PhysicalRegister.Remainder);
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
            for (Register used : successor.uses) {
                interferenceGraph.addEdge(defined, used);
            }
        }
    }
}
