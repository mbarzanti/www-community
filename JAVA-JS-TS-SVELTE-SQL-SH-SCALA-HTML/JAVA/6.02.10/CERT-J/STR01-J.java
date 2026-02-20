/*
Noncompliant Code Example
This noncompliant code example attempts to trim leading letters from string:

Unfortunately, the trim() method may fail because it is using the character form of the Character.isLetter() method. Methods that accept only a char value cannot support supplementary characters. According to the Java API [API 2014] class Character documentation:

They treat char values from the surrogate ranges as undefined characters. For example, Character.isLetter('\uD840') returns false, even though this specific value if followed by any low-surrogate value in a string would represent a letter.
*/

public static String trim(String string) {
  char ch;
  int i;
  for (i = 0; i < string.length(); i += 1) {
    ch = string.charAt(i);
    if (!Character.isLetter(ch)) {
      break;
    }
  }
  return string.substring(i);
}
