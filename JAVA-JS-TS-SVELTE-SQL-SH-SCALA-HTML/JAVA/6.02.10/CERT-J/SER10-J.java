/*
Noncompliant Code Example
This noncompliant code example reads and serializes data from an external sensor. Each invocation of the readSensorData() method returns a newly created SensorData instance, each containing one megabyte of data. SensorData instances are pure data streams, containing data and arrays but lacking references to other SensorData objects.

As already described, the ObjectOutputStream maintains a cache of previously written objects. Consequently, all SensorData objects remain alive until the cache itself becomes garbage-collected. An OutOfMemoryError can occure because the stream remains open while new objects are being written to it.
*/


class SensorData implements Serializable {
  // 1 MB of data per instance!
  ... 
  public static SensorData readSensorData() {...}
  public static boolean isAvailable() {...}
}

class SerializeSensorData {
  public static void main(String[] args) throws IOException {
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(
          new BufferedOutputStream(new FileOutputStream("ser.dat")));
      while (SensorData.isAvailable()) {
        // Note that each SensorData object is 1 MB in size
        SensorData sd = SensorData.readSensorData();
        out.writeObject(sd);
      }
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }
}