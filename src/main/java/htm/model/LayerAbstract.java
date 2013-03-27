/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;
import htm.utils.HelperMath;
import java.util.ArrayList;

/**
 *
 * @author marek
 */
public abstract class LayerAbstract<PART> {

    public final ArrayList<PART> parts;
    protected final CircularList input;
    private final CircularList output;
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
        this.output = new CircularList(timeStepsMax, dimX * dimY);
        this.input = input;
        this.parts = new ArrayList<>();
        for (int i = 0; i < dimX * dimY; i++) {
            parts.add(null); //pre-allocate, so add(0), add(2) can succeed
        }
        this.id = id;
        this.HISTORY_STEPS = timeStepsMax;
        this.dimX = Math.max(dimX, dimY); //some parts need bigger dimension be as dimX, eg getCoordinates()
        this.dimY = Math.min(dimX, dimY);
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
        return parts.get(x * dimY + y);
    }

    public void addPart(PART p, int x, int y) {
        System.err.println("x " + x + " y " + y + " dimX " + dimX + " dimY " + dimY);
        parts.add(x * dimY + y, p);
    }

    public boolean input(int index) {
        return this.input(index, HelperMath.NOW);
    }

    /**
     * number of parts
     *
     * @return
     */
    public int size() {
        return dimX * dimY;
    }

    public boolean input(int index, int time) {
        return input.get(time).get(index);
    }
}
