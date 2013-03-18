package nl.vanrijn.pooler;

import static org.junit.Assert.assertEquals;
import nl.vanrijn.model.Cell;
import nl.vanrijn.model.LateralSynapse;
import nl.vanrijn.model.Segment;

import org.junit.Before;
import org.junit.Test;

public class TemporalPoolerTest {
	TemporalPooler tempo;

	@Before
	public void setup() {
		tempo = new TemporalPooler(12, 12);
		tempo.init();

	}

	@Test
	public void checkactiveSegment(){

		Cell cell = tempo.getCells()[0][0][Cell.NOW];
		
		Segment segment = cell.getSegments().get(0);
		
		for (LateralSynapse synaps : segment.getSynapses()) {
			Cell cell1 =tempo.getCells()[synaps.getFromColumnIndex()][synaps.getFromCellIndex()][Cell.NOW];
			cell1.setActiveState(true);
			cell1.setLearnState(true);
			System.out.println(synaps);
		}

		
		Segment segment2=cell.getSegments().get(1);
		segment2.setSequenceSegment(true);
		for (LateralSynapse synaps : segment2.getSynapses()) {
			Cell cell1 =tempo.getCells()[synaps.getFromColumnIndex()][synaps.getFromCellIndex()][Cell.NOW];
			cell1.setActiveState(true);
			cell1.setLearnState(true);
		}
		Segment segmentToTest=tempo.getActiveSegment(0, 0, Cell.NOW, Cell.ACTIVE_STATE);
		System.out.println(segment.getConnectedSynapses().size());
		System.out.println(tempo.segmentActive(segment, Cell.NOW, Cell.ACTIVE_STATE));
		assertEquals(segmentToTest.isSsequenceSegment(), true);

	}
}
