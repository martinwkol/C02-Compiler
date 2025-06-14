package edu.kit.kastel.vads.compiler.ir.util;

import edu.kit.kastel.vads.compiler.ir.node.Block;

public record LoopInfo(Block bodyExit, Block loopExit) {
}
