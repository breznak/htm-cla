/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import java.util.List;

public class Segment {

	public static final boolean		GETS_NO_NEW_SYNAPSE	= false;

	public static final boolean		GETS_NEW_SYNAPSE	= true;

	private List<LateralSynapse>	synapses;

	private boolean					sequenceSegment;
	
	private int cellIndex;
	private int						segmentIndex;

	private int columnIndex;

	private int ammountActiveCells;

	public Segment(int c, int i, int s, List<LateralSynapse> synapses) {
		this.columnIndex=c;
		this.cellIndex=i;
		this.segmentIndex=s;
		this.synapses=synapses;
	}

	public int getCellIndex() {
		return cellIndex;
	}

	public int getSegmentIndex() {
		return segmentIndex;
	}	

	public int getColumnIndex() {
		return columnIndex;
	}

	public List<LateralSynapse> getSynapses() {
		return synapses;
	}
	public List<LateralSynapse> getConnectedSynapses(){
		List<LateralSynapse> connectedSynapses=new ArrayList<LateralSynapse>();
		for (LateralSynapse synapse : synapses){
			if( synapse.isConnected()){
				connectedSynapses.add(synapse);
			}
		}
		return connectedSynapses;
	}

	@Override
	public String toString() {
		
		return "segment on "+this.getColumnIndex()+","+this.getCellIndex()+","+this.getSegmentIndex()+",isSeq "+isSsequenceSegment()+",amm syn "+this.getSynapses().size();
	}

	public void setSynapses(List<LateralSynapse> synapses) {
		this.synapses = synapses;
	}

	public boolean isSsequenceSegment() {
		return this.sequenceSegment;
	}

	public void setSequenceSegment(boolean sequenceSegment) {
		this.sequenceSegment = sequenceSegment;
	}
	
/**
 * This is used for sorting a List of segments
 * @param ammountActiveCells
 */
	
	
	public void setAmmountActiveCells(int ammountActiveCells) {
		this.ammountActiveCells=ammountActiveCells;
		
	}

	public int getAmmountActiveCells() {
		return this.ammountActiveCells;
	}

}
