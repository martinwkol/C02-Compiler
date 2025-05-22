package edu.kit.kastel.vads.compiler.lexer;

import edu.kit.kastel.vads.compiler.Span;

import java.util.List;
import java.util.Objects;

public record Keyword(KeywordType type, Span span) implements Token {
    @Override
    public boolean isKeyword(KeywordType keywordType) {
        return type() == keywordType;
    }

    @Override
    public boolean isKeyword(KeywordType... keywordType) {
        return List.of(keywordType).contains(type());
    }

    @Override
    public String asString() {
        return type().keyword();
    }
}
