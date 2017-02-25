#!/bin/bash

time mvn -T 4 clean test
test -f testerCDouter.zip && rm testerCDouter.zip && echo "Removing testerCDouter.zip"
zip -r testerCDouter.zip pom.xml *.in.out src/main/*

