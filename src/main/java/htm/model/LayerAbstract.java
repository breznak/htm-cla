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
public abstract class LayerAbstract<PARENT, PARTS> {

    public final PARENT parent;
    public final PARTS parts;
    public final CircularList input = new CircularList(1);
    public final CircularList output;
    /**
     * unique ID of cell, use getName()
     */
    public final int id;
    /**
     * will keep a buffer of its last HISTORY_STEPS output values
     */
    public final int HISTORY_STEPS;

    public LayerAbstract(PARTS parts, PARENT parent, int id, int timeStepsMax) {
        this.output = new CircularList(timeStepsMax);
        this.parent = parent;
        this.parts = parts;
        this.id = id;
        this.HISTORY_STEPS = timeStepsMax;
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
}
