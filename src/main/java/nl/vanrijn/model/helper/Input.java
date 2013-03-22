/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vanrijn.model.helper;

/**
 *
 * @author marek
 */
public abstract class Input {

    private static final int INPUT_MODE_ASYNC = 1;
    private static final int INPUT_MODE_SYNC = 2;
    private byte[] vector;
    private int mode;
    private static int HISTORY_SIZE;

    public Input(int inputVectorSize, int mode, int historySteps) {
        vector = new byte[inputVectorSize]; //TODO use generic type
        //TODO 2: use bits only for big 0001000 arrays
        Input.HISTORY_SIZE = historySteps;
        this.mode = mode;
    }

    public byte[] getInput() {
        return vector;
    }

    public void setRawInput(Object rawInput) {
        vector = transform(rawInput);
    }

    abstract byte[] transform(Object rawInput);

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " = " + vector;
    }
}
