package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;

public final class ParameterInstruction extends Instruction {
    private final Register destination;
    private final int index;

    public ParameterInstruction(Register destination, int index) {
        super(true);
        addDefines(destination);
        this.destination = destination;
        this.index = index;
    }

    public Register getDestination(RegisterMapping registerMapping) {
        return registerMapping.get(destination);
    }

    public int index() {
        return index;
    }
}
