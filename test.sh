#!/bin/bash

# Java Serializer/Deserializer Test Script
# Runs all tests for the project

echo "Running Java Serializer/Deserializer tests..."

# Check if bin directory exists
if [ ! -d "bin" ]; then
    echo "Error: bin directory not found. Please run ./build.sh first."
    exit 1
fi

# Run all tests using TestRunner
echo ""
echo "=== Running All Tests ==="
java -cp bin com.pjr22.serialization.test.TestRunner

# Check exit code
if [ $? -eq 0 ]; then
    echo ""
    echo "All tests passed successfully."
else
    echo ""
    echo "Some tests failed."
    exit 1
fi
