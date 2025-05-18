package edu.kit.kastel.vads.compiler.backend.register;

import edu.kit.kastel.vads.compiler.ir.node.Node;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public interface RegisterAllocator {
    Register get(Node node);
    @Nullable
    Register getNullable(Node node);

    Set<Node> nodes();

    int requiredStackSize();
}
