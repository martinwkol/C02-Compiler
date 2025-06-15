package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.BitAndNode;

public final class BitAndInstruction extends BinaryOperationInstruction {
    public BitAndInstruction(BitAndNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
