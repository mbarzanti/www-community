/*
Noncompliant Code Example (toUpperCase())
Many web apps, such as forum or blogging software, input HTML and then display it. Displaying untrusted HTML can subject a web app to cross-site scripting (XSS) or HTML injection vulnerabilities. Therefore, it is vital that HTML be sanitized before sending it to a web browser.

One common step in sanitization is identifying tags that may contain malicious content. The <SCRIPT> tag typically contains JavaScript code that is executed by a client's browser. Consequently, HTML input is commonly filtered for <SCRIPT> tags. However, identifying <SCRIPT> tags is not as simple as it appears.

In HTML, tags are case-insensitive and consequently can be specified using uppercase, lowercase, or any mixture of cases. This noncompliant code example uses the locale-dependent String.toUpperCase() method to convert an HTML tag to uppercase to check it for further processing. The code must ignore <SCRIPT> tags, as they indicate code that is to be discarded. Whereas the English locale would convert "script" to "SCRIPT", the Turkish locale will convert "script" to "SCRİPT", and the check will fail to detect the <SCRIPT> tag.
*/
public static void processTag(String tag) {
  if (tag.toUpperCase().equals("SCRIPT")) {
    return;
  } 
  // Process tag
}

/*
Noncompliant Code Example (FileReader)
Java provides classes for handling input and output, which can be based on either bytes or characters. The byte I/O families derive from the InputStream and OutputStream interfaces and are independent of locale or character encoding. However, the character I/O families derive from Reader and Writer, and they must convert byte sequences into strings and back, so they rely on a specified character encoding to do their conversion. This encoding is indicated by the file.encoding system property, which is part of the current locale. Consequently, a file encoded with one encoding, such as UTF-8, must not be read by a character input method using a different encoding, such as UTF-16.

Programs that read character data (whether directly using a Reader or indirectly using some method such as constructing a String from a byte array) must be aware of the source of the data. If the encoding of the data is fixed (such as if the data comes from a file resource that is shipped with the program), then that encoding must be specified by the program. Failure to specify the coding enables an attacker to change the encoding to force the program to read the data using the wrong encoding.

This risk does not apply to programs that read data known to be in the encoding specified by the platform running the program. For example, if the program must open a file provided by the user, it is reasonable to rely on the default encoding, expecting that it will be set correctly.

This noncompliant code example reads its own source code and prints it out, prepending each line with a line number. If the program is run with the argument -Dfile.encoding=UTF16 while its source file is stored as UTF8, the program will save garbage in the output file.

*/

import java.io.*;

public class PrintMyself {
  private static String inputFile = "PrintMyself.java";
  private static String outputFile = "PrintMyself.txt";

  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
    int line = 0;
    while (reader.ready()) {
      line++;
      writer.println(line + ": " + reader.readLine());
    }
    reader.close();
    writer.close();
  }
}

/*
Noncompliant Code Example (Date)
The concepts of days and years are universal, but the way in which dates are represented varies across cultures and are therefore specific to locales. This noncompliant code example examines the current date and prints one of two messages, depending on whether or not the month is June:
This program behaves as expected on platforms with a US locale:

The date is Jun 20, 2014
Enjoy June!
but fails on other locales. For example, the output for a German locale (specified by -Duser.language=de) is

The date is 20.06.2014
It's not June.

*/

import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;

// ...

public static void isJune(Date date) {
  String myString = DateFormat.getDateInstance().format(date);
  System.out.println("The date is " + myString);
  if (myString.startsWith("Jun ")) {
    System.out.println("Enjoy June!");
  } else {
    System.out.println("It's not June.");
  }
}
