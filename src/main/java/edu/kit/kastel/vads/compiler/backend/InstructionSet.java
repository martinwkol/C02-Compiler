package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.instruction.*;
import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.VirtualRegister;
import edu.kit.kastel.vads.compiler.backend.register.VirtualRegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.*;

import java.util.*;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class InstructionSet {
    private final SequencedSet<Block> blocks = new LinkedHashSet<>();
    private final Map<Block, List<Instruction>> instructions = new HashMap<>();
    private final VirtualRegisterAllocator registerAllocator;

    public InstructionSet(IrGraph graph, VirtualRegisterAllocator registerAllocator) {
        this.registerAllocator = registerAllocator;
        Set<Node> visited = new HashSet<>();
        visited.add(graph.endBlock());
        scan(graph.endBlock(), visited);
        handlePhis(graph.endBlock());
    }

    public SequencedSet<Block> getBlocks() {
        return blocks;
    }

    private void scan(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scan(predecessor, visited);
            }
        }
        registerAllocator.allocateRegister(node);

        switch (node) {
            case Block block -> scanBlock(block);
            case AddNode add -> newAdd(add);
            case SubNode sub -> newSub(sub);
            case MulNode mul -> newMul(mul);
            case DivNode div -> newDiv(div);
            case ModNode mod -> newMod(mod);
            case ReturnNode ret -> newReturn(ret);
            case ConstIntNode constInt -> newConstInt(constInt);
            case Phi _ -> {} // ignore phis for now
            case ProjNode _, StartNode _ -> {}
        }
    }

    private void handlePhis(Block endBlock) {
        Set<Node> visited = new HashSet<>();
        visited.add(endBlock);
        handlePhisRecursive(endBlock, visited);
    }

    private void handlePhisRecursive(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                handlePhisRecursive(predecessor, visited);
            }
        }
        if (node instanceof Phi phi) {
            for (Node operand : phi.operands()) {
                instructions.get(operand.block()).add(new MoveInstruction(
                    registerAllocator.get(operand), registerAllocator.get(phi)
                ));
            }
        }
    }

    private void scanBlock(Block block) {
        if (blocks.contains(block)) return;
        blocks.add(block);
        instructions.put(block, new ArrayList<>());
        instructions.get(block).add(new LabelInstruction("block" + blocks.size()));
    }

    private void newAdd(AddNode add) {
        instructions.get(add.block()).add(new AddInstruction(add, registerAllocator));
    }

    private void newSub(SubNode sub) {
        instructions.get(sub.block()).add(new SubInstruction(sub, registerAllocator));
    }

    private void newMul(MulNode mul) {
        instructions.get(mul.block()).add(new MulInstruction(mul, registerAllocator));
    }

    private void newDiv(DivNode div) {
        addDivMod(div);
    }

    private void newMod(ModNode mod) {
        addDivMod(mod);
    }

    private void newReturn(ReturnNode ret) {
        instructions.get(ret.block()).add(new ReturnInstruction(ret, registerAllocator));
    }

    private void newConstInt(ConstIntNode constInt) {
        instructions.get(constInt.block()).add(new ConstIntInstruction(constInt, registerAllocator));
    }

    private void scanPhi(Phi phi) {

    }

    private void addDivMod(Node node) {
        assert node instanceof DivNode || node instanceof ModNode;
        Register destination = registerAllocator.get(node);
        Register left = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.LEFT));
        Register right = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT));

        List<Instruction> instructionList = instructions.get(node.block());
        instructionList.add(new MoveInstruction(left, PhysicalRegister.DividendLS));
        instructionList.add(new CtldInstruction());
        instructionList.add(new DivModInstruction(right));
        if (node instanceof DivNode) {
            instructionList.add(new MoveInstruction(PhysicalRegister.Quotient, destination));
        } else {
            instructionList.add(new MoveInstruction(PhysicalRegister.Remainder, destination));
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
