package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.CEqualsNode;

public final class EqualsInstruction extends BinaryOperationInstruction {
    public EqualsInstruction(CEqualsNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
