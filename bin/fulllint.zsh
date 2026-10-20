#!/usr/bin/env zsh

./gradlew sortDependencies versionCatalogFormat ktlintFormat                                                                             
./gradlew lint 
#./gradlew ktlintGenerateBaseline updateLintBaseline detektBaseline
./gradlew ktlintGenerateBaseline updateLintBaseline
# ./gradlew lint projectHealth
