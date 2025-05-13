package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.instruction.*;
import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.register.VirtualRegister;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.*;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class InstructionGenerator {
    private List<Instruction> instructions;
    private RegisterAllocator registerAllocator;

    public InstructionGenerator() {
        instructions = new ArrayList<>();
        registerAllocator = new RegisterAllocator();
    }

    public void generate(IrGraph graph) {
        registerAllocator.allocateRegisters(graph);
        Set<Node> visited = new HashSet<>();
        scan(graph.endBlock(), visited);
    }

    private void scan(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited);
            }
        }

        switch (node) {
            case BinaryOperationNode binaryOp -> binary(binaryOp);
            default -> {}
        }

    }

    private void binary(BinaryOperationNode node) {
        VirtualRegister destination = registerAllocator.get(node);
        VirtualRegister left = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.LEFT));
        VirtualRegister right = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT));

        if (!(node instanceof DivNode || node instanceof ModNode)) {
            move(destination, left);
            switch (node) {
                case AddNode _ -> addInstruction(new AddInstruction(destination, right));
                case SubNode _ -> addInstruction(new SubInstruction(destination, right));
                case MulNode _ -> addInstruction(new MulInstruction(destination, right));
                default -> throw new IllegalStateException("DivNode and ModNode should not be handled here");
            }
        } else {
            move(PhysicalRegister.DividendLS, left);
            addInstruction(new DivInstruction(right));
            if (node instanceof DivNode) {
                move(destination, PhysicalRegister.Quotient);
            } else {
                move(destination, PhysicalRegister.Remainder);
            }
        }
    }

    private void move(Register destination, Register source) {
        addInstruction(new MoveInstruction(destination, source));
    }

    private void addInstruction(Instruction instruction) {
        if (!instructions.isEmpty()) {
            Instruction last = instructions.getLast();
            if (!(last instanceof ReturnInstruction)) {
                last.addSuccessor(instruction);
            }
        }
        instructions.add(instruction);
    }
}
