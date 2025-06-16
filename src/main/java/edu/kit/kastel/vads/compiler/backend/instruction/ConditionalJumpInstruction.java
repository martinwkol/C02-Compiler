package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterMapping;
import edu.kit.kastel.vads.compiler.ir.node.Block;

public abstract sealed class ConditionalJumpInstruction extends Instruction permits
        JumpZeroInstruction, JumpNonZeroInstruction
{
    private final LabelInstruction target;
    private final Register register;

    public ConditionalJumpInstruction(LabelInstruction target, Register register) {
        super(true);
        this.target = target;
        this.register = register;
        addUses(register);
        addNonImmediateSuccessor(target);
    }

    public LabelInstruction target() {
        return this.target;
    }

    public Register register(RegisterMapping registerMapping) {
        return registerMapping.get(this.register);
    }

}
