package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.CSmallerNode;

public final class SmallerInstruction extends BinaryOperationInstruction {
    public SmallerInstruction(CSmallerNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
