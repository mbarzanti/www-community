import java.util.HashSet;
 
public class ShortSet {
  public static void main(String[] args) {
	HashSet<Short> s = new HashSet<Short>();
	for (int i = 0; i < 10; i++) {
	  s.add((short)i); // Cast required so that the code compiles
	  s.remove(i); // Tries to remove an Integer
	}
	System.out.println(s.size());
  }
}