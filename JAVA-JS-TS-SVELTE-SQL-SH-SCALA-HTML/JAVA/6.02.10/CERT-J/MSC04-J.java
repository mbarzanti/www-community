/*
Noncompliant Code Example (Off-by-One Programming Error)
The vector object in the noncompliant code example leaks memory. The condition for removing the vector element is mistakenly written as n > 0 instead of n >= 0. Consequently, the method fails to remove one element per invocation and quickly exhausts the available heap space.
*/
public class Leak {
  static Vector vector = new Vector();

  public void useVector(int count) { 	
    for (int n = 0; n < count; n++) {
      vector.add(Integer.toString(n));
    }
    // ...
    for (int n = count - 1; n > 0; n--) { // Free the memory
      vector.removeElementAt(n);
    }	
  }

  public static void main(String[] args) throws IOException {
    Leak le = new Leak();
    int i = 1;
    while (true) {
      System.out.println("Iteration: " + i);
      le.useVector(1);
      i++;
    }
  }
}
/*
Noncompliant Code Example (Nonlocal Instance Field)
This noncompliant code example declares and allocates a HashMap instance field that is used only in the doSomething() method:

Programmers may be surprised that the HashMap persists for the entire lifetime of the Storer instance.
*/

public class Storer {
  private HashMap<Integer,String> hm = new HashMap<Integer, String>();
  
  private void doSomething() {
    // hm is used only here and never referenced again
    hm.put(1, "java");
    // ...
  }
}
Noncompliant Code Example (Lapsed Listener)
This noncompliant code example, known as the Lapsed Listener [Goetz 2005a], demonstrates unintentional object retention. The button continues to hold a reference of the reader object after completion of the readSomething() method, even though the reader object is never used again. Consequently, the garbage collector cannot collect the reader object. A similar problem occurs with inner classes because they hold an implicit reference to the enclosing class.



public class LapseEvent extends JApplet {
  JButton button;
  public void init() {
    button = new JButton("Click Me");
    getContentPane().add(button, BorderLayout.CENTER);
    Reader reader = new Reader();
    button.addActionListener(reader);
    try {
      reader.readSomething();
    } catch (IOException e) { 
      // Handle exception 
    }		 
  }
}

class Reader implements ActionListener {
  public void actionPerformed(ActionEvent e)  {
    Toolkit.getDefaultToolkit().beep();
  }
  public void readSomething() throws IOException {
    // Read from file
  }
}
/*
Noncompliant Code Example (Exception before Remove)
This noncompliant code example attempts to remove the reader through use of the removeActionListener() method:

If an exception is thrown by the readSomething() method, the removeActionListener() statement is never executed.
*/

Reader reader = new Reader();
button.addActionListener(reader);
try {
  reader.readSomething();  // Can skip next line of code
  // Dereferenced, but control flow can change
  button.removeActionListener(reader);  
} catch (IOException e) { 
  // Forward to handler 
}
