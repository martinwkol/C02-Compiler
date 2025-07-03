package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;

public final class ParameterInstruction extends Instruction {
    public ParameterInstruction(Register destination) {
        super(true);
        addDefines(destination);
    }
}
