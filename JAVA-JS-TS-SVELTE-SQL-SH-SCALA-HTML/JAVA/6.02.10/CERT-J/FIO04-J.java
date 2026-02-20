/*
Noncompliant Code Example (File Handle)
This noncompliant code example opens a file and uses it but fails to explicitly close the file:
*/
public int processFile(String fileName)
                       throws IOException, FileNotFoundException {
  FileInputStream stream = new FileInputStream(fileName);
  BufferedReader bufRead =
      new BufferedReader(new InputStreamReader(stream));
  String line;
  while ((line = bufRead.readLine()) != null) {
    sendLine(line);
  }
  return 1;
}