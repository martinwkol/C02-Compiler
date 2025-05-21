package edu.kit.kastel.vads.compiler.parser.ast;

import edu.kit.kastel.vads.compiler.Position;
import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.parser.visitor.Visitor;
import org.jspecify.annotations.Nullable;

public record IfTree(ExpressionTree condition, StatementTree conditionTrue, @Nullable StatementTree conditionFalse, Position start) implements StatementTree {
    @Override
    public Span span() {
        if (conditionFalse != null) {
            return new Span.SimpleSpan(start, conditionFalse.span().end());
        }
        return new Span.SimpleSpan(start, conditionTrue.span().end());
    }

    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T data) {
        return visitor.visit(this, data);
    }
}
