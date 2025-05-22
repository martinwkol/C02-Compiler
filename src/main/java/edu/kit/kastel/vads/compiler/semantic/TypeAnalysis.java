package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.lexer.Operator;
import edu.kit.kastel.vads.compiler.parser.ast.*;
import edu.kit.kastel.vads.compiler.parser.symbol.Name;
import edu.kit.kastel.vads.compiler.parser.type.BasicType;
import edu.kit.kastel.vads.compiler.parser.type.Type;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;

import java.util.HashMap;
import java.util.Map;

///  Checks type correctness
public class TypeAnalysis implements NoOpVisitor<TypeAnalysis.TypeMapping> {

    public static class TypeMapping {
        private final Map<ExpressionTree, Type> expressionTypeMap;
        private final Map<Name, Type> nameTypeMap;

        public TypeMapping() {
            expressionTypeMap = new HashMap<>();
            nameTypeMap = new HashMap<>();
        }

        public Type get(ExpressionTree expression) {
            return expressionTypeMap.get(expression);
        }

        public void put(ExpressionTree expression, Type type) {
            expressionTypeMap.put(expression, type);
        }

        public Type get(Name name) {
            return nameTypeMap.get(name);
        }

        public void put(Name name, Type type) {
            nameTypeMap.put(name, type);
        }
    }

    @Override
    public Unit visit(DeclarationTree declarationTree, TypeMapping data) {
        Name name = declarationTree.name().name();
        Type type = declarationTree.type().type();
        data.put(name, type);
        if (declarationTree.initializer() != null && data.get(declarationTree.initializer()) != type) {
            throw new SemanticException(String.format("'%s' has type %s but is assigned an expression of type %s",
                    name, type, data.get(declarationTree.initializer()))
            );
        }
        return NoOpVisitor.super.visit(declarationTree, data);
    }

    @Override
    public Unit visit(AssignmentTree assignmentTree, TypeMapping data) {
        LValueTree lValue = assignmentTree.lValue();
        if (!(lValue instanceof LValueIdentTree lValueIdent)) {
            throw new SemanticException(lValue.getClass().getName() + " not supported as LValue");
        }
        Type left = data.get(lValueIdent.name().name());
        Type right = data.get(assignmentTree.expression());
        if (left != right) {
            throw new SemanticException(left.asString() + " does not match " + right.asString());
        }
        if (left == BasicType.BOOL) {
            if (assignmentTree.operator().type() != Operator.OperatorType.ASSIGN) {
                throw new SemanticException(assignmentTree.operator().type() + " is an invalid assignment operator for booleans");
            }
        } else if (left != BasicType.INT) {
            throw new SemanticException("Unsupported type " + left);
        }
        // Int accepts all assignment operators
        return NoOpVisitor.super.visit(assignmentTree, data);
    }

    @Override
    public Unit visit(BinaryOperationTree binaryOperationTree, TypeMapping data) {
        Type left = data.get(binaryOperationTree.lhs());
        Type right = data.get(binaryOperationTree.rhs());
        if (left != right) {
            throw new SemanticException(left.asString() + " does not match " + right.asString());
        }
        if (left == BasicType.BOOL) {
            switch (binaryOperationTree.operatorType()) {
                case EQUALITY, DISEQUALITY, LOGICAL_AND, LOGICAL_OR -> data.put(binaryOperationTree, BasicType.BOOL);
                default -> throw new SemanticException("Operator " + binaryOperationTree.operatorType() + " does not match (boolean, boolean)");
            }
        } else if (left == BasicType.INT) {
            switch (binaryOperationTree.operatorType()) {
                case EQUALITY, DISEQUALITY, SMALLER, SMALLER_EQUAL, BIGGER, BIGGER_EQUAL ->
                    data.put(binaryOperationTree, BasicType.BOOL);
                case PLUS, MINUS, MUL, DIV, MOD, BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT ->
                    data.put(binaryOperationTree, BasicType.INT);
                default -> throw new SemanticException("Operator " + binaryOperationTree.operatorType() + " does not match (int, int)");
            }
        } else {
            throw new SemanticException("Unsupported type " + left);
        }
        return NoOpVisitor.super.visit(binaryOperationTree, data);
    }

    @Override
    public Unit visit(IfTree ifTree, TypeMapping data) {
        if (data.get(ifTree.condition()) != BasicType.BOOL) {
            throw new SemanticException("Non-boolean expression given as condition of if-statement");
        }
        return NoOpVisitor.super.visit(ifTree, data);
    }

    @Override
    public Unit visit(ForTree forTree, TypeMapping data) {
        if (data.get(forTree.condition()) != BasicType.BOOL) {
            throw new SemanticException("Non-boolean expression given as condition of for-loop");
        }
        return NoOpVisitor.super.visit(forTree, data);
    }

    @Override
    public Unit visit(WhileTree whileTree, TypeMapping data) {
        if (data.get(whileTree.condition()) != BasicType.BOOL) {
            throw new SemanticException("Non-boolean expression given as condition of while-loop");
        }
        return NoOpVisitor.super.visit(whileTree, data);
    }
}
