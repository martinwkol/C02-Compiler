package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;
import edu.kit.kastel.vads.compiler.ir.node.ReturnNode;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public final class ReturnInstruction extends Instruction {
    private final Register returnRegister;

    public ReturnInstruction(ReturnNode node, RegisterAllocator registerAllocator) {
        super(false);
        returnRegister = registerAllocator.get(predecessorSkipProj(node, ReturnNode.RESULT));
        addUses(returnRegister);
        addDefines(PhysicalRegister.Return);
    }

    public Register getReturnRegister(RegisterMapping registerMapping) {
        return registerMapping.get(returnRegister);
    }
}
