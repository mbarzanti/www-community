private ArrayList<String> names;

void process(int index) {
  assert names.remove(null); // Side effect 
  // ...
}
