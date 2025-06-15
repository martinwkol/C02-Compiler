package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.LogNegationNode;

public final class LogNegationInstruction extends UnaryOperationInstruction {
    public LogNegationInstruction(LogNegationNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
