package edu.kit.kastel.vads.compiler.parser.symbol;

import edu.kit.kastel.vads.compiler.lexer.Identifier;
import edu.kit.kastel.vads.compiler.lexer.Keyword;

public sealed interface Name extends Comparable<Name> permits IdentName, KeywordName {

    static Name forKeyword(Keyword keyword) {
        return new KeywordName(keyword.type());
    }

    static Name forIdentifier(Identifier identifier) {
        return new IdentName(identifier.value());
    }

    String asString();

    default int compareTo(Name other) {
        return this.asString().compareTo(other.asString());
    }
}
