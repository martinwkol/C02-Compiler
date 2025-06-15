package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.CBiggerNode;

public final class BiggerInstruction extends BinaryOperationInstruction {
    public BiggerInstruction(CBiggerNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
