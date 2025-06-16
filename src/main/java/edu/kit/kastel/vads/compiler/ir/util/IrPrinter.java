package edu.kit.kastel.vads.compiler.ir.util;

import edu.kit.kastel.vads.compiler.backend.register.VirtualRegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.*;

import java.util.*;

public class IrPrinter {
    private final List<Block> blocks = new ArrayList<>();
    private final VirtualRegisterAllocator registerAllocator;
    private final Map<Block, StringBuilder> builder = new HashMap<>();

    public static String printIr(IrGraph graph) {
        IrPrinter printer = new IrPrinter(graph);
        StringBuilder combined = new StringBuilder();
        for (Block block : printer.blocks) {
            combined.append(printer.builder.get(block));
        }
        return combined.toString();
    }

    private IrPrinter(IrGraph graph) {
        this.registerAllocator = new VirtualRegisterAllocator();
        allocateRegisters(graph.endBlock());
        scanBlocks(graph.endBlock());
        initBlocks();
        scanInstructions(graph.endBlock());
        endBlocks();
    }

    private void allocateRegisters(Block endBlock) {
        Set<Node> visited = new HashSet<>();
        visited.add(endBlock);
        allocateRegistersRecursive(endBlock, visited);
    }

    private void allocateRegistersRecursive(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                allocateRegistersRecursive(predecessor, visited);
            }
        }
        registerAllocator.allocateRegister(node);
        if (node instanceof Block block) {
            if (block.exitNode() != null) allocateRegistersRecursive(block.exitNode(), visited);
        }
    }

    private void scanBlocks(Block endBlock) {
        Set<Node> visited = new HashSet<>();
        visited.add(endBlock);
        scanBlocksRecursive(endBlock, visited);
    }

    private void scanBlocksRecursive(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scanBlocksRecursive(predecessor, visited);
            }
        }
        if (node instanceof Block block) newBlock(block, visited);
    }

    private void initBlocks() {
        for (Map.Entry<Block, StringBuilder> entry : builder.entrySet()) {
            Block block = entry.getKey();
            StringBuilder s = entry.getValue();
            s.append(String.format("Block %d [ ", blocks.indexOf(block)));
            for (Node predecessor : block.predecessors()) {
                if (predecessor instanceof Block blockPre) {
                    s.append(String.format("%d ", blocks.indexOf(blockPre)));
                }
            }
            s.append("]\n");
        }
    }

    private void endBlocks() {
        for (Map.Entry<Block, StringBuilder> entry : builder.entrySet()) {
            Block block = entry.getKey();
            StringBuilder s = entry.getValue();
            ExitNode exitNode = block.exitNode();
            if (exitNode != null) {
                if (exitNode instanceof JumpNode jmp) jump(jmp);
                else if (exitNode instanceof IfNode ifN) ifNode(ifN);
                else if (exitNode instanceof ReturnNode ret) newReturn(ret);
            }
            else {
                s.append("no exit\n");
            }
            s.append("\n\n");
        }
    }

    private void scanInstructions(Block endBlock) {
        Set<Node> visited = new HashSet<>();
        visited.add(endBlock);
        scanInstructionsRecursive(endBlock, visited);
    }

    private void scanInstructionsRecursive(Node node, Set<Node> visited) {
        for (Node predecessor : node.predecessors()) {
            if (visited.add(predecessor)) {
                scanInstructionsRecursive(predecessor, visited);
            }
        }

        switch (node) {
            case Block block                    -> {}
            case AddNode add                    -> binary(add, "add");
            case SubNode sub                    -> binary(sub, "sub");
            case MulNode mul                    -> binary(mul, "mul");
            case DivNode div                    -> binary(div, "div");
            case ModNode mod                    -> binary(mod, "mod");

            case BitAndNode bitAnd              -> binary(bitAnd, "and");
            case BitOrNode bitOr                -> binary(bitOr, "or");
            case BitXorNode bitXor              -> binary(bitXor, "xor");
            case BitNegationNode bitNegation    -> unary(bitNegation, "not");

            case ShiftLeftNode shiftLeft        -> binary(shiftLeft, "shiftl");
            case ShiftRightNode shiftRight      -> binary(shiftRight, "shiftr");

            case CEqualsNode equals             -> binary(equals, "eq");
            case CUnequalsNode unequals         -> binary(unequals, "ueq");
            case CSmallerNode smaller           -> binary(smaller, "le");
            case CSmallerEqNode smallerEq       -> binary(smallerEq, "leq");
            case CBiggerNode bigger             -> binary(bigger, "ge");
            case CBiggerEqNode biggerEq         -> binary(biggerEq, "geq");
            case LogNegationNode logNegation    -> unary(logNegation, "!");

            case ConstBoolNode cBool            -> constBool(cBool);
            case ConstIntNode cInt              -> constInt(cInt);

            case ReturnNode ret                 -> {}
            case JumpNode _, IfNode _           -> {} // handle jumps later
            case Phi phi                        -> newPhi(phi);
            case ProjNode _, StartNode _        -> {}

            case InvalidNode inv                -> invalid(inv); // tolerate it for now
            //case InvalidNode _                  -> throw new UnsupportedOperationException("Invalid node");
        }
    }

    private void binary(BinaryOperationNode node, String opName) {
        builder.get(node.block()).append(String.format(
                "%s = %s %s %s\n",
                registerAllocator.get(node).registerName(),
                opName,
                registerAllocator.get(node.left()).registerName(),
                registerAllocator.get(node.right()).registerName()
        ));
    }

    private void unary(UnaryOperationNode node, String opName) {
        builder.get(node.block()).append(String.format(
                "%s = %s %s\n",
                registerAllocator.get(node).registerName(),
                opName,
                registerAllocator.get(node.node()).registerName()
        ));
    }

    private void constInt(ConstIntNode constInt) {
        builder.get(constInt.block()).append(String.format(
                "%s = int %d\n",
                registerAllocator.get(constInt).registerName(),
                constInt.value()
        ));
    }

    private void constBool(ConstBoolNode constBool) {
        builder.get(constBool.block()).append(String.format(
                "%s = int %s\n",
                registerAllocator.get(constBool).registerName(),
                constBool.value() ? "true" : "false"
        ));
    }

    private void newReturn(ReturnNode ret) {
        builder.get(ret.block()).append(String.format(
                "ret %s\n",
                registerAllocator.get(ret.predecessor(ReturnNode.RESULT)
        )));
    }

    private void newPhi(Phi phi) {
        StringBuilder pb = builder.get(phi.block());
        pb.append(String.format("%s = phi( ", registerAllocator.get(phi).registerName()));
        for (Node operand : phi.operands()) {
            pb.append(String.format("%s ", registerAllocator.get(operand).registerName()));
        }
        pb.append(")\n");
    }

    private void invalid(InvalidNode inv) {
        builder.get(inv.block()).append("invalid\n");
    }

    private void jump(JumpNode jumpNode) {
        builder.get(jumpNode.block()).append(String.format(
                "jmp %d\n",
                blocks.indexOf(jumpNode.targetBlock())
        ));
    }

    private void ifNode(IfNode ifN) {
        builder.get(ifN.block()).append(String.format(
                "if %s then %d else %d\n",
                registerAllocator.get(ifN.condition()).registerName(),
                blocks.indexOf(ifN.trueEntry()),
                blocks.indexOf(ifN.falseEntry())
        ));
    }

    private void newBlock(Block block, Set<Node> visited) {
        blocks.add(block);
        builder.put(block, new StringBuilder());
        // maybe unnecessary
        if (block.exitNode() != null) scanBlocksRecursive(block.exitNode(), visited);
    }
}
