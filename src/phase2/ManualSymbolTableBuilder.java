package phase2;

import java.util.*;
import phase1.*;

public class ManualSymbolTableBuilder {
    private SymbolTable globalScope;
    private SymbolTable currentScope;
    private List<LexerToken> tokens;
    private int currentToken;
    
    public ManualSymbolTableBuilder() {
        globalScope = new SymbolTable("Global", null);
        currentScope = globalScope;
        currentToken = 0;
    }
    
    public SymbolTable buildSymbolTable(String code) {
        try {
            Lexer lexer = new Lexer(code);
            tokens = lexer.tokenize();
            currentToken = 0;
            
            parseProgram();
            
        } catch (Exception e) {
            System.err.println("Error building symbol table: " + e.getMessage());
            e.printStackTrace();
        }
        
        return globalScope;
    }
    
    private void parseProgram() throws Exception {
        while (currentToken < tokens.size() && !isTokenType(TokenType.EOF)) {
            if (isTokenType(TokenType.IMPORT)) {
                skipImport();
            } else if (isTokenType(TokenType.CLASS)) {
                parseClass();
            } else if (isTokenType(TokenType.INTERFACE)) {
                parseInterface();
            } else {
                advance();
            }
        }
    }
    
    private void parseClass() throws Exception {
        boolean isAbstract = false;
        if (isTokenType(TokenType.ABSTRACT)) {
            isAbstract = true;
            advance();
        }
        
        expect(TokenType.CLASS);
        String className = expectIdentifier();
        
        Symbol classSymbol = new Symbol(className, Symbol.Type.CLASS, null, 
            getCurrentToken().getLine(), getCurrentToken().getColumn());
        classSymbol.setAbstract(isAbstract);
        currentScope.addSymbol(classSymbol);
        
        SymbolTable classScope = new SymbolTable(className, currentScope);
        classSymbol.setLocalScope(classScope);
        SymbolTable previousScope = currentScope;
        currentScope = classScope;
        
        if (isTokenType(TokenType.EXTENDS)) {
            advance();
            advance(); 
        } else if (isTokenType(TokenType.IMPLEMENTS)) {
            advance();
            do {
                advance(); 
                if (isTokenType(TokenType.COMMA)) {
                    advance();
                }
            } while (!isTokenType(TokenType.LBRACE));
        }
        
        expect(TokenType.LBRACE);
        
        while (!isTokenType(TokenType.RBRACE) && currentToken < tokens.size()) {
            parseClassMember();
        }
        
        expect(TokenType.RBRACE);
        currentScope = previousScope;
    }
    
    private void parseInterface() throws Exception {
        expect(TokenType.INTERFACE);
        String interfaceName = expectIdentifier();
        
        Symbol interfaceSymbol = new Symbol(interfaceName, Symbol.Type.INTERFACE, null,
            getCurrentToken().getLine(), getCurrentToken().getColumn());
        currentScope.addSymbol(interfaceSymbol);
        
        SymbolTable interfaceScope = new SymbolTable(interfaceName, currentScope);
        interfaceSymbol.setLocalScope(interfaceScope);
        SymbolTable previousScope = currentScope;
        currentScope = interfaceScope;
        
        expect(TokenType.LBRACE);
        
        while (!isTokenType(TokenType.RBRACE) && currentToken < tokens.size()) {
            parseInterfaceMember();
        }
        
        expect(TokenType.RBRACE);
        currentScope = previousScope;
    }
    
    private void parseClassMember() throws Exception {
        String accessModifier = "default";
        boolean isStatic = false;
        boolean isAbstract = false;
        
        if (isTokenType(TokenType.PUBLIC) || isTokenType(TokenType.PRIVATE) || 
            isTokenType(TokenType.PROTECTED) || isTokenType(TokenType.INTERNAL)) {
            accessModifier = getCurrentToken().getValue();
            advance();
        }
        
        if (isTokenType(TokenType.STATIC)) {
            isStatic = true;
            advance();
            
            if (isTokenType(TokenType.VOID) && peek().getValue().equals("main")) {
                parseMainMethod(accessModifier, isStatic);
                return;
            }
        }
        
        if (isTokenType(TokenType.ABSTRACT)) {
            isAbstract = true;
            advance();
        }
        
        if (isType()) {
            String type = getCurrentToken().getValue();
            advance();
            
            if (isTokenType(TokenType.IDENTIFIER)) {
                String name = getCurrentToken().getValue();
                advance();
                
                if (isTokenType(TokenType.LPAREN)) {
                    parseMethod(name, type, accessModifier, isStatic, isAbstract);
                } else {
                    parseField(name, type, accessModifier, isStatic);
                }
            }
        } else if (isTokenType(TokenType.VOID)) {
            advance();
            String methodName = expectIdentifier();
            parseMethod(methodName, "void", accessModifier, isStatic, isAbstract);
        } else if (isTokenType(TokenType.IDENTIFIER)) {
            String name = getCurrentToken().getValue();
            advance();
            if (isTokenType(TokenType.LPAREN)) {
                parseConstructor(name, accessModifier);
            }
        } else {
            advance(); 
        }
    }
    
    private void parseInterfaceMember() throws Exception {
        if (isType()) {
            String type = getCurrentToken().getValue();
            advance();
            String name = expectIdentifier();
            
            if (isTokenType(TokenType.LPAREN)) {
                parseInterfaceMethod(name, type);
            } else {
                parseInterfaceField(name, type);
            }
        } else if (isTokenType(TokenType.VOID)) {
            advance();
            String methodName = expectIdentifier();
            parseInterfaceMethod(methodName, "void");
        } else {
            advance();
        }
    }
    
    private void parseMainMethod(String accessModifier, boolean isStatic) throws Exception {
        expect(TokenType.VOID);
        expect(TokenType.MAIN);
        
        Symbol mainMethod = new Symbol("main", Symbol.Type.METHOD, "void",
            getCurrentToken().getLine(), getCurrentToken().getColumn());
        mainMethod.setAccessModifier(accessModifier);
        mainMethod.setStatic(isStatic);
        currentScope.addSymbol(mainMethod);
        
        SymbolTable methodScope = new SymbolTable("main", currentScope);
        mainMethod.setLocalScope(methodScope);
        SymbolTable previousScope = currentScope;
        currentScope = methodScope;
        
        expect(TokenType.LPAREN);
        expect(TokenType.STRING);
        expect(TokenType.LBRACKET);
        expect(TokenType.RBRACKET);
        String argsName = expectIdentifier();
        
        Symbol argsParam = new Symbol(argsName, Symbol.Type.PARAMETER, "String[]",
            getCurrentToken().getLine(), getCurrentToken().getColumn());
        currentScope.addSymbol(argsParam);
        
        expect(TokenType.RPAREN);
        expect(TokenType.LBRACE);
        
        parseMethodBody();
        
        expect(TokenType.RBRACE);
        currentScope = previousScope;
    }
    
    private void parseMethod(String name, String returnType, String accessModifier, 
                           boolean isStatic, boolean isAbstract) throws Exception {
        Symbol methodSymbol = new Symbol(name, Symbol.Type.METHOD, returnType,
            getCurrentToken().getLine(), getCurrentToken().getColumn());
        methodSymbol.setAccessModifier(accessModifier);
        methodSymbol.setStatic(isStatic);
        methodSymbol.setAbstract(isAbstract);
        currentScope.addSymbol(methodSymbol);
        
        SymbolTable methodScope = new SymbolTable(name, currentScope);
        methodSymbol.setLocalScope(methodScope);
        SymbolTable previousScope = currentScope;
        currentScope = methodScope;
        
        expect(TokenType.LPAREN);
        parseParameterList();
        expect(TokenType.RPAREN);
        
        if (isAbstract) {
            expect(TokenType.SEMICOLON);
        } else {
            expect(TokenType.LBRACE);
            parseMethodBody();
            expect(TokenType.RBRACE);
        }
        
        currentScope = previousScope;
    }
    
    private void parseConstructor(String name, String accessModifier) throws Exception {
        Symbol ctorSymbol = new Symbol(name, Symbol.Type.CONSTRUCTOR, null,
            getCurrentToken().getLine(), getCurrentToken().getColumn());
        ctorSymbol.setAccessModifier(accessModifier);
        currentScope.addSymbol(ctorSymbol);
        
        SymbolTable ctorScope = new SymbolTable(name + "_ctor", currentScope);
        ctorSymbol.setLocalScope(ctorScope);
        SymbolTable previousScope = currentScope;
        currentScope = ctorScope;
        
        expect(TokenType.LPAREN);
        parseParameterList();
        expect(TokenType.RPAREN);
        expect(TokenType.LBRACE);
        parseMethodBody();
        expect(TokenType.RBRACE);
        
        currentScope = previousScope;
    }
    
    private void parseField(String name, String type, String accessModifier, boolean isStatic) throws Exception {
        Symbol fieldSymbol = new Symbol(name, Symbol.Type.FIELD, type,
            getCurrentToken().getLine(), getCurrentToken().getColumn());
        fieldSymbol.setAccessModifier(accessModifier);
        fieldSymbol.setStatic(isStatic);
        
        if (isTokenType(TokenType.LBRACKET)) {
            fieldSymbol.setArray(true);
            advance();
            expect(TokenType.RBRACKET);
        }
        
        currentScope.addSymbol(fieldSymbol);
        
        while (!isTokenType(TokenType.SEMICOLON) && currentToken < tokens.size()) {
            advance();
        }
        expect(TokenType.SEMICOLON);
    }
    
    private void parseInterfaceMethod(String name, String returnType) throws Exception {
        Symbol methodSymbol = new Symbol(name, Symbol.Type.METHOD, returnType,
            getCurrentToken().getLine(), getCurrentToken().getColumn());
        methodSymbol.setAccessModifier("public");
        methodSymbol.setAbstract(true);
        currentScope.addSymbol(methodSymbol);
        
        SymbolTable methodScope = new SymbolTable(name + "_interface", currentScope);
        methodSymbol.setLocalScope(methodScope);
        SymbolTable previousScope = currentScope;
        currentScope = methodScope;
        
        expect(TokenType.LPAREN);
        parseParameterList();
        expect(TokenType.RPAREN);
        expect(TokenType.SEMICOLON);
        
        currentScope = previousScope;
    }
    
    private void parseInterfaceField(String name, String type) throws Exception {
        Symbol fieldSymbol = new Symbol(name, Symbol.Type.FIELD, type,
            getCurrentToken().getLine(), getCurrentToken().getColumn());
        fieldSymbol.setAccessModifier("public");
        fieldSymbol.setStatic(true);
        
        if (isTokenType(TokenType.LBRACKET)) {
            fieldSymbol.setArray(true);
            advance();
            expect(TokenType.RBRACKET);
        }
        
        currentScope.addSymbol(fieldSymbol);
        
        while (!isTokenType(TokenType.SEMICOLON) && currentToken < tokens.size()) {
            advance();
        }
        expect(TokenType.SEMICOLON);
    }
    
    private void parseParameterList() throws Exception {
        if (!isTokenType(TokenType.RPAREN)) {
            do {
                if (isType()) {
                    String paramType = getCurrentToken().getValue();
                    advance();
                    
                    if (isTokenType(TokenType.LBRACKET)) {
                        paramType += "[]";
                        advance();
                        expect(TokenType.RBRACKET);
                    }
                    
                    String paramName = expectIdentifier();
                    
                    Symbol paramSymbol = new Symbol(paramName, Symbol.Type.PARAMETER, paramType,
                        getCurrentToken().getLine(), getCurrentToken().getColumn());
                    currentScope.addSymbol(paramSymbol);
                }
                
                if (isTokenType(TokenType.COMMA)) {
                    advance();
                }
            } while (!isTokenType(TokenType.RPAREN));
        }
    }
    
    private void parseMethodBody() throws Exception {
        while (!isTokenType(TokenType.RBRACE) && currentToken < tokens.size()) {
            if (isType()) {
                String varType = getCurrentToken().getValue();
                advance();
                
                if (isTokenType(TokenType.LBRACKET)) {
                    varType += "[]";
                    advance();
                    expect(TokenType.RBRACKET);
                }
                
                String varName = expectIdentifier();
                
                Symbol varSymbol = new Symbol(varName, Symbol.Type.VARIABLE, varType,
                    getCurrentToken().getLine(), getCurrentToken().getColumn());
                currentScope.addSymbol(varSymbol);
                
                while (!isTokenType(TokenType.SEMICOLON) && currentToken < tokens.size()) {
                    advance();
                }
                expect(TokenType.SEMICOLON);
            } else {
                advance();
            }
        }
    }
    
    private void skipImport() throws Exception {
        while (!isTokenType(TokenType.SEMICOLON) && currentToken < tokens.size()) {
            advance();
        }
        advance(); 
    }
    
    private boolean isType() {
        return isTokenType(TokenType.INT) || isTokenType(TokenType.BOOLEAN) || 
               isTokenType(TokenType.CHAR) || isTokenType(TokenType.STRING) ||
               isTokenType(TokenType.IDENTIFIER);
    }
    
    private boolean isTokenType(TokenType type) {
        return currentToken < tokens.size() && tokens.get(currentToken).getType() == type;
    }
    
    private LexerToken getCurrentToken() {
        return currentToken < tokens.size() ? tokens.get(currentToken) : null;
    }
    
    private LexerToken peek() {
        return currentToken + 1 < tokens.size() ? tokens.get(currentToken + 1) : null;
    }
    
    private void advance() {
        if (currentToken < tokens.size()) {
            currentToken++;
        }
    }
    
    private void expect(TokenType type) throws Exception {
        if (!isTokenType(type)) {
            throw new Exception("Expected " + type + " but found " + 
                (getCurrentToken() != null ? getCurrentToken().getType() : "EOF") +
                " at line " + (getCurrentToken() != null ? getCurrentToken().getLine() : 0));
        }
        advance();
    }
    
    private String expectIdentifier() throws Exception {
        if (!isTokenType(TokenType.IDENTIFIER)) {
            throw new Exception("Expected identifier but found " + 
                (getCurrentToken() != null ? getCurrentToken().getType() : "EOF"));
        }
        String name = getCurrentToken().getValue();
        advance();
        return name;
    }
    
    public SymbolTable getSymbolTable() {
        return globalScope;
    }
}
