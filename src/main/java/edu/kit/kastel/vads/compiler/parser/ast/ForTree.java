package edu.kit.kastel.vads.compiler.parser.ast;

import edu.kit.kastel.vads.compiler.Position;
import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.parser.visitor.Visitor;
import org.jspecify.annotations.Nullable;

public record ForTree(@Nullable StatementTree initializer, ExpressionTree condition, @Nullable StatementTree step, StatementTree body, Position start) implements StatementTree {
    @Override
    public Span span() {
        return new Span.SimpleSpan(start, body().span().end());
    }

    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T data) {
        return visitor.visit(this, data);
    }
}
