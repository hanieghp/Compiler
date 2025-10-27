import java.util.List;

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

    }

    private static void testLexer(String code) {
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}