package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.ShiftRightNode;

public final class ShiftRightInstruction extends BinaryOperationInstruction {
    public ShiftRightInstruction(ShiftRightNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}