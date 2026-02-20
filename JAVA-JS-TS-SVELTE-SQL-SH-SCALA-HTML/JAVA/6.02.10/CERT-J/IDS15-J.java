class ExceptionExample {
  public static void main(String[] args) throws FileNotFoundException {
    // Linux stores a user's home directory path in 
    // the environment variable $HOME, Windows in %APPDATA%
    FileInputStream fis =
        new FileInputStream(System.getenv("APPDATA") + args[0]);  
	try {
	  FileInputStream fis =
		  new FileInputStream(System.getenv("APPDATA") + args[0]);
	} catch (FileNotFoundException e) {
	  // Log the exception
	  throw new IOException("Unable to retrieve file", e);
	}
  }
}
class SecurityIOException extends IOException {/* ... */};

try {
  FileInputStream fis =
      new FileInputStream(System.getenv("APPDATA") + args[0]);
} catch (FileNotFoundException e) {
  // Log the exception
  throw new SecurityIOException();
}
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

openFileOutput("someFile", MODE_WORLD_READABLE);
openFileOutput("someFile", MODE_PRIVATE); // Ok
