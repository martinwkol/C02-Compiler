package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;

public class MoveInstruction extends Instruction {
    private final Register source;
    private final Register destination;

    public MoveInstruction(Register source, Register destination) {
        super(true);
        this.source = source;
        this.destination = destination;
        addUses(source);
        addDefines(destination);
    }

    public Register getSource(RegisterMapping registerMapping) {
        return registerMapping.get(source);
    }

    public Register getDestination(RegisterMapping registerMapping) {
        return registerMapping.get(destination);
    }
}
