// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

/** NMEA examples from VLP-16
 * $GPRMC,214431,A,3707.7937,N,12139.2432,W,000.0,325.8,230715,013.8,E,D*0F
 * $GPRMC,214432,A,3707.7939,N,12139.2428,W,001.4,175.2,230715,013.8,E,D*01
 * 
 * at Duebendorf
 * $GPRMC,155524,A,4724.3266,N,00837.8624,E,002.0,172.1,131217,001.8,E,A*10
 * 
 * in VLP-16 lcm package the $GPRMC is at byte offset 218 */
public class VelodynePosEvent {
  /** when reading from lcm log file the byteBuffer should be at position 12 TODO where do these 12 bytes come from ?
   * 
   * @param byteBuffer
   * @return */
  public static VelodynePosEvent vlp16(ByteBuffer byteBuffer) {
    final int offset = byteBuffer.position(); // 0 or 42 in pcap file
    byteBuffer.position(offset + 198); // unused
    int gps_usec = byteBuffer.getInt(); // TODO from the hour?
    byteBuffer.getInt(); // unused
    byte[] nmea = new byte[72]; // NMEA positioning sentence
    byteBuffer.get(nmea);
    return new VelodynePosEvent(gps_usec, new String(nmea));
  }

  // ---
  /** number of microseconds past the hour per UTC time */
  private final int gps_usec;
  /** The Validity field in the $GPRMC message (‘A’ or ‘V’) should be checked by
   * the user to ensure the GPS system and the VLP-16 are receiving valid
   * Coordinated Universal Time (UTC) updates from the user’s GPS receiver. */
  private final String nmea;

  public VelodynePosEvent(int gps_usec, String nmea) {
    this.gps_usec = gps_usec;
    this.nmea = nmea;
  }

  public int gps_usec() {
    return gps_usec;
  }

  public String nmea() {
    return nmea;
  }

  private static final double TO_DEGREE = 1E-2;
  private static final Unit UNIT_DEGREE = Unit.of("deg");

  /** E W
   * 
   * @return */
  public Scalar gpsX() {
    double value = Double.parseDouble(nmea.substring(28, 28 + 10)) * TO_DEGREE;
    Scalar scalar = Quantity.of(value, UNIT_DEGREE);
    char id = nmea.charAt(39);
    return id == 'E' ? scalar : scalar.negate();
  }

  /** N S
   * 
   * @return */
  public Scalar gpsY() {
    double value = Double.parseDouble(nmea.substring(16, 16 + 9)) * TO_DEGREE;
    Scalar scalar = Quantity.of(value, UNIT_DEGREE);
    char id = nmea.charAt(25 + 1);
    return id == 'N' ? scalar : scalar.negate();
  }
}
