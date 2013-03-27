/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model.input;

import htm.utils.CircularList;
import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.Random;

/**
 *
 * @author marek
 */
public abstract class Input<RAW> extends CircularList {
//TODO add more input classes
    //TODO visual, parellel, mem, storage

    public static final int INPUT_MODE_ASYNC = 1;
    public static final int INPUT_MODE_SYNC = 2;
    private int mode;
    private static int inputCounter = 0;
    public final int id;

    public Input(int mode, int outputSize) {
        super(1, outputSize);
        this.mode = mode;
        this.id = Input.inputCounter++;
    }

    public BitSet randomSample() {
        BitSet rand = new BitSet(this.width);
        Random r = new Random();
        for (int i = 0; i < width; i++) {
            rand.set(i, r.nextBoolean());
        }
        return rand;
    }

    public void setRawInput(RAW rawInput) { //TODO general case doesnt work
        add(0, transform(rawInput));
    }

    public abstract BitSet transform(RAW rawInput);

    public BitSet transform(byte[] input) {
        return BitSet.valueOf(input);
    }

    public BitSet transform(long[] input) {
        return BitSet.valueOf(input);
    }

    public BitSet transform(String input) {
        return BitSet.valueOf(input.getBytes());
    }

    public BitSet transform(BufferedImage input) {
        int[] img = input.getRGB(0, 0, input.getWidth(null), input.getHeight(null), null, 0, input.getWidth(null));
        long[] loong = new long[img.length];
        for (int i = 0; i < img.length; i++) {
            loong[i] = (long) img[i];
        }
        return BitSet.valueOf((loong));
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
    public BitSet transform(double value, int lowerBound, int upperBound, int granuity) {
        BitSet bs = new BitSet(granuity * (upperBound - lowerBound));
        int idx = (int) Math.ceil((value - 1.1 * lowerBound) / granuity);
        bs.set(idx);
        return bs; //FIXME this is wrong, what representation?"5"=101(2) or "5"={0,0,0,0,0,1}(in enum 0..5)
    }

    public BitSet transform(BitSet binaryVector) {
        return binaryVector;
    }
}
