#!/bin/bash

#mvn clean test
#if [ -f testerCDouter.zip ] ; then
#    rm testerCDouter.zip
#fi
test -f testerCDouter.zip && rm testerCDouter.zip && echo "Removing testerCDouter.zip"
zip -r testerCDouter.zip pom.xml *.in.out src/main/*

