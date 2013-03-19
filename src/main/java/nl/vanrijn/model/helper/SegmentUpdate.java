/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model.helper;

import java.util.List;
import nl.vanrijn.model.LateralSynapse;

/**
 * segmentUpdate Data structure holding three pieces of information required to update a given segment: a) segment index
 * (-1 if it's a new segment) b) a list of existing active synapses, and c) a flag indicating whether this segment
 * should be marked as a sequence segment (defaults to false).
 *
 * @author vanrijn
 */
public class SegmentUpdate {

    /**
     * flag for new segment. Used in Index
     */
    private final static int NEW_SEGMENT_IDX = -1;
    private boolean sequenceSegment = false;
    private int segmentUpdateIndex = NEW_SEGMENT_IDX;
    private final int cellIndex;
    private final int columnIndex;
    private final List<LateralSynapse> activeSynapses;
    public static boolean POSITIVE_REINFORCEMENT = true;
    public static boolean NO_POSITIVE_REINFORCEMENT = false;

    public SegmentUpdate(final int columnIndex, final int cellIndex, int segmentUpdateIndex, List<LateralSynapse> activeSynapses) {
        this.cellIndex = cellIndex;
        this.columnIndex = columnIndex;
        this.segmentUpdateIndex = segmentUpdateIndex;
        this.activeSynapses = activeSynapses;
    }

    @Override
    public String toString() {
        return "segmentUpdate"+this.columnIndex+","+this.cellIndex+","+this.segmentUpdateIndex+","+this.sequenceSegment;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public int getSegmentUpdateIndex() {
        return segmentUpdateIndex;
    }

    public List<LateralSynapse> getActiveSynapses() {
        return activeSynapses;
    }

    public boolean isSequenceSegment() {
        return sequenceSegment;
    }

    public void setSequenceSegment(boolean sequenceSegment) {
        this.sequenceSegment = sequenceSegment;
    }
}
