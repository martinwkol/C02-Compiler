package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;
import edu.kit.kastel.vads.compiler.ir.node.BinaryOperationNode;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public abstract sealed class BinaryOperationInstruction extends Instruction permits
        AddInstruction, SubInstruction, MulInstruction,
        EqualsInstruction, UnequalsInstruction, SmallerInstruction, SmallerEqInstruction,
        BiggerInstruction, BiggerEqInstruction,
        BitAndInstruction, BitOrInstruction, BitXorInstruction
{
    private final Register destination;
    private final Register left;
    private final Register right;

    public BinaryOperationInstruction(BinaryOperationNode node, RegisterAllocator registerAllocator) {
        super(true);
        destination = registerAllocator.get(node);
        left = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.LEFT));
        right = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT));
        addDefines(destination);
        addUses(left);
        addUses(right);
    }

    public Register getDestination(RegisterMapping registerMapping) {
        return registerMapping.get(destination);
    }

    public Register getLeft(RegisterMapping registerMapping) {
        return registerMapping.get(left);
    }

    public Register getRight(RegisterMapping registerMapping) {
        return registerMapping.get(right);
    }
}
