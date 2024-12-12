package org.omnimc.lumina.param;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class Token {

    public enum Type {
        CLASS,
        METHOD,
        IDENTIFIER_OPEN,
        IDENTIFIER_CLOSE,
        BRACE_OPEN,
        BRACE_CLOSE,
        COMMA,
        SEMICOLON,
        NUMBER;
    }
}