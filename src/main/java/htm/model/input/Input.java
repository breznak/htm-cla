/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model.input;

import htm.model.LayerAbstract;
import java.awt.image.BufferedImage;
import java.util.BitSet;

/**
 *
 * @author marek
 */
public class Input extends LayerAbstract {

    public static final int INPUT_MODE_ASYNC = 1;
    public static final int INPUT_MODE_SYNC = 2;
    private int mode;
    private static int inputCounter = 0;

    public Input(int mode) {
        super(null, null, Input.inputCounter++, 1);
        this.mode = mode;
    }

    public void setRawInput(BitSet rawInput) { //TODO general case doesnt work
        this.output.add(0, transform(rawInput));
    }

    public static BitSet transform(byte[] input) {
        return BitSet.valueOf(input);
    }

    public static BitSet transform(long[] input) {
        return BitSet.valueOf(input);
    }

    public static BitSet transform(String input) {
        return BitSet.valueOf(input.getBytes());
    }

    public static BitSet transform(BufferedImage input) {
        return new BitSet();//.valueOf(input.getRGB(0, 0, input.getWidth(null), input.getHeight(null), null, 0, input.getWidth(null)));
    }

    /**
     * approximates continuous variable in bits. Say value can be in interval
     * 0.2meters...1meter, and i want measures in mm. Range is 0.8(m) and
     * granuity is 1000 (#measures in One unit of Range)
     *
     * @param value
     * @param range
     * @param granuity
     * @return
     */
    public static BitSet transform(double value, int range, int granuity) {
        BitSet bs = new BitSet(granuity * range);
        bs.set(range);
        return bs; //TODO this is wrong
    }

    public static BitSet transform(BitSet binaryVector) {
        return binaryVector;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": id= " + this.id + output;
    }
}
