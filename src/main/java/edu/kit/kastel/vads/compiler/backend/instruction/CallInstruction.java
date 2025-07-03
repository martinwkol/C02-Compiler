package edu.kit.kastel.vads.compiler.backend.instruction;

import java.util.List;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;

public final class CallInstruction extends Instruction {
    private final List<Register> parameters;

    public CallInstruction(String functionName, List<Register> parameters) {
        super(true);
        addDefines(PhysicalRegister.Return);
        for (Register parameter : parameters) {
            addUses(parameter);
        }
        this.parameters = parameters;
    }

    public Register getParameter(RegisterMapping registerMapping, int index) {
        return registerMapping.get(parameters.get(index));
    }
}
