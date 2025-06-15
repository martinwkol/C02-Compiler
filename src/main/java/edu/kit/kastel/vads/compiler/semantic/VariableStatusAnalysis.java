package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.lexer.Operator;
import edu.kit.kastel.vads.compiler.parser.ast.*;
import edu.kit.kastel.vads.compiler.parser.symbol.Name;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;
import edu.kit.kastel.vads.compiler.util.Pair;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Stack;

/// Checks that variables are
/// - declared before assignment
/// - not declared twice
/// - not initialized twice
/// - assigned before referenced
class VariableStatusAnalysis  {

    static class VariableStatus {
        private final LinkedHashSet<Name> declared;
        private final LinkedHashSet<Name> initialized;
        private final Stack<Pair<Integer, Integer>> blockStart;

        public VariableStatus() {
            declared = new LinkedHashSet<>();
            initialized = new LinkedHashSet<>();
            blockStart = new Stack<>();
        }

        public boolean declare(Name name) {
            return declared.add(name);
        }

        public boolean initialize(Name name) {
            return initialized.add(name);
        }

        public boolean isDeclared(Name name) {
            return declared.contains(name);
        }

        public boolean isInitialized(Name name) {
            return initialized.contains(name);
        }

        public void newBlock() {
            blockStart.push(new Pair<>(declared.size(), initialized.size()));
        }

        public void endBlock() {
            Pair<Integer, Integer> start = blockStart.pop();
            while (declared.size() > start.first()) declared.removeLast();
            while (initialized.size() > start.second()) initialized.removeLast();
        }
    };

    static class PreorderVisitor implements NoOpVisitor<VariableStatus> {
        @Override
        public Unit visit(BlockTree blockTree, VariableStatus data) {
            data.newBlock();
            return NoOpVisitor.super.visit(blockTree, data);
        }

        @Override
        public Unit visit(AssignmentTree assignmentTree, VariableStatus data) {
            switch (assignmentTree.lValue()) {
                case LValueIdentTree(var name) -> {
                    if (assignmentTree.operator().type() == Operator.OperatorType.ASSIGN) {
                        checkDeclared(name, data);
                    } else {
                        checkInitialized(name, data);
                    }
                    data.initialize(name.name());
                }
            }
            return NoOpVisitor.super.visit(assignmentTree, data);
        }

        @Override
        public Unit visit(DeclarationTree declarationTree, VariableStatus data) {
            checkUndeclared(declarationTree.name(), data);
            data.declare(declarationTree.name().name());
            if (declarationTree.initializer() != null) {
                data.initialize(declarationTree.name().name());
            }
            return NoOpVisitor.super.visit(declarationTree, data);
        }

        @Override
        public Unit visit(IdentExpressionTree identExpressionTree, VariableStatus data) {
            checkInitialized(identExpressionTree.name(), data);
            return NoOpVisitor.super.visit(identExpressionTree, data);
        }

        private static void checkDeclared(NameTree name, VariableStatus data) {
            if (!data.isDeclared(name.name())) {
                throw new SemanticException("Variable " + name + " must be declared before assignment");
            }
        }

        private static void checkInitialized(NameTree name, VariableStatus data) {
            if (!data.isInitialized(name.name())) {
                throw new SemanticException("Variable " + name + " must be initialized before use");
            }
        }

        private static void checkUndeclared(NameTree name, VariableStatus data) {
            if (data.isDeclared(name.name())) {
                throw new SemanticException("Variable " + name + " is already declared");
            }
        }
    }

    static class PostorderVisitor implements NoOpVisitor<VariableStatus> {
        @Override
        public Unit visit(BlockTree blockTree, VariableStatus data) {
            data.endBlock();
            return NoOpVisitor.super.visit(blockTree, data);
        }
    }
}
