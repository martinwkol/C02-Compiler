package edu.kit.kastel.vads.compiler.parser;

import edu.kit.kastel.vads.compiler.lexer.Operator;
import edu.kit.kastel.vads.compiler.parser.ast.*;

import java.util.List;

/// This is a utility class to help with debugging the parser.
public class Printer {

    private final Tree ast;
    private final StringBuilder builder = new StringBuilder();
    private boolean requiresIndent;
    private int indentDepth;
    private boolean ignoreSemicolon = false;

    public Printer(Tree ast) {
        this.ast = ast;
    }

    public static String print(Tree ast) {
        Printer printer = new Printer(ast);
        printer.printRoot();
        return printer.builder.toString();
    }

    private void printRoot() {
        printTree(this.ast);
    }

    private void printTree(Tree tree) {
        switch (tree) {
            case BlockTree(List<StatementTree> statements, _) -> {
                print("{");
                lineBreak();
                this.indentDepth++;
                for (StatementTree statement : statements) {
                    printTree(statement);
                }
                this.indentDepth--;
                print("}");
            }
            case FunctionTree(var returnType, var name, var body) -> {
                printTree(returnType);
                space();
                printTree(name);
                print("()");
                space();
                printTree(body);
            }
            case NameTree(var name, _) -> print(name.asString());
            case ProgramTree(var topLevelTrees) -> {
                for (FunctionTree function : topLevelTrees) {
                    printTree(function);
                    lineBreak();
                }
            }
            case TypeTree(var type, _) -> print(type.asString());
            case BinaryOperationTree(var lhs, var rhs, var op) -> binaryOperation(lhs, rhs, op);
            case IntLiteralTree(var value, _, _) -> this.builder.append(value);
            case BoolLiteralTree(boolean value, _) -> this.builder.append(value);
            case UnaryOperatorTree(ExpressionTree expression, Operator.OperatorType negationOp, _) -> {
                this.builder.append(negationOp);
                print("(");
                printTree(expression);
                print(")");
            }
            case AssignmentTree(var lValue, var op, var expression) -> {
                printTree(lValue);
                space();
                this.builder.append(op);
                space();
                printTree(expression);
                semicolon();
            }
            case DeclarationTree(var type, var name, var initializer) -> {
                printTree(type);
                space();
                printTree(name);
                if (initializer != null) {
                    print(" = ");
                    printTree(initializer);
                }
                semicolon();
            }
            case IfTree(ExpressionTree condition, StatementTree conditionTrue, StatementTree conditionFalse, _) -> {
                print("if (");
                printTree(condition);
                print(") ");
                print(conditionTrue);
                if (conditionFalse != null) {
                    print("else ");
                    print(conditionFalse);
                }
            }
            case WhileTree(ExpressionTree condition, StatementTree body, _) -> {
                print("while (");
                printTree(condition);
                print(") ");
                print(body);
            }
            case ForTree(StatementTree initializer, ExpressionTree condition, StatementTree step, StatementTree body, _) -> {
                ignoreSemicolon = true;
                print("for (");
                if (initializer != null) printTree(initializer);
                print("; ");
                printTree(condition);
                print("; ");
                if (step != null) print(step);
                print(") ");
                ignoreSemicolon = false;
                print(body);
            }
            case BreakTree(_) -> {
                print("break");
                semicolon();
            }
            case ContinueTree(_) -> {
                print("continue");
                semicolon();
            }
            case TernaryConditionTree(ExpressionTree condition, ExpressionTree caseTrue, ExpressionTree caseFalse) -> {
                printTree(condition);
                print(" ? ");
                printTree(caseTrue);
                print(" : ");
                printTree(caseFalse);
            }
            case ReturnTree(var expr, _) -> {
                print("return ");
                printTree(expr);
                semicolon();
            }
            case LValueIdentTree(var name) -> printTree(name);
            case IdentExpressionTree(var name) -> printTree(name);
        }
    }

    private void binaryOperation(ExpressionTree lhs, ExpressionTree rhs, Operator.OperatorType op) {
        print("(");
        printTree(lhs);
        print(")");
        space();
        this.builder.append(op);
        space();
        print("(");
        printTree(rhs);
        print(")");
    }

    private void print(String str) {
        if (this.requiresIndent) {
            this.requiresIndent = false;
            this.builder.append(" ".repeat(4 * this.indentDepth));
        }
        this.builder.append(str);
    }

    private void lineBreak() {
        this.builder.append("\n");
        this.requiresIndent = true;
    }

    private void semicolon() {
        if (ignoreSemicolon) return;
        this.builder.append(";");
        lineBreak();
    }

    private void space() {
        this.builder.append(" ");
    }

}
