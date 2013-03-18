/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model.helper;

import java.util.List;

import nl.vanrijn.model.LateralSynapse;
import nl.vanrijn.model.Synapse;


/**
 * segmentUpdate Data structure holding three pieces of information required to update a given segment: a) segment index
 * (-1 if it's a new segment), b) a list of existing active synapses, and c) a flag indicating whether this segment
 * should be marked as a sequence segment (defaults to false).
 * 
 * @author vanrijn
 */
public class SegmentUpdate {

	public SegmentUpdate(int columnIndex, int cellIndex, int segmentUpdateIndex, List<LateralSynapse> activeSynapses) {
		super();

		this.cellIndex = cellIndex;
		this.columnIndex = columnIndex;
		this.segmentUpdateIndex = segmentUpdateIndex;

		this.activeSynapses = activeSynapses;
	}

	private boolean	sequenceSegment;

	private int		segmentUpdateIndex;

	@Override
	public String toString() {
		
		return "segmentUpdate"+this.getColumnIndex()+","+this.getCellIndex()+","+this.getSegmentUpdateIndex()+","+this.isSequenceSegment();
	}

	private int		cellIndex;

	private int		columnIndex;

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	private List<LateralSynapse>	activeSynapses;

	public static boolean			POSITIVE_REINFORCEMENT		= true;

	public static boolean			NO_POSITIVE_REINFORCEMENT	= false;

	public int getCellIndex() {
		return cellIndex;
	}

	public void setCellIndex(int cellIndex) {
		this.cellIndex = cellIndex;
	}

	public int getSegmentUpdateIndex() {
		return segmentUpdateIndex;
	}

	public void setSegmentUpdateIndex(int segmentUpdateIndex) {
		this.segmentUpdateIndex = segmentUpdateIndex;
	}

	public List<LateralSynapse> getActiveSynapses() {
		return activeSynapses;
	}

	public void setActiveSynapses(List<LateralSynapse> activeSynapses) {
		this.activeSynapses = activeSynapses;
	}

	public boolean isSequenceSegment() {
		return sequenceSegment;
	}

	public void setSequenceSegment(boolean sequenceSegment) {
		this.sequenceSegment = sequenceSegment;

	}

}
