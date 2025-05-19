package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.instruction.*;
import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.VirtualRegister;
import edu.kit.kastel.vads.compiler.backend.register.VirtualRegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class InstructionBlock {
    private final List<Instruction> instructions = new ArrayList<>();
    private final VirtualRegisterAllocator registerAllocator;

    public InstructionBlock(IrGraph graph, VirtualRegisterAllocator registerAllocator) {
        this.registerAllocator = registerAllocator;
        Set<Node> visited = new HashSet<>();
        visited.add(graph.endBlock());
        scan(graph.endBlock(), visited);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    private void scan(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited);
            }
        }
        registerAllocator.allocateRegister(node);

        switch (node) {
            case AddNode add -> instructions.add(new AddInstruction(add, registerAllocator));
            case SubNode sub -> instructions.add(new SubInstruction(sub, registerAllocator));
            case MulNode mul -> instructions.add(new MulInstruction(mul, registerAllocator));
            case DivNode _, ModNode _ -> addDivMod(node);
            case ReturnNode r -> instructions.add(new ReturnInstruction(r, registerAllocator));
            case ConstIntNode c -> instructions.add(new ConstIntInstruction(c, registerAllocator));
            case Phi _ -> throw new UnsupportedOperationException("phi");
            case Block _, ProjNode _, StartNode _ -> {}
        }
    }

    private void addDivMod(Node node) {
        assert node instanceof DivNode || node instanceof ModNode;
        Register destination = registerAllocator.get(node);
        Register left = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.LEFT));
        Register right = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT));

        instructions.add(new MoveInstruction(left, PhysicalRegister.DividendLS));
        instructions.add(new CtldInstruction());
        instructions.add(new DivModInstruction(right));
        if (node instanceof DivNode) {
            instructions.add(new MoveInstruction(PhysicalRegister.Quotient, destination));
        }
    }


    public void deduceLiveness() {
        for (Instruction instruction : instructions) {
            instruction.markUsedAsLive();
        }

        boolean changes = true;
        while (changes) {
            changes = false;
            Instruction next = null;
            for (int i = instructions.size() - 1; i >= 0; --i) {
                Instruction instruction = instructions.get(i);
                boolean changesForInstruction = instruction.deduceLiveness(next);
                changes = changes || changesForInstruction;
                next = instruction;
            }
        }
    }

    public InterferenceGraph buildInterferenceGraph() {
        InterferenceGraph interferenceGraph = new InterferenceGraph();
        for (VirtualRegister virtualRegister : registerAllocator.usedRegisters()) {
            interferenceGraph.addRegister(virtualRegister);
        }
        Instruction next = null;
        for (int i = instructions.size() - 1; i >= 0; --i) {
            Instruction instruction = instructions.get(i);
            instruction.addEdges(interferenceGraph, next);
            next = instruction;
        }
        return interferenceGraph;
    }
}
