package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.SubNode;

public final class SubInstruction extends BinaryOperationInstruction {
    public SubInstruction(SubNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
