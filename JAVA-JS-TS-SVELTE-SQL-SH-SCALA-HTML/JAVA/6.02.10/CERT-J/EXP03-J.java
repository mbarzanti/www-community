import java.util.Comparator;
 
static Comparator<Integer> cmp = new Comparator<Integer>() {
  public int compare(Integer i, Integer j) {
    return i < j ? -1 : (i == j ? 0 : 1);
  } 
};