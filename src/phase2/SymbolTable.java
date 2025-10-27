package phase2;

import java.util.*;

public class SymbolTable {
    private String scopeName;
    private SymbolTable parent;
    private Map<String, Symbol> symbols;
    private List<SymbolTable> children;
    private int scopeLevel;
    
    public SymbolTable(String scopeName, SymbolTable parent) {
        this.scopeName = scopeName;
        this.parent = parent;
        this.symbols = new LinkedHashMap<>();
        this.children = new ArrayList<>();
        this.scopeLevel = parent != null ? parent.scopeLevel + 1 : 0;
        
        if (parent != null) {
            parent.addChild(this);
        }
    }
    
    public void addSymbol(Symbol symbol) throws Exception {
        if (symbols.containsKey(symbol.getName())) {
            throw new Exception("Symbol '" + symbol.getName() + "' already exists in scope '" + scopeName + "'");
        }
        symbols.put(symbol.getName(), symbol);
    }
    
    public Symbol lookup(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        }
        
        if (parent != null) {
            return parent.lookup(name);
        }
        
        return null;
    }
    
    public Symbol lookupLocal(String name) {
        return symbols.get(name);
    }
    
    public void addChild(SymbolTable child) {
        children.add(child);
    }
    
    public List<Symbol> getAllSymbols() {
        return new ArrayList<>(symbols.values());
    }
    
    public List<Symbol> getSymbolsByType(Symbol.Type type) {
        List<Symbol> result = new ArrayList<>();
        for (Symbol symbol : symbols.values()) {
            if (symbol.getSymbolType() == type) {
                result.add(symbol);
            }
        }
        return result;
    }
    
    public void printTable() {
        printTable(0);
    }
    
    private void printTable(int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "=== " + scopeName + " (Level " + scopeLevel + ") ===");
        System.out.println(indent + String.format("%-15s %-12s %-12s %-8s %s", 
            "Name", "Type", "DataType", "Access", "Details"));
        System.out.println(indent + "-".repeat(70));
        
        if (symbols.isEmpty()) {
            System.out.println(indent + "No symbols in this scope");
        } else {
            for (Symbol symbol : symbols.values()) {
                System.out.println(indent + symbol.toString());
            }
        }
        
        System.out.println();
        
        for (SymbolTable child : children) {
            child.printTable(depth + 1);
        }
    }
    
    public void generateReport() {
        System.out.println("=".repeat(80));
        System.out.println("                        SYMBOL TABLE REPORT");
        System.out.println("=".repeat(80));
        
        Map<Symbol.Type, Integer> typeCount = new HashMap<>();
        countSymbolsByType(typeCount);
        
        System.out.println("STATISTICS:");
        System.out.println("-".repeat(40));
        for (Map.Entry<Symbol.Type, Integer> entry : typeCount.entrySet()) {
            System.out.println(String.format("%-15s: %d", entry.getKey(), entry.getValue()));
        }
        System.out.println("-".repeat(40));
        System.out.println(String.format("%-15s: %d", "Total Symbols", getTotalSymbolCount()));
        System.out.println(String.format("%-15s: %d", "Scope Levels", getMaxScopeLevel()));
        System.out.println();
        
        printTable();
        
        System.out.println("=".repeat(80));
    }
    
    private void countSymbolsByType(Map<Symbol.Type, Integer> typeCount) {
        for (Symbol symbol : symbols.values()) {
            typeCount.put(symbol.getSymbolType(), 
                typeCount.getOrDefault(symbol.getSymbolType(), 0) + 1);
        }
        
        for (SymbolTable child : children) {
            child.countSymbolsByType(typeCount);
        }
    }
    
    private int getTotalSymbolCount() {
        int count = symbols.size();
        for (SymbolTable child : children) {
            count += child.getTotalSymbolCount();
        }
        return count;
    }
    
    private int getMaxScopeLevel() {
        int maxLevel = scopeLevel;
        for (SymbolTable child : children) {
            maxLevel = Math.max(maxLevel, child.getMaxScopeLevel());
        }
        return maxLevel;
    }
    
    public String getScopeName() { return scopeName; }
    public SymbolTable getParent() { return parent; }
    public Map<String, Symbol> getSymbols() { return symbols; }
    public List<SymbolTable> getChildren() { return children; }
    public int getScopeLevel() { return scopeLevel; }
}
