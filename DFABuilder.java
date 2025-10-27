import java.util.HashMap;
import java.util.Map;

public class DFABuilder {
    private Map<String, TokenType> keywords;
    
    public DFABuilder() {
        initializeKeywords();
    }

    private void initializeKeywords() {
        keywords = new HashMap<>();
        keywords.put("abstract", TokenType.ABSTRACT);
        keywords.put("boolean", TokenType.BOOLEAN);
        keywords.put("break", TokenType.BREAK);
        keywords.put("char", TokenType.CHAR);
        keywords.put("class", TokenType.CLASS);
        keywords.put("continue", TokenType.CONTINUE);
        keywords.put("else", TokenType.ELSE);
        keywords.put("extends", TokenType.EXTENDS);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("if", TokenType.IF);
        keywords.put("implements", TokenType.IMPLEMENTS);
        keywords.put("import", TokenType.IMPORT);
        keywords.put("int", TokenType.INT);
        keywords.put("interface", TokenType.INTERFACE);
        keywords.put("internal", TokenType.INTERNAL);
        keywords.put("main", TokenType.MAIN);
        keywords.put("new", TokenType.NEW);
        keywords.put("null", TokenType.NULL);
        keywords.put("private", TokenType.PRIVATE);
        keywords.put("protected", TokenType.PROTECTED);
        keywords.put("public", TokenType.PUBLIC);
        keywords.put("read", TokenType.READ);
        keywords.put("return", TokenType.RETURN);
        keywords.put("static", TokenType.STATIC);
        keywords.put("String", TokenType.STRING);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("void", TokenType.VOID);
        keywords.put("while", TokenType.WHILE);
        keywords.put("print", TokenType.PRINT);
    }

    public DFAState buildIdentifierDFA() {
        DFAState start = new DFAState(0);
        DFAState accepting = new DFAState(1, true, TokenType.IDENTIFIER);

        start.addRangeTransition('a', 'z', accepting);
        start.addRangeTransition('A', 'Z', accepting);
        start.addTransition('$', accepting);
        start.addTransition('_', accepting);

        accepting.addRangeTransition('a', 'z', accepting);
        accepting.addRangeTransition('A', 'Z', accepting);
        accepting.addRangeTransition('0', '9', accepting);
        accepting.addTransition('$', accepting);
        accepting.addTransition('_', accepting);

        return start;
    }

    public DFAState buildIntegerDFA() {
        DFAState start = new DFAState(0);
        DFAState zero = new DFAState(1, true, TokenType.INTEGER_LITERAL);
        DFAState nonZero = new DFAState(2, true, TokenType.INTEGER_LITERAL);
        DFAState digit = new DFAState(3, true, TokenType.INTEGER_LITERAL);
        DFAState suffix = new DFAState(4, true, TokenType.INTEGER_LITERAL);

        start.addTransition('0', zero);
        
        start.addRangeTransition('1', '9', nonZero);
        
        nonZero.addRangeTransition('0', '9', digit);
        nonZero.addTransition('_', digit);
        
        digit.addRangeTransition('0', '9', digit);
        digit.addTransition('_', digit);
        
        zero.addTransition('l', suffix);
        zero.addTransition('L', suffix);
        nonZero.addTransition('l', suffix);
        nonZero.addTransition('L', suffix);
        digit.addTransition('l', suffix);
        digit.addTransition('L', suffix);

        return start;
    }

    public DFAState buildStringDFA() {
        DFAState start = new DFAState(0);
        DFAState inString = new DFAState(1);
        DFAState escaped = new DFAState(2);
        DFAState accepting = new DFAState(3, true, TokenType.STRING_LITERAL);

        start.addTransition('"', inString);
        
        for (char c = 0; c < 128; c++) {
            if (c != '"' && c != '\\') {
                inString.addTransition(c, inString);
            }
        }
        
        inString.addTransition('\\', escaped);
        
        for (char c = 0; c < 128; c++) {
            escaped.addTransition(c, inString);
        }
        
        inString.addTransition('"', accepting);

        return start;
    }

    public DFAState buildCharDFA() {
        DFAState start = new DFAState(0);
        DFAState afterQuote = new DFAState(1);
        DFAState escaped = new DFAState(2);
        DFAState hasChar = new DFAState(3);
        DFAState accepting = new DFAState(4, true, TokenType.CHAR_LITERAL);

        start.addTransition('\'', afterQuote);
        
        for (char c = 0; c < 128; c++) {
            if (c != '\'' && c != '\\' && c != '\r' && c != '\n') {
                afterQuote.addTransition(c, hasChar);
            }
        }
        
        afterQuote.addTransition('\\', escaped);
        
        for (char c = 0; c < 128; c++) {
            escaped.addTransition(c, hasChar);
        }
        
        hasChar.addTransition('\'', accepting);

        return start;
    }

    public TokenType getKeywordType(String identifier) {
        return keywords.getOrDefault(identifier, TokenType.IDENTIFIER);
    }

    public boolean isKeyword(String identifier) {
        return keywords.containsKey(identifier);
    }
}