package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;

public class DivModInstruction extends Instruction {
    private final Register divisor;

    public DivModInstruction(Register divisor) {
        super(true);
        this.divisor = divisor;

        addUses(PhysicalRegister.DividendLS);
        addUses(PhysicalRegister.DividendMS);
        addDefines(PhysicalRegister.Quotient);
        addDefines(PhysicalRegister.Remainder);
    }

    public Register getDivisor(RegisterMapping registerMapping) {
        return registerMapping.get(divisor);
    }
}
