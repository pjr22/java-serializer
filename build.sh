#!/bin/bash

# Java Serializer/Deserializer Build Script
# Compiles all source files to the bin directory

echo "Building Java Serializer/Deserializer library..."

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all source files with -parameters flag to preserve parameter names
javac -Xlint:deprecation --release 17 -parameters -g -d bin -sourcepath src src/com/pjr22/serialization/**/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Build completed successfully."
    echo "Compiled classes are in: bin/"
else
    echo "Build failed."
    exit 1
fi
