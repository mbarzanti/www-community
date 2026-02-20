/*
Non-Compliant Code Example

This non-compliant code deserializes a byte array without first validating what classes will be created. If the object being deserialized belongs to a class with a lax readObject() method, this could result in remote code execution.
*/


import java.io.*;

class DeserializeExample {
  public static Object deserialize(byte[] buffer) throws IOException, ClassNotFoundException {
    Object ret = null;
    try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer)) {
      try (ObjectInputStream ois = new ObjectInputStream(bais)) {
        ret = ois.readObject();
      }
    }
    return ret;
  }
} 