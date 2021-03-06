// code by jph
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class SlamConfigTest extends TestCase {
  public void testSimple() {
    SlamCoreConfig slamConfig = new SlamCoreConfig();
    Tensor high = slamConfig.cornerHigh();
    Clip clip = Clip.function(Quantity.of(60, SI.METER), Quantity.of(80, SI.METER));
    assertTrue(clip.isInside(high.Get(0)));
    assertTrue(clip.isInside(high.Get(1)));
    // assertEquals(high, Tensors.fromString("{70[m], 70[m]}"));
  }

  public void testSlamAlgo() {
    SlamAlgoConfig slamAlgoConfig = new SlamCoreConfig().slamAlgoConfig;
    assertNotNull(slamAlgoConfig);
  }
}
