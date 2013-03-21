/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import nl.vanrijn.pooler.TemporalPooler;

public class LateralSynapse extends SynapseAbstract {

    private final Cell fromCell;

    public LateralSynapse(Cell from, double initialPerm) {
        super(initialPerm);
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
        return "LateralSynapse from " + this.fromCell + ",perm " + getPermanance();
    }
}
