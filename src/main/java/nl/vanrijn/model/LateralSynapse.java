/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import nl.vanrijn.pooler.TemporalPooler;

public class LateralSynapse extends SynapseAbstract {

    private final int fromColumnIndex;
    private final int fromCellIndex;
    private final int columnIndex;
    private final int cellIndex;
    private final int segmentIndex;

    public LateralSynapse(int c, int i, int s, int fromColumnIndex, int fromCellIndex, double initialPerm) {
        super(initialPerm);
        this.columnIndex = c;
        this.cellIndex = i;
        this.segmentIndex = s;
        this.fromColumnIndex = fromColumnIndex;
        this.fromCellIndex = fromCellIndex;
    }

    public int getFromColumnIndex() {
        return fromColumnIndex;
    }

    public int getFromCellIndex() {
        return fromCellIndex;
    }

    public boolean isConnected() {
        // logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+
        // "input="+sourceInput);
        return super.isConnected(TemporalPooler.CONNECTED_PERMANANCE);
    }

    @Override
    public String toString() {
        return "LateralSynapse from " + this.fromColumnIndex + "," + this.fromCellIndex + ", on " + this.columnIndex
                + "," + this.cellIndex + "," + this.segmentIndex + ",perm " + getPermanance();
    }
}
