package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;
import edu.kit.kastel.vads.compiler.ir.node.ConstBoolNode;

public final class ConstBoolInstruction extends Instruction {
    private final Register destination;
    private final boolean value;

    public ConstBoolInstruction(ConstBoolNode node, RegisterAllocator registerAllocator) {
        super(true);
        destination = registerAllocator.get(node);
        value = node.value();
        addDefines(destination);
    }

    public Register getDestination(RegisterMapping registerMapping) {
        return registerMapping.get(destination);
    }

    public boolean getValue() {
        return value;
    }
}
