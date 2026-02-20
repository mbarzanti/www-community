/*
Noncompliant Code Example (delete())
This noncompliant code example attempts to delete a specified file but gives no indication of its success. The Java platform requires File.delete() to throw a SecurityException only when the program lacks authorization to delete the file [API 2014]. No other exceptions are thrown, so the deletion can silently fail.
*/


File file = new File(args[0]);
file.delete();