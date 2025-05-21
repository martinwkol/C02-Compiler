package edu.kit.kastel.vads.compiler.parser.visitor;

import edu.kit.kastel.vads.compiler.parser.ast.*;

/// A visitor that traverses a tree in postorder
/// @param <T> a type for additional data
/// @param <R> a type for a return type
public class RecursivePostorderVisitor<T, R> implements Visitor<T, R> {
    private final Visitor<T, R> visitor;

    public RecursivePostorderVisitor(Visitor<T, R> visitor) {
        this.visitor = visitor;
    }

    @Override
    public R visit(AssignmentTree assignmentTree, T data) {
        R r = assignmentTree.lValue().accept(this, data);
        r = assignmentTree.expression().accept(this, accumulate(data, r));
        r = this.visitor.visit(assignmentTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(BinaryOperationTree binaryOperationTree, T data) {
        R r = binaryOperationTree.lhs().accept(this, data);
        r = binaryOperationTree.rhs().accept(this, accumulate(data, r));
        r = this.visitor.visit(binaryOperationTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(BlockTree blockTree, T data) {
        R r;
        T d = data;
        for (StatementTree statement : blockTree.statements()) {
            r = statement.accept(this, d);
            d = accumulate(d, r);
        }
        r = this.visitor.visit(blockTree, d);
        return r;
    }

    @Override
    public R visit(DeclarationTree declarationTree, T data) {
        R r = declarationTree.type().accept(this, data);
        r = declarationTree.name().accept(this, accumulate(data, r));
        if (declarationTree.initializer() != null) {
            r = declarationTree.initializer().accept(this, accumulate(data, r));
        }
        r = this.visitor.visit(declarationTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(FunctionTree functionTree, T data) {
        R r = functionTree.returnType().accept(this, data);
        r = functionTree.name().accept(this, accumulate(data, r));
        r = functionTree.body().accept(this, accumulate(data, r));
        r = this.visitor.visit(functionTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(IdentExpressionTree identExpressionTree, T data) {
        R r = identExpressionTree.name().accept(this, data);
        r = this.visitor.visit(identExpressionTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(IfTree ifTree, T data) {
        R r = ifTree.condition().accept(this, data);
        r = ifTree.conditionTrue().accept(this, accumulate(data, r));
        if (ifTree.conditionFalse() != null) {
            r = ifTree.conditionFalse().accept(this, accumulate(data, r));
        }
        r = this.visitor.visit(ifTree, accumulate(data, r));
        return r;
    }

    public R visit(WhileTree whileTree, T data) {
        R r = whileTree.condition().accept(this, data);
        r = whileTree.body().accept(this, accumulate(data, r));
        r = this.visitor.visit(whileTree, accumulate(data, r));
        return r;
    }

    public R visit(ForTree forTree, T data) {
        R r = null;
        if (forTree.initializer() != null) {
            r = forTree.initializer().accept(this, data);
        }
        r = forTree.condition().accept(this, r == null ? data : accumulate(data, r));
        if (forTree.step() != null) {
            r = forTree.step().accept(this, accumulate(data, r));
        }
        r = forTree.body().accept(this, accumulate(data, r));
        r = this.visitor.visit(forTree, accumulate(data, r));
        return r;
    }

    public R visit(BreakTree breakTree, T data) {
        return this.visitor.visit(breakTree, data);
    }

    public R visit(ContinueTree continueTree, T data) {
        return this.visitor.visit(continueTree, data);
    }

    public R visit(TernaryConditionTree ternaryConditionTree, T data) {
        R r = ternaryConditionTree.condition().accept(this, data);
        r = ternaryConditionTree.caseTrue().accept(this, accumulate(data, r));
        r = ternaryConditionTree.caseFalse().accept(this, accumulate(data, r));
        r = this.visitor.visit(ternaryConditionTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(LiteralTree literalTree, T data) {
        return this.visitor.visit(literalTree, data);
    }

    @Override
    public R visit(LValueIdentTree lValueIdentTree, T data) {
        R r = lValueIdentTree.name().accept(this, data);
        r = this.visitor.visit(lValueIdentTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(NameTree nameTree, T data) {
        return this.visitor.visit(nameTree, data);
    }

    @Override
    public R visit(NegateTree negateTree, T data) {
        R r = negateTree.expression().accept(this, data);
        r = this.visitor.visit(negateTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(ProgramTree programTree, T data) {
        R r;
        T d = data;
        for (FunctionTree tree : programTree.topLevelTrees()) {
            r = tree.accept(this, d);
            d = accumulate(data, r);
        }
        r = this.visitor.visit(programTree, d);
        return r;
    }

    @Override
    public R visit(ReturnTree returnTree, T data) {
        R r = returnTree.expression().accept(this, data);
        r = this.visitor.visit(returnTree, accumulate(data, r));
        return r;
    }

    @Override
    public R visit(TypeTree typeTree, T data) {
        return this.visitor.visit(typeTree, data);
    }

    protected T accumulate(T data, R value) {
        return data;
    }
}
