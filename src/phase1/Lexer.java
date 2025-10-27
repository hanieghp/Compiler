package phase1;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String input;
    private int pos;
    private int line;
    private int column;
    private DFABuilder dfaBuilder;

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
        this.line = 1;
        this.column = 1;
        this.dfaBuilder = new DFABuilder();
    }

    public List<LexerToken> tokenize() {
        List<LexerToken> tokens = new ArrayList<>();
        
        while (pos < input.length()) {
            skipWhitespaceAndComments();
            
            if (pos >= input.length()) {
                break;
            }

            LexerToken LexerToken = nextToken();
            if (LexerToken != null) {
                tokens.add(LexerToken);
            }
        }

        tokens.add(new LexerToken(TokenType.EOF, "", line, column));
        return tokens;
    }

    private LexerToken nextToken() {
        char current = peek();
        int startLine = line;
        int startColumn = column;

        LexerToken opToken = tryOperatorOrDelimiter();
        if (opToken != null) {
            return opToken;
        }

        if (current == '"') {
            return scanString(startLine, startColumn);
        }

        if (current == '\'') {
            return scanChar(startLine, startColumn);
        }

        if (Character.isDigit(current)) {
            return scanInteger(startLine, startColumn);
        }

        if (isIdentifierStart(current)) {
            return scanIdentifier(startLine, startColumn);
        }

        advance();
        return new LexerToken(TokenType.ERROR, String.valueOf(current), startLine, startColumn);
    }

    private LexerToken tryOperatorOrDelimiter() {
        char c = peek();
        int startLine = line;
        int startColumn = column;

        if (pos + 1 < input.length()) {
            String twoChar = input.substring(pos, pos + 2);
            TokenType type = null;

            switch (twoChar) {
                case "&&": type = TokenType.AND; break;
                case "||": type = TokenType.OR; break;
                case "<=": type = TokenType.LE; break;
                case ">=": type = TokenType.GE; break;
                case "==": type = TokenType.EQEQ; break;
                case "!=": type = TokenType.NEQ; break;
                case "**": type = TokenType.POWER; break;
            }

            if (type != null) {
                advance();
                advance();
                return new LexerToken(type, twoChar, startLine, startColumn);
            }
        }

        if (pos + 6 < input.length() && input.substring(pos, pos + 7).equals(".length")) {
            String val = ".length";
            for (int i = 0; i < 7; i++) advance();
            return new LexerToken(TokenType.DOTLENGTH, val, startLine, startColumn);
        }

        TokenType type = null;
        switch (c) {
            case '<': type = TokenType.LT; break;
            case '>': type = TokenType.GT; break;
            case '+': type = TokenType.PLUS; break;
            case '-': type = TokenType.MINUS; break;
            case '*': type = TokenType.TIMES; break;
            case '/': type = TokenType.DIV; break;
            case '%': type = TokenType.MOD; break;
            case '!': type = TokenType.NOT; break;
            case '=': type = TokenType.EQ; break;
            case '(': type = TokenType.LPAREN; break;
            case ')': type = TokenType.RPAREN; break;
            case '{': type = TokenType.LBRACE; break;
            case '}': type = TokenType.RBRACE; break;
            case '[': type = TokenType.LBRACKET; break;
            case ']': type = TokenType.RBRACKET; break;
            case ';': type = TokenType.SEMICOLON; break;
            case ',': type = TokenType.COMMA; break;
            case '.': type = TokenType.DOT; break;
        }

        if (type != null) {
            advance();
            return new LexerToken(type, String.valueOf(c), startLine, startColumn);
        }

        return null;
    }

    private LexerToken scanIdentifier(int startLine, int startColumn) {
        DFAState start = dfaBuilder.buildIdentifierDFA();
        StringBuilder sb = new StringBuilder();
        DFAState currentState = start;
        DFAState lastAcceptingState = null;
        int lastAcceptingPos = pos;

        while (pos < input.length()) {
            char c = peek();
            DFAState nextState = currentState.transition(c);

            if (nextState == null) {
                break;
            }

            sb.append(c);
            advance();
            currentState = nextState;

            if (currentState.isAccepting()) {
                lastAcceptingState = currentState;
                lastAcceptingPos = pos;
            }
        }

        if (lastAcceptingState != null) {
            String value = sb.toString();
            TokenType type = dfaBuilder.getKeywordType(value);
            return new LexerToken(type, value, startLine, startColumn);
        }

        return new LexerToken(TokenType.ERROR, sb.toString(), startLine, startColumn);
    }

    private LexerToken scanInteger(int startLine, int startColumn) {
        DFAState start = dfaBuilder.buildIntegerDFA();
        StringBuilder sb = new StringBuilder();
        DFAState currentState = start;
        DFAState lastAcceptingState = null;
        int lastAcceptingPos = pos;

        while (pos < input.length()) {
            char c = peek();
            DFAState nextState = currentState.transition(c);

            if (nextState == null) {
                break;
            }

            sb.append(c);
            advance();
            currentState = nextState;

            if (currentState.isAccepting()) {
                lastAcceptingState = currentState;
                lastAcceptingPos = pos;
            }
        }

        if (lastAcceptingState != null) {
            String value = sb.toString();
            return new LexerToken(TokenType.INTEGER_LITERAL, value, startLine, startColumn);
        }

        return new LexerToken(TokenType.ERROR, sb.toString(), startLine, startColumn);
    }

    private LexerToken scanString(int startLine, int startColumn) {
        DFAState start = dfaBuilder.buildStringDFA();
        StringBuilder sb = new StringBuilder();
        DFAState currentState = start;

        while (pos < input.length()) {
            char c = peek();
            DFAState nextState = currentState.transition(c);

            if (nextState == null) {
                break;
            }

            sb.append(c);
            advance();
            currentState = nextState;

            if (currentState.isAccepting()) {
                return new LexerToken(TokenType.STRING_LITERAL, sb.toString(), startLine, startColumn);
            }
        }

        return new LexerToken(TokenType.ERROR, sb.toString(), startLine, startColumn);
    }

    private LexerToken scanChar(int startLine, int startColumn) {
        DFAState start = dfaBuilder.buildCharDFA();
        StringBuilder sb = new StringBuilder();
        DFAState currentState = start;

        while (pos < input.length()) {
            char c = peek();
            DFAState nextState = currentState.transition(c);

            if (nextState == null) {
                break;
            }

            sb.append(c);
            advance();
            currentState = nextState;

            if (currentState.isAccepting()) {
                return new LexerToken(TokenType.CHAR_LITERAL, sb.toString(), startLine, startColumn);
            }
        }

        return new LexerToken(TokenType.ERROR, sb.toString(), startLine, startColumn);
    }

    private void skipWhitespaceAndComments() {
        while (pos < input.length()) {
            char c = peek();

            if (Character.isWhitespace(c)) {
                advance();
                continue;
            }

            if (c == '/' && pos + 1 < input.length() && peek(1) == '/') {
                advance();
                advance();
                while (pos < input.length() && peek() != '\n') {
                    advance();
                }
                continue;
            }

            if (c == '/' && pos + 1 < input.length() && peek(1) == '*') {
                advance();
                advance();
                while (pos + 1 < input.length()) {
                    if (peek() == '*' && peek(1) == '/') {
                        advance();
                        advance();
                        break;
                    }
                    advance();
                }
                continue;
            }

            break;
        }
    }

    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '$' || c == '_';
    }

    private char peek() {
        return peek(0);
    }

    private char peek(int offset) {
        int index = pos + offset;
        if (index >= input.length()) {
            return '\0';
        }
        return input.charAt(index);
    }

    private void advance() {
        if (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            pos++;
        }
    }
}
