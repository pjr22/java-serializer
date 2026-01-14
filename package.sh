#!/bin/bash

# Java Serializer/Deserializer Package Script
# Builds library jar and source jar files

echo "Packaging Java Serializer/Deserializer library..."

# Check if bin directory exists
if [ ! -d "bin" ]; then
    echo "Error: bin directory not found. Please run ./build.sh first."
    exit 1
fi

# Create library jar
echo ""
echo "Creating library jar: java-serializer.jar"
jar cf java-serializer.jar -C bin com

# Create source jar
echo "Creating source jar: java-serializer-sources.jar"
jar cf java-serializer-sources.jar -C src com

# Generate javadoc
echo ""
echo "Generating javadoc..."
javadoc -d docs/javadoc -sourcepath src -subpackages com.pjr22.serialization -exclude com.pjr22.serialization.test -quiet -Xdoclint:none

# Create javadoc jar
echo ""
echo "Creating javadoc jar: java-serializer-javadoc.jar"
jar cf java-serializer-javadoc.jar -C docs/javadoc .

echo ""
echo "Package completed successfully."
echo "Generated files:"
echo "  - java-serializer.jar (library)"
echo "  - java-serializer-sources.jar (source)"
echo "  - java-serializer-javadoc.jar (javadoc)"
echo "  - docs/javadoc/ (API documentation)"
