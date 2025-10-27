public enum TokenType {
    // Keywords
    ABSTRACT, BOOLEAN, BREAK, CHAR, CLASS, CONTINUE, ELSE, EXTENDS,
    FALSE, FOR, IF, IMPLEMENTS, IMPORT, INT, INTERFACE, INTERNAL,
    MAIN, NEW, NULL, OVERRIDE, PRIVATE, PROTECTED, PUBLIC, READ,
    RETURN, STATIC, STRING, THIS, TRUE, VOID, WHILE, PRINT,

    // Operators
    AND,        // &&
    OR,         // ||
    LT,         // <
    LE,         // <=
    GT,         // >
    GE,         // >=
    EQEQ,       // ==
    NEQ,        // !=
    PLUS,       // +
    MINUS,      // -
    TIMES,      // *
    DIV,        // /
    MOD,        // %
    POWER,      // **
    NOT,        // !
    EQ,         // =

    // Delimiters
    LPAREN,     // (
    RPAREN,     // )
    LBRACE,     // {
    RBRACE,     // }
    LBRACKET,   // [
    RBRACKET,   // ]
    SEMICOLON,  // ;
    COMMA,      // ,
    DOT,        // .
    DOTLENGTH,  // .length

    // Literals
    INTEGER_LITERAL,
    CHAR_LITERAL,
    STRING_LITERAL,

    // Identifier
    IDENTIFIER,

    // Special
    EOF,
    ERROR
}