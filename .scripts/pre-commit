#!/bin/bash

stagedFiles=$(git diff --staged --name-only)

echo "Running spotless check"
./gradlew spotlessApply

if [ $? -eq 0 ]; then
    echo "spotless check succeed"
    for file in $stagedFiles; do
        if test -f "$file"; then
            git add $file
        fi
    done
else
    echo "spotless check failed" >&2
    exit 1
fi