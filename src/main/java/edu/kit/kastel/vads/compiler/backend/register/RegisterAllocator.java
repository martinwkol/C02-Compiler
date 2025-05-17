package edu.kit.kastel.vads.compiler.backend.register;

import edu.kit.kastel.vads.compiler.ir.node.Node;
import org.jspecify.annotations.Nullable;

public interface RegisterAllocator {

    @Nullable
    Register allocateRegister(Node node);
    Register get(Node node);
}
