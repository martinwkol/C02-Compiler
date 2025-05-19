package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.AddNode;

public class AddInstruction extends BinaryOperationInstruction {
    public AddInstruction(AddNode node, RegisterAllocator registerAllocator) {
        super(node, registerAllocator);
    }
}
