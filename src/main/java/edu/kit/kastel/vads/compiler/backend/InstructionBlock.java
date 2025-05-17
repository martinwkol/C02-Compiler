package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.register.VirtualRegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstructionBlock {
    private final List<Instruction> instructions = new ArrayList<>();
    private final VirtualRegisterAllocator registerAllocator = new VirtualRegisterAllocator();

    public InstructionBlock(IrGraph graph) {
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
        instructions.add(new Instruction(node, registerAllocator));
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
        Instruction next = null;
        for (int i = instructions.size() - 1; i >= 0; --i) {
            Instruction instruction = instructions.get(i);
            instruction.addEdges(interferenceGraph, next);
            next = instruction;
        }
        return interferenceGraph;
    }
}
