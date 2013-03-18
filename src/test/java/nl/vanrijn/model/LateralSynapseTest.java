package nl.vanrijn.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class LateralSynapseTest {

	LateralSynapse	synapse	= null;

	@Before
	public void setup() {
		synapse = new LateralSynapse(1, 3, 2, 5, 1, 0.8);

	}

	@Test
	public void setSegmentIndex() {

		assertEquals(50, 5 * 10);

		// this.segmentIndex = segmentIndex;
	}
}
