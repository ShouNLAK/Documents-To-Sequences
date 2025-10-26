#!/bin/bash
# ============================================================================
# Document-to-Sequence Converter - Build & Run Script
# ============================================================================

build() {
    echo ""
    echo "================================================================================"
    echo "Building Document-to-Sequence Converter"
    echo "================================================================================"
    echo ""

    # Create target directory
    mkdir -p target/classes

    echo "Compiling Java source files..."

    # Compile all Java files
    javac -d target/classes -encoding UTF-8 \
        src/main/java/com/example/sequencer/preprocessing/*.java \
        src/main/java/com/example/sequencer/encoding/*.java \
        src/main/java/com/example/sequencer/vectorization/*.java \
        src/main/java/com/example/sequencer/model/*.java \
        src/main/java/com/example/sequencer/io/*.java \
        src/main/java/com/example/sequencer/utils/*.java \
        src/main/java/com/example/sequencer/pipeline/*.java \
        src/main/java/com/example/sequencer/core/*.java

    if [ $? -ne 0 ]; then
        echo ""
        echo "❌ Compilation failed! Please check the errors above."
        exit 1
    fi

    echo ""
    echo "✓ Compilation successful!"
    echo ""
    if [ -z "$1" ]; then
        echo "To run the application: ./build.sh run"
        echo "To test components: ./build.sh test"
        echo ""
    fi
}

run_app() {
    if [ ! -f "target/classes/com/example/sequencer/core/DocumentSequencerApplication.class" ]; then
        echo "Building first..."
        build "skip-message"
        if [ $? -ne 0 ]; then
            exit 1
        fi
    fi

    echo ""
    echo "================================================================================"
    echo "Document-to-Sequence Converter for Data Mining"
    echo "================================================================================"
    echo ""

    java -cp target/classes com.example.sequencer.core.DocumentSequencerApplication
    echo ""
}

run_test() {
    if [ ! -f "target/classes/com/example/sequencer/core/DocumentSequencerApplication.class" ]; then
        echo "Building first..."
        build "skip-message"
        if [ $? -ne 0 ]; then
            exit 1
        fi
    fi

    # Compile test if needed
    if [ ! -f "target/classes/com/example/sequencer/test/ManualTest.class" ]; then
        echo "Compiling test class..."
        javac -d target/classes -encoding UTF-8 -cp target/classes \
            src/main/java/com/example/sequencer/test/ManualTest.java
        
        if [ $? -ne 0 ]; then
            echo "❌ Test compilation failed!"
            exit 1
        fi
    fi

    echo ""
    echo "================================================================================"
    echo "Running Component Tests"
    echo "================================================================================"
    echo ""

    java -cp target/classes com.example.sequencer.test.ManualTest
    echo ""
}

# Main script logic
case "$1" in
    run)
        run_app
        ;;
    test)
        run_test
        ;;
    *)
        build "$1"
        ;;
esac
