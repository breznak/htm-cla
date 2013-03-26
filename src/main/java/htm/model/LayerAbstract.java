/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;
import java.util.ArrayList;

/**
 *
 * @author marek
 */
public abstract class LayerAbstract<PART> {

    public final ArrayList<PART> parts;
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

    public LayerAbstract(int dimX, int dimY, int id, int timeStepsMax, CircularList input) {
        this.output = new CircularList(timeStepsMax);
        this.input = input;
        this.parts = new ArrayList<>(dimX * dimY);
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

    public PART part(int x, int y) {
        return parts.get(x * dimX + y);
    }

    public void addPart(PART p, int x, int y) {
        parts.add(x * dimX + y, p);
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
