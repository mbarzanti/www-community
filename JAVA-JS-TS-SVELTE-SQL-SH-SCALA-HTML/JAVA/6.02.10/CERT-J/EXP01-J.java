public static int cardinality(Object obj, final Collection<?> col) {
  int count = 0;
  if (col == null) {
    return count;
  }
  Iterator<?> it = col.iterator();
  while (it.hasNext()) {
    Object elt = it.next();
    if ((null == obj && null == elt) || obj.equals(elt)) {  // Null pointer dereference
      count++;
    }
  }
  return count;
}