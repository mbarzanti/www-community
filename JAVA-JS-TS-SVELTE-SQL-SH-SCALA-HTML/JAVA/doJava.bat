dir /b /s *.java > sources.txt
javac -encoding ISO-8859-1 -d out @sources.txt 2>result.txt