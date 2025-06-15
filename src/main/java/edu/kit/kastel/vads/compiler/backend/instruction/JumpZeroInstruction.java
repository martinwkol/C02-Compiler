package edu.kit.kastel.vads.compiler.backend.instruction;

import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.ir.node.Block;

public final class JumpZeroInstruction extends JumpInstruction {
    private final Register register;

    public JumpZeroInstruction(Block target, Register register) {
        super(target);
        this.register = register;
        addUses(register);
    }

    public Register register() {
        return this.register;
    }
}
