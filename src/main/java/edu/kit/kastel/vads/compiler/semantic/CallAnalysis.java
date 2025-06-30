package edu.kit.kastel.vads.compiler.semantic;

import edu.kit.kastel.vads.compiler.parser.ast.CallTree;
import edu.kit.kastel.vads.compiler.parser.type.FunctionType;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;

public class CallAnalysis implements NoOpVisitor<Namespace<Void>> {
    private final Namespace<FunctionType> functionTypeNamespace;

    public CallAnalysis(Namespace<FunctionType> functionTypeNamespace) {
        this.functionTypeNamespace = functionTypeNamespace;
    }

    @Override
    public Unit visit(CallTree callTree, Namespace<Void> data) {
        if (functionTypeNamespace.get(callTree.functionName().name()) == null) {
            throw new SemanticException("No function named " + callTree.functionName().name());
        }
        return NoOpVisitor.super.visit(callTree, data);
    }
}
