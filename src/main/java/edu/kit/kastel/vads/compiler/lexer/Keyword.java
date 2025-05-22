package edu.kit.kastel.vads.compiler.lexer;

import edu.kit.kastel.vads.compiler.Span;

import java.util.List;

public record Keyword(KeywordType type, Span span) implements Token {
    @Override
    public boolean isKeyword(KeywordType... keywordType) {
        return List.of(keywordType).contains(type());
    }

    @Override
    public String asString() {
        return type().keyword();
    }
}
