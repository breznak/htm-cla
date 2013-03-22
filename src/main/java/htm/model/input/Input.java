/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model.input;

import htm.model.LayerAbstract;

/**
 *
 * @author marek
 */
public abstract class Input<T, TYPE> extends LayerAbstract<Object, Object, TYPE> {

    public static final int INPUT_MODE_ASYNC = 1;
    public static final int INPUT_MODE_SYNC = 2;
    private int mode;
    private static int inputCounter = 0;

    public Input(int mode) {
        super(null, null, Input.inputCounter++, 1);
        this.mode = mode;
    }

    public void setRawInput(T rawInput) {
        this.output.add(0, transform(rawInput));
    }

    abstract TYPE[] transform(T rawInput);

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": id= " + this.id + " {" + input + "}";
    }
}
