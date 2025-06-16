package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;

public abstract sealed class ShiftInstruction extends Instruction permits
    ShiftLeftInstruction, ShiftRightInstruction
{
    private final Register destination;
    private final Register source;

    public ShiftInstruction(Register source, Register destination) {
        super(true);
        this.destination = destination;
        this.source = source;
        addDefines(destination);
        addUses(source);
        addUses(PhysicalRegister.ShiftRegister);
    }

    public Register getDestination(RegisterMapping registerMapping) {
        return registerMapping.get(destination);
    }

    public Register getSource(RegisterMapping registerMapping) {
        return registerMapping.get(source);
    }
}
