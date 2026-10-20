#!/usr/bin/env zsh

./gradlew sortDependencies versionCatalogFormat
# ./gradlew ktlintFormat
# ./gradlew ktlintGenerateBaseline
# ./gradlew updateLintBaseline
# ./gradlew detektBaseline
./gradlew lint
