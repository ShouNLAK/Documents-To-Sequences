@echo off
REM ============================================================================
REM Document-to-Sequence Converter - Build & Run Script
REM ============================================================================

if "%1"=="run" goto :run
if "%1"=="auto" goto :auto
if "%1"=="test" goto :test

:build
echo.
echo ================================================================================
echo Building Document-to-Sequence Converter
echo ================================================================================
echo.

REM Create target directory
if not exist "target\classes" mkdir target\classes

echo Compiling Java source files...

REM Compile all Java files
javac -d target\classes -encoding UTF-8 ^
    src\main\java\com\example\sequencer\preprocessing\*.java ^
    src\main\java\com\example\sequencer\encoding\*.java ^
    src\main\java\com\example\sequencer\vectorization\*.java ^
    src\main\java\com\example\sequencer\model\*.java ^
    src\main\java\com\example\sequencer\io\*.java ^
    src\main\java\com\example\sequencer\utils\*.java ^
    src\main\java\com\example\sequencer\pipeline\*.java ^
    src\main\java\com\example\sequencer\core\*.java

if %errorlevel% neq 0 (
    echo.
    echo ❌ Compilation failed! Please check the errors above.
    pause
    exit /b 1
)

echo.
echo ✓ Compilation successful!
echo.
if "%1"=="" (
    echo To run the application: build.bat run
    echo To run auto-mode with HTML report: build.bat auto
    echo To test components: build.bat test
    echo.
    pause
)
exit /b 0

:run
if not exist "target\classes\com\example\sequencer\core\DocumentSequencerApplication.class" (
    echo Building first...
    call :build
    if %errorlevel% neq 0 exit /b 1
)

echo.
echo ================================================================================
echo Document-to-Sequence Converter for Data Mining
echo ================================================================================
echo.

java -cp target\classes com.example.sequencer.core.DocumentSequencerApplication
echo.
pause
exit /b 0

:auto
if not exist "target\classes\com\example\sequencer\core\AutoRunner.class" (
    echo Building first...
    call :build
    if %errorlevel% neq 0 exit /b 1
)

echo.
echo ================================================================================
echo Auto-Runner: Document-to-Sequence with HTML Report
echo ================================================================================
echo.

java -cp target\classes com.example.sequencer.core.AutoRunner
echo.
echo ================================================================================
echo HTML Report: Data/Output/report.html
echo ================================================================================
echo.
pause
exit /b 0

:test
if not exist "target\classes\com\example\sequencer\core\DocumentSequencerApplication.class" (
    echo Building first...
    call :build
    if %errorlevel% neq 0 exit /b 1
)

REM Compile test if needed
if not exist "target\classes\com\example\sequencer\test\ManualTest.class" (
    echo Compiling test class...
    javac -d target\classes -encoding UTF-8 -cp target\classes ^
        src\main\java\com\example\sequencer\test\ManualTest.java
    
    if %errorlevel% neq 0 (
        echo ❌ Test compilation failed!
        pause
        exit /b 1
    )
)

echo.
echo ================================================================================
echo Running Component Tests
echo ================================================================================
echo.

java -cp target\classes com.example.sequencer.test.ManualTest
echo.
pause
exit /b 0
