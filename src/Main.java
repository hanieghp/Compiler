import java.util.List;
import phase1.*;
import phase2.*;

public class Main {
    public static void main(String[] args) {
        String code1 = 
    "class HelloWorld {\n" +
    "    public static void main(String[] args) {\n" +
    "        print(\"Hello, World!\");\n" +
    "    }\n" +
    "}";


        System.out.println("=== Test 1: Simple Hello World ===");
        testLexer(code1);

        String code2 =
            "class Calculator {\n" +
            "    public int calculate(int x, int y) {\n" +
            "        int sum = x + y;\n" +
            "        int product = x * y;\n" +
            "        int power = x ** y;\n" +
            "        return sum;\n" +
            "    }\n" +
            "}";

        System.out.println("\n=== Test 2: Calculator with Operators ===");
        testLexer(code2);

        String code3 =
            "class Loop {\n" +
            "    public void loop() {\n" +
            "        for (int i = 0; i < 10; i = i + 1) {\n" +
            "            if (i % 2 == 0) {\n" +
            "                print(i);\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";

        System.out.println("\n=== Test 3: Control Structures ===");
        testLexer(code3);

        String code4 =
            "class Arrays {\n" +
            "    public void test() {\n" +
            "        int[] arr = new int[10];\n" +
            "        int len = arr.length;\n" +
            "        String str = \"Hello\";\n" +
            "        char c = 'A';\n" +
            "    }\n" +
            "}";

        System.out.println("\n=== Test 4: Arrays and Literals ===");
        testLexer(code4);

        String code5 =
            "// This is a line comment\n" +
            "class Test {\n" +
            "    /* This is a \n" +
            "       multi-line comment */\n" +
            "    public int x;\n" +
            "}";

        System.out.println("\n=== Test 5: Comments ===");
        testLexer(code5);

        String code6 =
            "class Compare {\n" +
            "    boolean test(int a, int b) {\n" +
            "        return a < b && a <= b && a > b && a >= b && a == b && a != b || false;\n" +
            "    }\n" +
            "}";

        System.out.println("\n=== Test 6: Comّparison Operators ===");
        testLexer(code6);

        // Symbol Table
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    SYMBOL TABLE TESTS");
        System.out.println("=".repeat(80));

        testSymbolTable(code1, "Simple Hello World");
        testSymbolTable(code2, "Calculator with Operators");
        testSymbolTable(code3, "Control Structures");
        testSymbolTable(code4, "Arrays and Literals");

        String complexCode = 
            "class Calculator {\n" +
            "    private int result;\n" +
            "    public static final int MAX_VALUE = 100;\n" +
            "\n" +
            "    public Calculator() {\n" +
            "        result = 0;\n" +
            "    }\n" +
            "\n" +
            "    public int add(int a, int b) {\n" +
            "        int sum = a + b;\n" +
            "        result = sum;\n" +
            "        return sum;\n" +
            "    }\n" +
            "\n" +
            "    public void printResult() {\n" +
            "        print(result);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "interface Drawable {\n" +
            "    void draw();\n" +
            "    int getArea();\n" +
            "}";

        testSymbolTable(complexCode, "Complex Class with Interface");

    }

    private static void testLexer(String code) {
        Lexer lexer = new Lexer(code);
        List<LexerToken> tokens = lexer.tokenize();
        
        for (LexerToken token : tokens) {
            System.out.println(token);
        }
    }
    
    private static void testSymbolTable(String code, String testName) {
        System.out.println("\n=== Symbol Table Test: " + testName + " ===");
        
        try {
            ManualSymbolTableBuilder builder = new ManualSymbolTableBuilder();
            SymbolTable symbolTable = builder.buildSymbolTable(code);
            
            if (symbolTable != null) {
                symbolTable.generateReport();
            } else {
                System.out.println("Failed to build symbol table");
            }
            
        } catch (Exception e) {
            System.err.println("Error in symbol table test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}