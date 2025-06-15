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
    private final List<Block> blocks = new ArrayList<>();
    private final Set<Block> blockVisited = new HashSet<>();
    private final Map<Block, List<Instruction>> instructions = new HashMap<>();
    private final VirtualRegisterAllocator registerAllocator;

    public InstructionSet(IrGraph graph, VirtualRegisterAllocator registerAllocator) {
        this.registerAllocator = registerAllocator;
        scan(graph.endBlock());
        handlePhis(graph.endBlock());
        handleJumps();
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    private void scan(Block endBlock) {
        Set<Node> visited = new HashSet<>();
        visited.add(endBlock);
        scanRecursive(endBlock, visited);
    }

    private void scanRecursive(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scanRecursive(predecessor, visited);
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
            case CEqualsNode equals -> newEquals(equals);
            case CUnequalsNode unequals -> newUnequals(unequals);
            case CSmallerNode smaller -> newSmaller(smaller);
            case CSmallerEqNode smallerEq -> newSmallerEq(smallerEq);
            case CBiggerNode bigger -> newBigger(bigger);
            case CBiggerEqNode biggerEq -> newBiggerEq(biggerEq);
            case ReturnNode ret -> newReturn(ret);
            case ConstIntNode constInt -> newConstInt(constInt);
            case JumpNode _, IfNode _ -> {} // handle jumps later
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

    private void handleJumps() {
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            ExitNode exitNode = block.exitNode();
            if (exitNode instanceof JumpNode jump) {
                if (i == blocks.size() - 1 || jump.targetBlock() == blocks.get(i + 1)) continue;
                instructions.get(block).add(newJumpAlways(jump.targetBlock()));
            }
            else if (exitNode instanceof IfNode ifNode) {
                if (i < blocks.size() - 1) {
                    if (ifNode.trueEntry() == blocks.get(i + 1)) {
                        instructions.get(block).add(newJumpZero(ifNode.falseEntry(), ifNode.condition()));
                        continue;
                    }
                    if (ifNode.falseEntry() == blocks.get(i + 1)) {
                        instructions.get(block).add(newJumpNonZero(ifNode.trueEntry(), ifNode.condition()));
                        continue;
                    }
                }
                instructions.get(block).add(newJumpZero(ifNode.falseEntry(), ifNode.condition()));
                instructions.get(block).add(newJumpAlways(ifNode.trueEntry()));
            }
        }
    }

    private void scanBlock(Block block) {
        if (blockVisited.contains(block)) return;
        blocks.add(block);
        blockVisited.add(block);
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

    private void newEquals(CEqualsNode equals) {
        instructions.get(equals.block()).add(new EqualsInstruction(equals, registerAllocator));
    }

    private void newUnequals(CUnequalsNode unequals) {
        instructions.get(unequals.block()).add(new UnequalsInstruction(unequals, registerAllocator));
    }

    private void newSmaller(CSmallerNode smaller) {
        instructions.get(smaller.block()).add(new SmallerInstruction(smaller, registerAllocator));
    }

    private void newSmallerEq(CSmallerEqNode smallerEq) {
        instructions.get(smallerEq.block()).add(new SmallerEqInstruction(smallerEq, registerAllocator));
    }

    private void newBigger(CBiggerNode bigger) {
        instructions.get(bigger.block()).add(new BiggerInstruction(bigger, registerAllocator));
    }

    private void newBiggerEq(CBiggerEqNode biggerEq) {
        instructions.get(biggerEq.block()).add(new BiggerEqInstruction(biggerEq, registerAllocator));
    }

    private void newReturn(ReturnNode ret) {
        instructions.get(ret.block()).add(new ReturnInstruction(ret, registerAllocator));
    }

    private void newConstInt(ConstIntNode constInt) {
        instructions.get(constInt.block()).add(new ConstIntInstruction(constInt, registerAllocator));
    }

    private JumpInstruction newJumpAlways(Block target) {
        JumpInstruction jump = new JumpAlwaysInstruction(target);
        jump.addNonImmediateSuccessor(instructions.get(target).getFirst());
        return jump;
    }

    private JumpInstruction newJumpZero(Block target, Node condition) {
        JumpInstruction jump = new JumpZeroInstruction(
                target, registerAllocator.get(condition)
        );
        jump.addNonImmediateSuccessor(instructions.get(target).getFirst());
        return jump;
    }

    private JumpInstruction newJumpNonZero(Block target, Node condition) {
        JumpInstruction jump = new JumpNonZeroInstruction(
                target, registerAllocator.get(condition)
        );
        jump.addNonImmediateSuccessor(instructions.get(target).getFirst());
        return jump;
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
        for (Block block : blocks) {
            for (Instruction instruction : instructions.get(block)) {
                instruction.markUsedAsLive();
            }
        }

        boolean changes = true;
        while (changes) {
            changes = false;
            Instruction next = null;
            for (int i = blocks.size() - 1; i >= 0; --i) {
                List<Instruction> instructionList = instructions.get(blocks.get(i));
                for (int j = instructionList.size() - 1; j >= 0; --j) {
                    Instruction instruction = instructionList.get(j);
                    boolean changesForInstruction = instruction.deduceLiveness(next);
                    changes = changes || changesForInstruction;
                    next = instruction;
                }
            }
        }
    }

    public InterferenceGraph buildInterferenceGraph() {
        InterferenceGraph interferenceGraph = new InterferenceGraph();
        for (VirtualRegister virtualRegister : registerAllocator.usedRegisters()) {
            interferenceGraph.addRegister(virtualRegister);
        }

        Instruction next = null;
        for (int i = blocks.size() - 1; i >= 0; --i) {
            List<Instruction> instructionList = instructions.get(blocks.get(i));
            for (int j = instructionList.size() - 1; j >= 0; --j) {
                Instruction instruction = instructionList.get(j);
                instruction.addEdges(interferenceGraph, next);
                next = instruction;
            }
        }
        return interferenceGraph;
    }
}
