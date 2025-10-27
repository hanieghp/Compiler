package phase2;

public class Symbol {
    public enum Type {
        CLASS,
        INTERFACE,
        METHOD,
        CONSTRUCTOR,
        FIELD,
        VARIABLE,
        PARAMETER
    }
    
    private String name;
    private Type symbolType;
    private String dataType;
    private String accessModifier;
    private int line;
    private int column;
    private boolean isStatic;
    private boolean isAbstract;
    private boolean isArray;
    private SymbolTable localScope; 
    
    public Symbol(String name, Type symbolType, String dataType, int line, int column) {
        this.name = name;
        this.symbolType = symbolType;
        this.dataType = dataType;
        this.line = line;
        this.column = column;
        this.accessModifier = "default";
        this.isStatic = false;
        this.isAbstract = false;
        this.isArray = false;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Type getSymbolType() { return symbolType; }
    public void setSymbolType(Type symbolType) { this.symbolType = symbolType; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public String getAccessModifier() { return accessModifier; }
    public void setAccessModifier(String accessModifier) { this.accessModifier = accessModifier; }
    
    public int getLine() { return line; }
    public void setLine(int line) { this.line = line; }
    
    public int getColumn() { return column; }
    public void setColumn(int column) { this.column = column; }
    
    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    
    public boolean isAbstract() { return isAbstract; }
    public void setAbstract(boolean isAbstract) { this.isAbstract = isAbstract; }
    
    public boolean isArray() { return isArray; }
    public void setArray(boolean isArray) { this.isArray = isArray; }
    
    public SymbolTable getLocalScope() { return localScope; }
    public void setLocalScope(SymbolTable localScope) { this.localScope = localScope; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s %-12s %-12s %-8s", 
            name, symbolType, dataType != null ? dataType : "N/A", accessModifier));
        
        if (isStatic) sb.append(" static");
        if (isAbstract) sb.append(" abstract");
        if (isArray) sb.append("[]");
        
        sb.append(String.format(" [%d:%d]", line, column));
        return sb.toString();
    }
    
    public String getFullSignature() {
        StringBuilder sb = new StringBuilder();
        if (!accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (isStatic) sb.append("static ");
        if (isAbstract) sb.append("abstract ");
        
        if (dataType != null && !symbolType.equals(Type.CLASS) && !symbolType.equals(Type.INTERFACE)) {
            sb.append(dataType);
            if (isArray) sb.append("[]");
            sb.append(" ");
        }
        
        sb.append(name);
        return sb.toString();
    }
}
