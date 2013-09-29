#!/bin/bash

javac -d bin -cp "./lib/*" src/HelloLamp.java || exit $?
java -cp "./bin/:./lib/*" HelloLamp
