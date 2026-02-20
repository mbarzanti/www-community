#!/bin/bash
# see: https://www.geeksforgeeks.org/java/verification-java-jvm/
# The bytecode verification process runs by default. The -Xverify:none flag on the # JVM command line suppresses the verification process. This noncompliant code example # uses the flag to disable bytecode verification:
java -Xverify:none ApplicationName
java -noverify ApplicationName