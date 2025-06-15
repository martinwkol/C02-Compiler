package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.BitNegationNode;

public final class BitNegationInstruction extends UnaryOperationInstruction {
    public BitNegationInstruction(BitNegationNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
