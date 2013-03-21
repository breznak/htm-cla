/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import nl.vanrijn.pooler.TemporalPooler;

public class LateralSynapse extends SynapseAbstract {

    private final Cell fromCell;
    private final Cell onCell;
    private final int segmentIndex;

    public LateralSynapse(Cell on, int s, Cell from, double initialPerm) {
        super(initialPerm);
        this.onCell = on;
        this.segmentIndex = s;
        this.fromCell = from;
    }

    public Cell getFromCell() {
        return fromCell;
    }

    public boolean isConnected() {
        // logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+
        // "input="+sourceInput);
        return super.isConnected(TemporalPooler.CONNECTED_PERMANANCE);
    }

    @Override
    public String toString() {
        return "LateralSynapse from " + this.fromCell + ", on " + this.onCell + "," + this.segmentIndex + ",perm " + getPermanance();
    }
}
