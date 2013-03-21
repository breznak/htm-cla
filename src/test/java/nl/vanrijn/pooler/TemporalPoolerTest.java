package nl.vanrijn.pooler;

import nl.vanrijn.model.Cell;
import nl.vanrijn.model.DendriteSegment;
import nl.vanrijn.model.LateralSynapse;
import nl.vanrijn.utils.HelperMath;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class TemporalPoolerTest {

    private TemporalPooler tempo;

    @Before
    public void setup() {
        tempo = new TemporalPooler(12, 12);
    }

    @Test
    public void checkactiveSegment() {
        Cell cell = tempo.getCells()[0][0];
        DendriteSegment segment = cell.getSegments().get(0);

        for (LateralSynapse synaps : segment.getSynapses()) {
            Cell cell1 = synaps.getFromCell();
            cell1.setOutput(Cell.ACTIVE);
            cell1.setLearnState(true);
            System.out.println(synaps);
        }

        DendriteSegment segment2 = cell.getSegments().get(1);
        segment2.setSequenceSegment(true);
        for (LateralSynapse synaps : segment2.getSynapses()) {
            Cell cell1 = synaps.getFromCell();
            cell1.setOutput(Cell.ACTIVE);
            cell1.setLearnState(true);
        }
        DendriteSegment segmentToTest = tempo.getActiveSegment(0, 0, HelperMath.NOW, Cell.ACTIVE);
        System.out.println(this.getClass().getName() + ">> segment connected synapses = " + segment.getConnectedSynapses().size());
        System.out.println(this.getClass().getName() + ">> " + tempo.segmentActive(segment, HelperMath.NOW, Cell.ACTIVE));
        System.out.println(this.getClass().getName() + ">> is Sequence segment: " + segmentToTest.isSequenceSegment());
        assertEquals(segmentToTest.isSequenceSegment(), false);
    }
}
