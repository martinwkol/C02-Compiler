package edu.kit.kastel.vads.compiler.parser.visitor;

import edu.kit.kastel.vads.compiler.parser.ast.*;

/// A visitor that traverses a tree in postorder
/// @param <T> a type for additional data
public class RecursivePostorderVisitor<T> extends RecursiveVisitor<T, Unit> implements Visitor<T, Unit> {

    public RecursivePostorderVisitor(Visitor<T, Unit> visitor) {
        super(new NoOpVisitor<>() {}, visitor);
    }

}
