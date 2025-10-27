# Java-- Compiler

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com)
[![ANTLR](https://img.shields.io/badge/ANTLR-4.13.2-blue?style=for-the-badge)](https://www.antlr.org/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

A comprehensive compiler implementation for the **Java-- (Java Minus Minus)** programming language, developed as part of a Compiler Design course. This project demonstrates the implementation of lexical analysis and symbol table construction using both manual DFA construction and ANTLR grammar parsing.

## 🚀 Features

- **Phase 1**: Complete lexical analysis with hand-crafted DFA implementation
- **Phase 2**: Symbol table construction with hierarchical scope management
- **Manual DFA Construction**: No external regex libraries used for tokenization
- **ANTLR Integration**: Grammar-based parsing for symbol table generation
- **Comprehensive Testing**: Extensive test cases for all language constructs

## 📁 Project Structure

```
Compiler/
├── 📂 src/                    # Source code
│   ├── 📂 phase1/            # Phase 1: Lexical Analysis
│   │   ├── Lexer.java        # Main lexer implementation
│   │   ├── LexerToken.java   # Token representation
│   │   ├── TokenType.java    # Token type enumeration
│   │   ├── DFABuilder.java   # DFA construction utility
│   │   └── DFAState.java     # DFA state representation
│   ├── 📂 phase2/            # Phase 2: Symbol Table
│   │   ├── Symbol.java       # Symbol representation
│   │   ├── SymbolTable.java  # Symbol table implementation
│   │   └── ManualSymbolTableBuilder.java  # Symbol table builder
│   └── Main.java             # Main test program
├── 📂 generated/             # ANTLR generated files
├── 📂 build/                 # Compiled classes
├── 📂 lib/                   # External libraries
│   └── antlr-4.13.2-complete.jar
├── 📂 docs/                  # Documentation
├── build.bat                 # Build script
├── run.bat                   # Run script
└── README.md
```

## 🔧 Implementation Details

### Phase 1: Lexical Analysis
- **Manual DFA Implementation**: Hand-crafted finite automata for token recognition
- **No External Libraries**: All pattern matching implemented from scratch
- **Complete Token Support**: Keywords, operators, identifiers, literals, and comments
- **Error Handling**: Robust error detection and reporting

### Phase 2: Symbol Table Construction
- **ANTLR Grammar**: Uses ANTLR 4.13.2 for parsing (as per project requirements)
- **Hierarchical Scoping**: Multi-level scope management
- **Symbol Recognition**: Classes, interfaces, methods, fields, variables, and parameters
- **Access Modifiers**: Support for public, private, protected, and internal modifiers

## 🎯 Supported Language Features

### Token Recognition
- **Keywords**: `class`, `public`, `private`, `static`, `void`, `int`, `boolean`, `interface`, `abstract`, etc.
- **Operators**: `+`, `-`, `*`, `/`, `%`, `**`, `==`, `!=`, `<=`, `>=`, `&&`, `||`, `!`, etc.
- **Delimiters**: `{`, `}`, `[`, `]`, `(`, `)`, `;`, `,`, `.`
- **Literals**: Integer numbers, strings, characters, boolean values
- **Comments**: Single-line (`//`) and multi-line (`/* */`)

### Symbol Types
- 🏛️ **CLASS**: Class definitions
- 🔌 **INTERFACE**: Interface definitions
- ⚙️ **METHOD**: Methods (regular and abstract)
- 🏗️ **CONSTRUCTOR**: Class constructors
- 📊 **FIELD**: Class fields and attributes
- 📝 **VARIABLE**: Local variables
- 📋 **PARAMETER**: Method parameters

### Symbol Table Features
- **Hierarchical Scoping**: Multi-level scope management with parent-child relationships
- **Access Modifiers**: Recognition of `public`, `private`, `protected` modifiers
- **Static & Abstract**: Support for static methods and abstract declarations
- **Array Detection**: Recognition of array types and dimensions
- **Comprehensive Reporting**: Detailed statistics and scope visualization

## 🛠️ Getting Started

### Prerequisites
- Java 8 or higher
- Windows environment (for batch scripts) or modify scripts for your OS

### Quick Start
1. **Clone the repository**
   ```bash
   git clone https://github.com/hanieghp/Compiler.git
   cd Compiler
   ```

2. **Build the project**
   ```batch
   build.bat
   ```

3. **Run the compiler**
   ```batch
   run.bat
   ```

### Manual Compilation
```batch
# Compile Phase 1 (Lexical Analysis)
javac -d build src\phase1\*.java

# Compile Phase 2 (Symbol Table)
javac -cp build -d build src\phase2\*.java

# Compile Main test program
javac -cp build -d build src\Main.java
```

### Manual Execution
```batch
java -cp build Main
```

## 📊 Sample Output

The compiler runs comprehensive tests for both phases:

1. **Lexer Tests**: Displays tokens generated from sample Java-- code
2. **Symbol Table Tests**: Shows hierarchical symbol tables with statistics

### Sample Symbol Table Output
```
=== Symbol Table Test: Simple Hello World ===
STATISTICS:
METHOD         : 1
PARAMETER      : 1  
CLASS          : 1
Total Symbols  : 3
Scope Levels   : 2

=== Global (Level 0) ===
HelloWorld      CLASS        N/A          default
  === HelloWorld (Level 1) ===
  main            METHOD       void         public static
    === main (Level 2) ===
    args            PARAMETER    String[]     default
```

### Lexer Token Output
```
=== Lexer Test: Simple Program ===
Token: [KEYWORD] 'class' at line 1
Token: [IDENTIFIER] 'HelloWorld' at line 1
Token: [DELIMITER] '{' at line 1
Token: [KEYWORD] 'public' at line 2
Token: [KEYWORD] 'static' at line 2
Token: [KEYWORD] 'void' at line 2
Token: [IDENTIFIER] 'main' at line 2
...
```

## 🏗️ Architecture & Design

### Phase 1 Design Principles
- **No Regex Dependencies**: All pattern matching implemented using hand-crafted DFA
- **Error Handling**: Comprehensive detection and reporting of invalid tokens
- **Optimization**: Accepting states used for longest match principle
- **Modularity**: Clean separation between DFA construction and token recognition

### Phase 2 Design Principles
- **Simple Parser**: Lightweight parser implementation with minimal ANTLR dependencies
- **Stack-based Scope Management**: Efficient hierarchical scope handling
- **Robust Error Handling**: Exception handling for syntax and semantic errors
- **Extensibility**: Modular design allows easy addition of new symbol types

## 📦 Package Structure

The project follows a clean package organization:
- **`phase1.*`**: Lexical analysis components
  - Core lexer implementation
  - Token definitions and types
  - DFA construction utilities
- **`phase2.*`**: Symbol table components
  - Symbol representation and management
  - Hierarchical scope implementation
  - Symbol table construction logic

## ⚠️ Current Limitations

- **Language Subset**: Currently supports core Java-- language constructs
- **Grammar Coverage**: Some advanced grammar features are not yet implemented
- **Testing Scope**: Tests are performed on a limited set of sample programs
- **Platform**: Batch scripts are Windows-specific (easily adaptable to other platforms)

## 🧪 Testing

The project includes comprehensive test cases:
- **Lexer Tests**: Various token recognition scenarios
- **Symbol Table Tests**: Class hierarchies, method overloading, scope resolution
- **Error Handling**: Invalid syntax and semantic error detection
- **Edge Cases**: Boundary conditions and special character handling

## 📚 Educational Value

This project demonstrates:
- **Compiler Theory**: Practical implementation of lexical analysis and symbol tables
- **DFA Construction**: Manual finite automata implementation without libraries
- **Grammar Processing**: Integration with ANTLR for parsing
- **Software Engineering**: Clean architecture, proper package organization, and documentation

## 🤝 Contributing

This is an educational project for a Compiler Design course. Contributions are welcome for:
- Additional test cases
- Bug fixes
- Documentation improvements
- Cross-platform compatibility

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

**Compiler Design Course Project**
- Implementation: Course participants
- Supervision: Academic staff

## 🔗 References

- [ANTLR 4 Documentation](https://www.antlr.org/)
- [Compiler Design Principles](https://en.wikipedia.org/wiki/Compiler)
- [Java Language Specification](https://docs.oracle.com/javase/specs/)