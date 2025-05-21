package edu.kit.kastel.vads.compiler.parser.ast;

import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.parser.visitor.Visitor;

public record TernaryConditionalTree(ExpressionTree condition, ExpressionTree caseTrue, ExpressionTree caseFalse) implements StatementTree {
    @Override
    public Span span() {
        return condition.span().merge(caseFalse.span());
    }

    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T data) {
        return visitor.visit(this, data);
    }
}
