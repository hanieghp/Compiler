@echo off
echo Building Java-- Compiler Project...

echo.
echo Cleaning previous build...
if exist build\*.class del /Q build\*.class

echo.
echo Compiling Phase 1 (Lexical Analysis)...
javac -d build src\phase1\*.java
if %errorlevel% neq 0 (
    echo Error compiling Phase 1!
    exit /b 1
)

echo.
echo Compiling Phase 2 (Symbol Table)...
javac -cp build -d build src\phase2\Symbol.java src\phase2\SymbolTable.java src\phase2\ManualSymbolTableBuilder.java
if %errorlevel% neq 0 (
    echo Error compiling Phase 2!
    exit /b 1
)

echo.
echo Compiling Main...
javac -cp build -d build src\Main.java
if %errorlevel% neq 0 (
    echo Error compiling Main!
    exit /b 1
)

echo.
echo Build completed successfully!
echo.
echo To run the program:
echo   java -cp build Main
