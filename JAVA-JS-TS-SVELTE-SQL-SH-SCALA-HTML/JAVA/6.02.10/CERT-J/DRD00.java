private String filename = "myfile"

private String string = "sensitive data such as credit card number"
FileOutputStream fos = null;

try {
  File file = new File(getExternalFilesDir(TARGET_TYPE), filename);
  fos = new FileOutputStream(file, false);
  fos.write(string.getBytes());
} catch (FileNotFoundException e) {
  // handle FileNotFoundException
} catch (IOException e) {
  // handle IOException
} finally {
  if (fos != null) {
    try {
  	fos.close();
    } catch (IOException e) {
      // handle error
    }
  }
}

