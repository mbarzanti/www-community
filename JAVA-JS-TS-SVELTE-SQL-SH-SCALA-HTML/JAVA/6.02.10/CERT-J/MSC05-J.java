/*
Noncompliant Code Example (readLine())
This noncompliant code example reads lines of text from a file and adds each one to a vector until a line with the word "quit" is encountered:

The code places no upper bounds on the memory space required to execute the program. Consequently, the program can easily exhaust the available heap space in two ways. First, an attacker can supply arbitrarily many lines in the file, causing the vector to grow until memory is exhausted. Second, an attacker can simply supply an arbitrarily long line, causing the readLine() method to exhaust memory. According to the Java API documentation [API 2014], the BufferedReader.readLine() method

Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.

Any code that uses this method is susceptible to a resource exhaustion attack because the user can enter a string of any length.
*/

class ReadNames {
  private Vector<String> names = new Vector<String>();
  private final InputStreamReader input;
  private final BufferedReader reader;

  public ReadNames(String filename) throws IOException {
    this.input = new FileReader(filename);
    this.reader = new BufferedReader(input);
  }

  public void addNames() throws IOException {
    try {
      String newName;
      while (((newName = reader.readLine()) != null) &&
             !(newName.equalsIgnoreCase("quit"))) {
        names.addElement(newName);
        System.out.println("adding " + newName);
      }
    } finally {
      input.close();
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Arguments: [filename]");
      return;
    }
    ReadNames demo = new ReadNames(args[0]);
    demo.addNames();
  }
}
/*
Noncompliant Code Example
In a server-class machine using a parallel garbage collector, the default initial and maximum heap sizes are as follows for Java SE 6 [Sun 2006]:

Initial heap size: larger of 1/64 of the machine's physical memory or some reasonable minimum.
Maximum heap size: smaller of 1/4 of the physical memory or 1GB.
This noncompliant code example requires more memory on the heap than is available by default:



Assuming the heap size as 512 MB 
 * (calculated as 1/4 of 2GB RAM = 512MB)
 * Considering long values being entered (64 bits each, 
 * the max number of elements would be 512MB/64 bits = 
 * 67108864)
 */
public class ReadNames {
  // Accepts unknown number of records
  Vector<Long> names = new Vector<Long>(); 
  long newID = 0L;
  int count = 67108865;
  int i = 0;
  InputStreamReader input = new InputStreamReader(System.in);
  Scanner reader = new Scanner(input);

  public void addNames() {
    try {
      do {
        // Adding unknown number of records to a list
        // The user can enter more IDs than the heap can support and,
        // as a result, exhaust the heap. Assume that the record ID
        // is a 64-bit long value
        System.out.print("Enter recordID (To quit, enter -1): ");
        newID = reader.nextLong();

        names.addElement(newID);
        i++;
      } while (i < count || newID != -1);
    } finally {
      input.close();
    }
  }

  public static void main(String[] args) {
    ReadNames demo = new ReadNames();
    demo.addNames();
  }
}
