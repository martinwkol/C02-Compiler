package edu.kit.kastel.vads.compiler.parser.type;

import java.util.List;
import java.util.stream.Collectors;

public record FunctionType(Type returnType, List<Type> parameterTypes) implements Type {
    @Override
    public String asString() {
        return new StringBuilder()
            .append(returnType.toString())
            .append("(")
            .append(parameterTypes.stream().map(type -> type.toString()).collect(Collectors.joining(", ")))
            .append(")")
            .toString();
    }
}
