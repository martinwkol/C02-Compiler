package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.ShiftLeftNode;

public final class ShiftLeftInstruction extends BinaryOperationInstruction {
    public ShiftLeftInstruction(ShiftLeftNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}