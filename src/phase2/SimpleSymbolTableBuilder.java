import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class SimpleSymbolTableBuilder {
    private SymbolTable globalScope;
    private SymbolTable currentScope;
    
    public SimpleSymbolTableBuilder() {
        globalScope = new SymbolTable("Global", null);
        currentScope = globalScope;
    }
    
    public SymbolTable buildSymbolTable(String code) {
        try {
            ANTLRInputStream input = new ANTLRInputStream(code);
            javaMinusMinus2Lexer lexer = new javaMinusMinus2Lexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            javaMinusMinus2Parser parser = new javaMinusMinus2Parser(tokens);
            
            ParseTree tree = parser.program();
            
            buildFromParseTree(tree);
            
        } catch (Exception e) {
            System.err.println("Error building symbol table: " + e.getMessage());
            e.printStackTrace();
        }
        
        return globalScope;
    }
    
    private void buildFromParseTree(ParseTree tree) {
        if (tree == null) return;
        
        String nodeType = tree.getClass().getSimpleName();
        
        switch (nodeType) {
            case "MainClassContext":
                processMainClass(tree);
                break;
            case "ClassDeclContext":
                processClass(tree);
                break;
            case "InterfaceDeclContext":
                processInterface(tree);
                break;
            case "MethodDeclContext":
                processMethod(tree);
                break;
            case "FieldDeclContext":
                processField(tree);
                break;
            case "LocalDeclContext":
                processLocalVar(tree);
                break;
        }
        
        for (int i = 0; i < tree.getChildCount(); i++) {
            buildFromParseTree(tree.getChild(i));
        }
    }
    
    private void processMainClass(ParseTree tree) {
        try {
            String className = getIdentifierAt(tree, 1); 
            Symbol classSymbol = new Symbol(className, Symbol.Type.CLASS, null, 0, 0);
            currentScope.addSymbol(classSymbol);
            
            SymbolTable classScope = new SymbolTable(className, currentScope);
            classSymbol.setLocalScope(classScope);
            currentScope = classScope;
            
            Symbol mainMethod = new Symbol("main", Symbol.Type.METHOD, "void", 0, 0);
            mainMethod.setAccessModifier("public");
            mainMethod.setStatic(true);
            currentScope.addSymbol(mainMethod);
            
            SymbolTable mainScope = new SymbolTable("main", currentScope);
            mainMethod.setLocalScope(mainScope);
            SymbolTable previousScope = currentScope;
            currentScope = mainScope;
            
            Symbol argsParam = new Symbol("args", Symbol.Type.PARAMETER, "String[]", 0, 0);
            currentScope.addSymbol(argsParam);
            
            
            currentScope = previousScope;
            currentScope = currentScope.getParent();
            
        } catch (Exception e) {
            System.err.println("Error processing main class: " + e.getMessage());
        }
    }
    
    private void processClass(ParseTree tree) {
        try {
            String className = getIdentifierAt(tree, 1); 
            Symbol classSymbol = new Symbol(className, Symbol.Type.CLASS, null, 0, 0);
            
            if (tree.getChild(0).getText().equals("abstract")) {
                classSymbol.setAbstract(true);
            }
            
            currentScope.addSymbol(classSymbol);
            
            SymbolTable classScope = new SymbolTable(className, currentScope);
            classSymbol.setLocalScope(classScope);
            SymbolTable previousScope = currentScope;
            currentScope = classScope;
            
            
            currentScope = previousScope;
            
        } catch (Exception e) {
            System.err.println("Error processing class: " + e.getMessage());
        }
    }
    
    private void processInterface(ParseTree tree) {
        try {
            String interfaceName = getIdentifierAt(tree, 1); 
            Symbol interfaceSymbol = new Symbol(interfaceName, Symbol.Type.INTERFACE, null, 0, 0);
            currentScope.addSymbol(interfaceSymbol);
            
            SymbolTable interfaceScope = new SymbolTable(interfaceName, currentScope);
            interfaceSymbol.setLocalScope(interfaceScope);
            SymbolTable previousScope = currentScope;
            currentScope = interfaceScope;
            
            
            currentScope = previousScope;
            
        } catch (Exception e) {
            System.err.println("Error processing interface: " + e.getMessage());
        }
    }
    
    private void processMethod(ParseTree tree) {
        try {
            String methodName = "";
            String returnType = "void";
            String accessModifier = "default";
            
            for (int i = 0; i < tree.getChildCount(); i++) {
                String childText = tree.getChild(i).getText();
                if (childText.equals("public") || childText.equals("private") || 
                    childText.equals("protected") || childText.equals("internal")) {
                    accessModifier = childText;
                } else if (childText.equals("int") || childText.equals("boolean") || 
                          childText.equals("char") || childText.equals("String") || 
                          childText.equals("void")) {
                    returnType = childText;
                } else if (isIdentifier(tree.getChild(i)) && methodName.isEmpty()) {
                    methodName = childText;
                }
            }
            
            if (!methodName.isEmpty()) {
                Symbol methodSymbol = new Symbol(methodName, Symbol.Type.METHOD, returnType, 0, 0);
                methodSymbol.setAccessModifier(accessModifier);
                currentScope.addSymbol(methodSymbol);
                
                SymbolTable methodScope = new SymbolTable(methodName, currentScope);
                methodSymbol.setLocalScope(methodScope);
                SymbolTable previousScope = currentScope;
                currentScope = methodScope;
                
                
                currentScope = previousScope;
            }
            
        } catch (Exception e) {
            System.err.println("Error processing method: " + e.getMessage());
        }
    }
    
    private void processField(ParseTree tree) {
        try {
            String fieldName = "";
            String dataType = "";
            String accessModifier = "default";
            
            for (int i = 0; i < tree.getChildCount(); i++) {
                String childText = tree.getChild(i).getText();
                if (childText.equals("public") || childText.equals("private") || 
                    childText.equals("protected") || childText.equals("internal")) {
                    accessModifier = childText;
                } else if (childText.equals("int") || childText.equals("boolean") || 
                          childText.equals("char") || childText.equals("String")) {
                    dataType = childText;
                } else if (isIdentifier(tree.getChild(i)) && fieldName.isEmpty()) {
                    fieldName = childText;
                }
            }
            
            if (!fieldName.isEmpty() && !dataType.isEmpty()) {
                Symbol fieldSymbol = new Symbol(fieldName, Symbol.Type.FIELD, dataType, 0, 0);
                fieldSymbol.setAccessModifier(accessModifier);
                
                if (dataType.contains("[]")) {
                    fieldSymbol.setArray(true);
                }
                
                currentScope.addSymbol(fieldSymbol);
            }
            
        } catch (Exception e) {
            System.err.println("Error processing field: " + e.getMessage());
        }
    }
    
    private void processLocalVar(ParseTree tree) {
        try {
            String varName = "";
            String dataType = "";
            
            for (int i = 0; i < tree.getChildCount(); i++) {
                String childText = tree.getChild(i).getText();
                if (childText.equals("int") || childText.equals("boolean") || 
                    childText.equals("char") || childText.equals("String")) {
                    dataType = childText;
                } else if (isIdentifier(tree.getChild(i)) && varName.isEmpty()) {
                    varName = childText;
                }
            }
            
            if (!varName.isEmpty() && !dataType.isEmpty()) {
                Symbol varSymbol = new Symbol(varName, Symbol.Type.VARIABLE, dataType, 0, 0);
                
                if (dataType.contains("[]")) {
                    varSymbol.setArray(true);
                }
                
                currentScope.addSymbol(varSymbol);
            }
            
        } catch (Exception e) {
            System.err.println("Error processing local variable: " + e.getMessage());
        }
    }
    
    private String getIdentifierAt(ParseTree tree, int position) {
        int identifierCount = 0;
        for (int i = 0; i < tree.getChildCount(); i++) {
            if (isIdentifier(tree.getChild(i))) {
                if (identifierCount == position) {
                    return tree.getChild(i).getText();
                }
                identifierCount++;
            }
        }
        return "";
    }
    
    private boolean isIdentifier(ParseTree node) {
        return node instanceof TerminalNode && 
               ((TerminalNode) node).getSymbol().getType() == javaMinusMinus2Lexer.Identifier;
    }
    
    public SymbolTable getSymbolTable() {
        return globalScope;
    }
}
