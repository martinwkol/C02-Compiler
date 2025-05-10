package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;

import java.util.List;

public class DivInstruction extends Instruction {
    private Register divisor;

    public DivInstruction(Register divisor) {
        super(  List.of(PhysicalRegister.Quotient, PhysicalRegister.Remainder),
                List.of(PhysicalRegister.DividendLS, PhysicalRegister.DividendMS, divisor));

        this.divisor = divisor;
    }
}
