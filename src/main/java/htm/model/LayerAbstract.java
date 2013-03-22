/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author marek
 */
public abstract class LayerAbstract<PART, PARENT> {

    private final PARENT parent;
    private final PART parts;
    public CopyOnWriteArrayList<Boolean> input;
    public final CircularList output;
    /**
     * unique ID of cell, use getName()
     */
    private final int uniqueID;
    /**
     * will keep a buffer of its last HISTORY_STEPS output values
     */
    public final int HISTORY_STEPS;

    public LayerAbstract(PART parts, PARENT parent, int id, int timeStepsMax) {
        this.output = new CircularList(timeStepsMax);
        this.parent = parent;
        this.parts = parts;
        this.uniqueID = id;
        this.HISTORY_STEPS = timeStepsMax;
    }

    /**
     * unique name (int) of the object
     *
     * @return
     */
    public int id() {
        return uniqueID;
    }

    /**
     * higher-level structure, this is a part of it
     *
     * @return
     */
    public PARENT parent() {
        return parent;
    }

    /**
     * lower-level structure parts, this is their parent
     *
     * @return
     */
    public PART parts() {
        return parts;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(this.getClass())) {
            return false;
        }
        if (((LayerAbstract) obj).id() == this.id()) {
            return true;
        }
        return false;
    }
}
