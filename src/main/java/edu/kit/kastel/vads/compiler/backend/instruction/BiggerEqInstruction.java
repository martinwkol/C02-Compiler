package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.CBiggerEqNode;

public final class BiggerEqInstruction extends BinaryOperationInstruction {
    public BiggerEqInstruction(CBiggerEqNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
