package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.BitXorNode;

public final class BitXorInstruction extends BinaryOperationInstruction {
    public BitXorInstruction(BitXorNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
