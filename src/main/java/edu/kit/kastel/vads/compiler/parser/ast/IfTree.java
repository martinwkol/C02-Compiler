package edu.kit.kastel.vads.compiler.parser.ast;

import edu.kit.kastel.vads.compiler.Position;
import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.parser.visitor.Visitor;
import org.jspecify.annotations.Nullable;

public record IfTree(ExpressionTree condition, StatementTree caseTrue, @Nullable StatementTree caseFalse, Position start) implements StatementTree {
    @Override
    public Span span() {
        if (caseFalse != null) {
            return new Span.SimpleSpan(start, caseFalse.span().end());
        }
        return new Span.SimpleSpan(start, caseTrue.span().end());
    }

    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T data) {
        return visitor.visit(this, data);
    }
}
