/*
Noncompliant Code Example
This noncompliant code example attempts to convert a BigInteger value to a String and then restore it to a BigInteger value. The toByteArray() method used returns a byte array containing the two's-complement representation of this BigInteger. The byte array is in big-endian byte order: the most significant byte is in the zeroth element. The program uses the String(byte[] bytes) constructor to create the string from the byte array. The behavior of this constructor when the given bytes are not valid in the default character set is unspecified, which is likely to be the case. Specifying the character set as a string also has unspecified behavior, although the Java API [API 2014] document claims that the String(byte[], Charset) method always replaces malformed-input and unmappable-character sequences with this character set's default replacement string. In any case, converting the String back to a BigInteger is unlikely to reproduce the original value. 
*/


BigInteger x = new BigInteger("530500452766");
byte[] byteArray = x.toByteArray();
String s = new String(byteArray);
byteArray = s.getBytes();
x = new BigInteger(byteArray);