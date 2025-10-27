public class SymbolTableBuilder extends javaMinusMinus2BaseVisitor<Void> {
    private SymbolTable globalScope;
    private SymbolTable currentScope;
    
    public SymbolTableBuilder() {
        globalScope = new SymbolTable("Global", null);
        currentScope = globalScope;
    }
    
    public SymbolTable getSymbolTable() {
        return globalScope;
    }
    
    @Override
    public Void visitProgram(javaMinusMinus2Parser.ProgramContext ctx) {
        return super.visitProgram(ctx);
    }
    
    @Override
    public Void visitMainClass(javaMinusMinus2Parser.MainClassContext ctx) {
        try {
            String className = ctx.Identifier(0).getText();
            Symbol classSymbol = new Symbol(className, Symbol.Type.CLASS, null, 
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            currentScope.addSymbol(classSymbol);
            
            SymbolTable classScope = new SymbolTable(className, currentScope);
            classSymbol.setLocalScope(classScope);
            currentScope = classScope;
            
            Symbol mainMethod = new Symbol("main", Symbol.Type.METHOD, "void",
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            mainMethod.setAccessModifier("public");
            mainMethod.setStatic(true);
            currentScope.addSymbol(mainMethod);
            
            SymbolTable mainScope = new SymbolTable("main", currentScope);
            mainMethod.setLocalScope(mainScope);
            SymbolTable previousScope = currentScope;
            currentScope = mainScope;
            
            Symbol argsParam = new Symbol(ctx.Identifier(1).getText(), Symbol.Type.PARAMETER, "String[]",
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            currentScope.addSymbol(argsParam);
            
            for (javaMinusMinus2Parser.StatementContext stmt : ctx.statement()) {
                visit(stmt);
            }
            
            currentScope = previousScope;
            currentScope = currentScope.getParent(); 
            
        } catch (Exception e) {
            System.err.println("Error in main class: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitClassDecl(javaMinusMinus2Parser.ClassDeclContext ctx) {
        try {
            String className = ctx.Identifier(0).getText();
            Symbol classSymbol = new Symbol(className, Symbol.Type.CLASS, null,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            
            if (ctx.getChild(0).getText().equals("abstract")) {
                classSymbol.setAbstract(true);
            }
            
            currentScope.addSymbol(classSymbol);
            
            SymbolTable classScope = new SymbolTable(className, currentScope);
            classSymbol.setLocalScope(classScope);
            SymbolTable previousScope = currentScope;
            currentScope = classScope;
            
            for (javaMinusMinus2Parser.FieldDeclContext field : ctx.fieldDecl()) {
                visit(field);
            }
            
            if (ctx.ctorDecl() != null) {
                visit(ctx.ctorDecl());
            }
            
            for (javaMinusMinus2Parser.MethodDeclContext method : ctx.methodDecl()) {
                visit(method);
            }
            
            for (javaMinusMinus2Parser.AbstractMethodDeclContext abstractMethod : ctx.abstractMethodDecl()) {
                visit(abstractMethod);
            }
            
            currentScope = previousScope;
            
        } catch (Exception e) {
            System.err.println("Error in class declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitInterfaceDecl(javaMinusMinus2Parser.InterfaceDeclContext ctx) {
        try {
            String interfaceName = ctx.Identifier().getText();
            Symbol interfaceSymbol = new Symbol(interfaceName, Symbol.Type.INTERFACE, null,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            currentScope.addSymbol(interfaceSymbol);
            
            SymbolTable interfaceScope = new SymbolTable(interfaceName, currentScope);
            interfaceSymbol.setLocalScope(interfaceScope);
            SymbolTable previousScope = currentScope;
            currentScope = interfaceScope;
            
            for (javaMinusMinus2Parser.InterfaceFieldDeclContext field : ctx.interfaceFieldDecl()) {
                visit(field);
            }
            
            for (javaMinusMinus2Parser.InterfaceMethodDeclContext method : ctx.interfaceMethodDecl()) {
                visit(method);
            }
            
            currentScope = previousScope;
            
        } catch (Exception e) {
            System.err.println("Error in interface declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitFieldDecl(javaMinusMinus2Parser.FieldDeclContext ctx) {
        return visit(ctx.varDecl());
    }
    
    @Override
    public Void visitVarDecl(javaMinusMinus2Parser.VarDeclContext ctx) {
        try {
            String fieldName = ctx.Identifier().getText();
            String dataType = getTypeString(ctx.type());
            
            Symbol fieldSymbol = new Symbol(fieldName, Symbol.Type.FIELD, dataType,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            
            if (ctx.accessModifier() != null) {
                fieldSymbol.setAccessModifier(ctx.accessModifier().getText());
            }
            
            if (ctx.type().getText().contains("[]")) {
                fieldSymbol.setArray(true);
            }
            
            currentScope.addSymbol(fieldSymbol);
            
        } catch (Exception e) {
            System.err.println("Error in field declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitMethodDecl(javaMinusMinus2Parser.MethodDeclContext ctx) {
        try {
            String methodName = ctx.Identifier().getText();
            String returnType = ctx.getChild(ctx.getChildCount() - 6).getText(); 
            
            Symbol methodSymbol = new Symbol(methodName, Symbol.Type.METHOD, returnType,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            
            if (ctx.accessModifier() != null) {
                methodSymbol.setAccessModifier(ctx.accessModifier().getText());
            }
            
            currentScope.addSymbol(methodSymbol);
            
            SymbolTable methodScope = new SymbolTable(methodName, currentScope);
            methodSymbol.setLocalScope(methodScope);
            SymbolTable previousScope = currentScope;
            currentScope = methodScope;
            
            if (ctx.parameterList() != null) {
                visit(ctx.parameterList());
            }
            
            visit(ctx.methodBody());
            
            currentScope = previousScope;
            
        } catch (Exception e) {
            System.err.println("Error in method declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitCtorDecl(javaMinusMinus2Parser.CtorDeclContext ctx) {
        try {
            String ctorName = ctx.Identifier().getText();
            
            Symbol ctorSymbol = new Symbol(ctorName, Symbol.Type.CONSTRUCTOR, null,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            
            if (ctx.accessModifier() != null) {
                ctorSymbol.setAccessModifier(ctx.accessModifier().getText());
            }
            
            currentScope.addSymbol(ctorSymbol);
            
            SymbolTable ctorScope = new SymbolTable(ctorName + "_ctor", currentScope);
            ctorSymbol.setLocalScope(ctorScope);
            SymbolTable previousScope = currentScope;
            currentScope = ctorScope;
            
            if (ctx.parameterList() != null) {
                visit(ctx.parameterList());
            }
            
            visit(ctx.methodBody());
            
            currentScope = previousScope;
            
        } catch (Exception e) {
            System.err.println("Error in constructor declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitAbstractMethodDecl(javaMinusMinus2Parser.AbstractMethodDeclContext ctx) {
        try {
            String methodName = ctx.Identifier().getText();
            String returnType = ctx.getChild(ctx.getChildCount() - 5).getText(); 
            
            Symbol methodSymbol = new Symbol(methodName, Symbol.Type.METHOD, returnType,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            methodSymbol.setAbstract(true);
            
            if (ctx.accessModifier() != null) {
                methodSymbol.setAccessModifier(ctx.accessModifier().getText());
            }
            
            currentScope.addSymbol(methodSymbol);
            
            SymbolTable methodScope = new SymbolTable(methodName + "_abstract", currentScope);
            methodSymbol.setLocalScope(methodScope);
            SymbolTable previousScope = currentScope;
            currentScope = methodScope;
            
            if (ctx.parameterList() != null) {
                visit(ctx.parameterList());
            }
            
            currentScope = previousScope;
            
        } catch (Exception e) {
            System.err.println("Error in abstract method declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitParameterList(javaMinusMinus2Parser.ParameterListContext ctx) {
        for (javaMinusMinus2Parser.ParameterContext param : ctx.parameter()) {
            visit(param);
        }
        return null;
    }
    
    @Override
    public Void visitParameter(javaMinusMinus2Parser.ParameterContext ctx) {
        try {
            String paramName = ctx.Identifier().getText();
            String dataType = getTypeString(ctx.type());
            
            Symbol paramSymbol = new Symbol(paramName, Symbol.Type.PARAMETER, dataType,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            
            if (ctx.type().getText().contains("[]")) {
                paramSymbol.setArray(true);
            }
            
            currentScope.addSymbol(paramSymbol);
            
        } catch (Exception e) {
            System.err.println("Error in parameter declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitLocalDecl(javaMinusMinus2Parser.LocalDeclContext ctx) {
        try {
            String varName = ctx.Identifier().getText();
            String dataType = getTypeString(ctx.type());
            
            Symbol varSymbol = new Symbol(varName, Symbol.Type.VARIABLE, dataType,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            
            if (ctx.type().getText().contains("[]")) {
                varSymbol.setArray(true);
            }
            
            currentScope.addSymbol(varSymbol);
            
        } catch (Exception e) {
            System.err.println("Error in local variable declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitInterfaceFieldDecl(javaMinusMinus2Parser.InterfaceFieldDeclContext ctx) {
        try {
            String fieldName = ctx.Identifier().getText();
            String dataType = getTypeString(ctx.type());
            
            Symbol fieldSymbol = new Symbol(fieldName, Symbol.Type.FIELD, dataType,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            fieldSymbol.setAccessModifier("public");
            fieldSymbol.setStatic(true);
            
            if (ctx.type().getText().contains("[]")) {
                fieldSymbol.setArray(true);
            }
            
            currentScope.addSymbol(fieldSymbol);
            
        } catch (Exception e) {
            System.err.println("Error in interface field declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Void visitInterfaceMethodDecl(javaMinusMinus2Parser.InterfaceMethodDeclContext ctx) {
        try {
            String methodName = ctx.Identifier().getText();
            String returnType = ctx.getChild(0).getText(); 
            
            Symbol methodSymbol = new Symbol(methodName, Symbol.Type.METHOD, returnType,
                ctx.start.getLine(), ctx.start.getCharPositionInLine());
            methodSymbol.setAccessModifier("public");
            methodSymbol.setAbstract(true); 
            
            currentScope.addSymbol(methodSymbol);
            
            SymbolTable methodScope = new SymbolTable(methodName + "_interface", currentScope);
            methodSymbol.setLocalScope(methodScope);
            SymbolTable previousScope = currentScope;
            currentScope = methodScope;
            
            if (ctx.parameterList() != null) {
                visit(ctx.parameterList());
            }
            
            currentScope = previousScope;
            
        } catch (Exception e) {
            System.err.println("Error in interface method declaration: " + e.getMessage());
        }
        
        return null;
    }
    
    private String getTypeString(javaMinusMinus2Parser.TypeContext ctx) {
        if (ctx == null) return "void";
        
        String type = "";
        if (ctx.javaType() != null) {
            type = ctx.javaType().getText();
        } else if (ctx.Identifier() != null) {
            type = ctx.Identifier().getText();
        }
        
        if (ctx.getChildCount() > 1 && ctx.getChild(1).getText().equals("[")) {
            type += "[]";
        }
        
        return type;
    }
}
