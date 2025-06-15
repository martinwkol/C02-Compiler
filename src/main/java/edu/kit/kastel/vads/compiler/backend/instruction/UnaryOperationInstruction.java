package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;
import edu.kit.kastel.vads.compiler.ir.node.UnaryOperationNode;

public sealed class UnaryOperationInstruction extends Instruction permits
    BitNegationInstruction, LogNegationInstruction
{
    private final Register destination;
    private final Register source;

    public UnaryOperationInstruction(UnaryOperationNode unary, RegisterAllocator registerAllocator) {
        super(true);
        this.destination = registerAllocator.get(unary);
        this.source = registerAllocator.get(unary.node());
        addDefines(destination);
        addUses(source);
    }

    public Register getDestination(RegisterMapping registerMapping) {
        return registerMapping.get(destination);
    }

    public Register getSource(RegisterMapping registerMapping) {
        return registerMapping.get(source);
    }
}
