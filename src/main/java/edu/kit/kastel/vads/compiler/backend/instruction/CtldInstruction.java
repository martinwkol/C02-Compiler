package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;

public class CtldInstruction extends Instruction {
    public CtldInstruction() {
        super(true);
        addUses(PhysicalRegister.DividendLS);
        addDefines(PhysicalRegister.DividendMS);
    }
}
