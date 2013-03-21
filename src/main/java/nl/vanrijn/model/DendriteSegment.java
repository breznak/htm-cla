/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DendriteSegment {

    private List<LateralSynapse> synapses;
    private boolean sequenceSegment;
    private int segmentIndex;
    private int ammountActiveCells;
    private Cell belongsToCell;

    public DendriteSegment(Cell belongsToCell, int s, List<LateralSynapse> synapses) {
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

    public List<LateralSynapse> getSynapses() {
        return Collections.unmodifiableList(synapses);
    }

    public List<LateralSynapse> getConnectedSynapses() {
        List<LateralSynapse> connectedSynapses = new ArrayList<>();
        for (LateralSynapse synapse : synapses) {
            if (synapse.isConnected()) {
                connectedSynapses.add(synapse);
            }
        }
        return connectedSynapses;
    }

    @Override
    public String toString() {
        return "segment on " + getBelongingCell().getColumnIndex() + "," + getBelongingCell().getCellIndex() + "," + this.segmentIndex + ",isSeq " + sequenceSegment + ",amm syn " + this.getSynapses().size();
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
