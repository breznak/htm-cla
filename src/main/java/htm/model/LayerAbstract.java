/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;
import java.util.BitSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author marek
 */
public abstract class LayerAbstract {

    private final LayerAbstract parent;
    private final LayerAbstract parts;
    public final CopyOnWriteArrayList<BitSet> input;
    public final CircularList output;
    /**
     * unique ID of cell, use getName()
     */
    public final int id;
    /**
     * will keep a buffer of its last HISTORY_STEPS output values
     */
    public final int HISTORY_STEPS;

    public LayerAbstract(LayerAbstract parts, LayerAbstract parent, int id, int timeStepsMax) {
        this.output = new CircularList(timeStepsMax);
        this.parent = parent;
        this.parts = parts;
        this.id = id;
        this.HISTORY_STEPS = timeStepsMax;
        input = new CopyOnWriteArrayList<>();
    }

    /**
     * higher-level structure, this is a part of it
     *
     * @return
     */
    public LayerAbstract parent() {
        return parent;
    }

    /**
     * lower-level structure parts, this is their parent
     *
     * @return
     */
    public LayerAbstract parts() {
        return parts;
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
