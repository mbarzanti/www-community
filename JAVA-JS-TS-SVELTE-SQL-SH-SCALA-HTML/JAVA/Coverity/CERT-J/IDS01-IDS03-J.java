// String s may be user controllable
// \uFE64 is normalized to < and \uFE65 is normalized to > using the NFKC normalization form
String s = "\uFE64" + "script" + "\uFE65";

// Validate
Pattern pattern = Pattern.compile("[<>]"); // Check for angle brackets
Matcher matcher = pattern.matcher(s);
if (matcher.find()) {
  // Found black listed tag
  throw new IllegalStateException();
} else {
  if (loginSuccessful) {
  logger.severe("User login succeeded for: " + username);
  } else {
	logger.severe("User login failed for: " + username);
  }
}

// Normalize
s = Normalizer.normalize(s, Form.NFKC);