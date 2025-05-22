package edu.kit.kastel.vads.compiler.parser.ast;

public sealed interface ExpressionTree extends Tree
        permits ArithmeticOperationTree, RelationalOperationTree, BitwiseOperationTree,
        TernaryConditionTree, IdentExpressionTree, LiteralTree, NegateTree {
}
