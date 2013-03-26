/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;

/**
 *
 * @author marek
 */
public abstract class LayerAbstract<PARTS> {

    public final PARTS parts;
    public final CircularList input;
    public final CircularList output;
    /**
     * unique ID of cell, use getName()
     */
    public final int id;
    /**
     * will keep a buffer of its last HISTORY_STEPS output values
     */
    public final int HISTORY_STEPS;
    public final int dimX;
    public final int dimY;

    public LayerAbstract(PARTS parts, int dimX, int dimY, int id, int timeStepsMax, CircularList input) {
        this.output = new CircularList(timeStepsMax);
        this.input = input;
        this.parts = parts;
        this.id = id;
        this.HISTORY_STEPS = timeStepsMax;
        this.dimX = dimX;
        this.dimY = dimY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(this.getClass())) {
            return false;
        }
        if (((LayerAbstract) obj).id == this.id) {
            return true;
        }
        return false;
    }

    /**
     * number of parts
     *
     * @return
     */
    public int size() {
        return dimX * dimY;
    }
}
