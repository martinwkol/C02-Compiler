package edu.kit.kastel.vads.compiler.parser.ast;

import edu.kit.kastel.vads.compiler.Span;
import edu.kit.kastel.vads.compiler.lexer.Operator;
import edu.kit.kastel.vads.compiler.parser.visitor.Visitor;

public record NegateTree(ExpressionTree expression, Operator.OperatorType operator, Span negationPos) implements ExpressionTree {
    @Override
    public Span span() {
        return negationPos().merge(expression().span());
    }

    @Override
    public <T, R> R accept(Visitor<T, R> visitor, T data) {
        return visitor.visit(this, data);
    }
}
