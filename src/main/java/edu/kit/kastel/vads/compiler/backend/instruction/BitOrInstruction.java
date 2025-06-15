package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.BitOrNode;

public final class BitOrInstruction extends BinaryOperationInstruction {
    public BitOrInstruction(BitOrNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
