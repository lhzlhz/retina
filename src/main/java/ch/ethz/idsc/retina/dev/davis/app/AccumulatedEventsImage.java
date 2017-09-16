// code by jpg
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.TimedImageListener;

/** synthesizes grayscale images based on incoming events during intervals of fixed duration
 * positive events appear in white color
 * negative events appear in black color */
public class AccumulatedEventsImage implements DavisDvsListener {
  private static final byte CLEAR_BYTE = (byte) 128;
  // ---
  private final int width;
  private final int height;
  private final List<TimedImageListener> listeners = new LinkedList<>();
  private final BufferedImage bufferedImage;
  private final byte[] bytes;
  private final int interval;
  private Integer last = null;

  /** @param interval [us] */
  public AccumulatedEventsImage(DavisDevice davisDevice, int interval) {
    width = davisDevice.getWidth();
    height = davisDevice.getHeight();
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    GlobalAssert.that(bytes.length == width * height);
    this.interval = interval;
    GlobalAssert.that(0 < interval);
    // ---
    clearImage();
  }

  public void addListener(TimedImageListener timedImageListener) {
    listeners.add(timedImageListener);
  }

  @Override
  public void davisDvs(DavisDvsEvent dvsDavisEvent) {
    if (Objects.isNull(last))
      last = dvsDavisEvent.time;
    final int delta = dvsDavisEvent.time - last;
    if (delta < 0) {
      System.err.println("dvs image clear due to reverse timing");
      clearImage();
      last = dvsDavisEvent.time;
    } else //
    if (interval < delta) {
      listeners.forEach(listener -> listener.image(last, bufferedImage));
      clearImage();
      last += interval;
    }
    int polarity = dvsDavisEvent.i == 0 ? 0 : 255;
    int index = dvsDavisEvent.x + (dvsDavisEvent.y) * width;
    bytes[index] = (byte) polarity;
  }

  private void clearImage() {
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = CLEAR_BYTE);
  }
}
