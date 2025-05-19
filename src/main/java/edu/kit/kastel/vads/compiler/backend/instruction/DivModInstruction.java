package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.ir.node.BinaryOperationNode;
import edu.kit.kastel.vads.compiler.ir.node.DivNode;
import edu.kit.kastel.vads.compiler.ir.node.ModNode;

public class DivModInstruction extends Instruction {
    public DivModInstruction() {
        super(true);

        addUses(PhysicalRegister.DividendLS);
        addUses(PhysicalRegister.DividendMS);
        addDefines(PhysicalRegister.Quotient);
        addDefines(PhysicalRegister.Remainder);
    }
}
