/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;
import htm.utils.HelperMath;

/**
 *
 * @author marek
 */
public abstract class LayerAbstract<PART, PARENT, IN, OUT> {

    private final PARENT parent;
    private final PART parts;
    private final IN input = null;
    private final CircularList<OUT> output;
    /**
     * unique ID of cell, use getName()
     */
    private final int uniqueID;
    /**
     * will keep a buffer of its last HISTORY_STEPS output values
     */
    public final int HISTORY_STEPS;

    public LayerAbstract(PART parts, PARENT parent, int id, int timeStepsMax) {
        this.output = new CircularList<>(timeStepsMax);
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

    /**
     * query cell's output state: {active,predict,inactive} at given time
     *
     * when requested time exceeds cell's history buffer, oldest is returned.
     *
     * @param time - when was this state. 0==Cell.NOW==actual, 1=BEFORE, ..upto
     * TIME_STEPS
     *
     * @return 0/1/2 only!
     */
    public OUT output(int time) {
        if (time >= HISTORY_STEPS) {
            System.err.println("! i dont remember so much, asshole");
            time = HISTORY_STEPS - 1;
        }
        return this.output.get(time);
    }

    /**
     * set cell's state. can be: ACTIVE/INACTIVE/PREDICT
     *
     * @param state
     */
    public void setOutput(OUT state) {
        this.output.add(HelperMath.NOW, state);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(this.getClass())) {
            return false;
        }
        if (((LayerAbstract<PART, PARENT, IN, OUT>) obj).id() == this.id()) {
            return true;
        }
        return false;
    }
}
