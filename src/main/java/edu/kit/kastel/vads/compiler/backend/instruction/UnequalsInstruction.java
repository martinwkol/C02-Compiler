package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.CUnequalsNode;

public final class UnequalsInstruction extends BinaryOperationInstruction {
    public UnequalsInstruction(CUnequalsNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
