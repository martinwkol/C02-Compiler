package edu.kit.kastel.vads.compiler.semantic;

import java.util.List;
import java.util.stream.Collectors;

import edu.kit.kastel.vads.compiler.parser.ast.FunctionTree;
import edu.kit.kastel.vads.compiler.parser.type.FunctionType;
import edu.kit.kastel.vads.compiler.parser.type.Type;
import edu.kit.kastel.vads.compiler.parser.visitor.NoOpVisitor;
import edu.kit.kastel.vads.compiler.parser.visitor.Unit;

public class FunctionDefinitionAnalysis implements NoOpVisitor<Namespace<FunctionType>> {
    public Unit visit(FunctionTree functionTree, Namespace<FunctionType> data) {
        if (data.get(functionTree.name()) == null) {
            throw new SemanticException("function " + functionTree.name().name() + " already defined");
        }
        List<Type> parameterTypes = functionTree.parameters().stream().map(param -> param.type().type()).collect(Collectors.toList());
        data.put(functionTree.name(), new FunctionType(functionTree.returnType().type(), parameterTypes));
        return NoOpVisitor.super.visit(functionTree, data);
    }
}
