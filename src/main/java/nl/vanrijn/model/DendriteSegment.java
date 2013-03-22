/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import java.util.List;

public class DendriteSegment<S extends LateralSynapse> {

    private List<S> synapses;
    private boolean sequenceSegment;
    private int segmentIndex;
    private int ammountActiveCells;
    private Cell belongsToCell;

    public DendriteSegment(Cell belongsToCell, int s, List<S> synapses) {
        this.belongsToCell = belongsToCell;
        this.segmentIndex = s;
        this.synapses = synapses;
    }

    public Cell getBelongingCell() {
        return this.belongsToCell;
    }

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public List<S> getSynapses() {
        return synapses;
    }

    public List<S> getConnectedSynapses(double connectedPermanance) {
        List<S> connectedSynapses = new ArrayList<>();
        for (S syn : synapses) {
            if (syn.isConnected(connectedPermanance)) {
                connectedSynapses.add(syn);
            }
        }
        return connectedSynapses;
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName() + " segment on " + getBelongingCell().getColumn() + "," + getBelongingCell().getName() + "," + this.segmentIndex + ",isSeq " + sequenceSegment + ",amm syn " + this.getSynapses().size();
    }

    public boolean isSequenceSegment() {
        return this.sequenceSegment;
    }

    public void setSequenceSegment(boolean sequenceSegment) {
        this.sequenceSegment = sequenceSegment;
    }

    /**
     * This is used for sorting a List of segments
     *
     * @param ammountActiveCells
     */
    public void setAmmountActiveCells(int ammountActiveCells) {
        this.ammountActiveCells = ammountActiveCells;
    }

    public int getAmmountActiveCells() {
        return this.ammountActiveCells;
    }
}
