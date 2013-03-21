package nl.vanrijn.model;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class LateralSynapseTest {

    private LateralSynapse synapse = null;

    @Before
    public void setup() {
        Cell c = new Cell(new Column(1, 1, 2), 2, 5, new ArrayList<DendriteSegment>());
        this.synapse = new LateralSynapse(c, 0.8);
    }

    @Test
    public void setSegmentIndex() {
        //TODO
        assertEquals(50, 5 * 10);
        // this.segmentIndex = segmentIndex;
    }
}
