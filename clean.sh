#!/bin/bash

# Java Serializer/Deserializer Clean Script
# Removes build artifacts and generated jar files

echo "Cleaning Java Serializer/Deserializer project..."

# Remove bin directory
if [ -d "bin" ]; then
    echo "Removing bin/ directory..."
    rm -rf bin
fi

# Remove generated jar files
if [ -f "java-serializer.jar" ]; then
    echo "Removing java-serializer.jar..."
    rm -f java-serializer.jar
fi

if [ -f "java-serializer-sources.jar" ]; then
    echo "Removing java-serializer-sources.jar..."
    rm -f java-serializer-sources.jar
fi

echo "Clean completed."
