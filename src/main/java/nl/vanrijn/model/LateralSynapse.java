/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

public class LateralSynapse extends SynapseAbstract {

    private final Cell fromCell;

    public LateralSynapse(Cell from, double initialPerm) {
        super(initialPerm);
        this.fromCell = from;
    }

    public Cell getFromCell() {
        return fromCell;
    }

    @Override
    public String toString() {
        return "LateralSynapse from " + this.fromCell + ",perm " + getPermanance();
    }
}
