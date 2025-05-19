package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.MulNode;

public class MulInstruction extends BinaryOperationInstruction {
    public MulInstruction(MulNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
