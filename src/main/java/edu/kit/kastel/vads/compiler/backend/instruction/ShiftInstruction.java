package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;

public abstract sealed class ShiftInstruction extends Instruction permits
    ShiftLeftInstruction, ShiftRightInstruction
{
    private final Register destination;

    public ShiftInstruction(Register destination) {
        super(true);
        this.destination = destination;
        addDefines(destination);
        addUses(destination);
        addUses(PhysicalRegister.ShiftRegister);
    }

    public Register getDestination(RegisterMapping registerMapping) {
        return registerMapping.get(destination);
    }
}
