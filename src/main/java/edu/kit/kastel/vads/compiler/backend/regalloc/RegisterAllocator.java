package edu.kit.kastel.vads.compiler.backend.regalloc;

import edu.kit.kastel.vads.compiler.ir.IrGraph;
import edu.kit.kastel.vads.compiler.ir.node.Node;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface RegisterAllocator {

    @Nullable
    Register allocateRegister(Node node);
    Register get(Node node);
}
