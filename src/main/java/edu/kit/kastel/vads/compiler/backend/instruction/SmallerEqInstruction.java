package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.CSmallerEqNode;

public final class SmallerEqInstruction extends BinaryOperationInstruction {
    public SmallerEqInstruction(CSmallerEqNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
