/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.utils;

import java.util.BitSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ArrayList with circular buffer functionality has a capacity, if full, remove
 * the oldest element
 *
 * @author marek
 */
public class CircularList extends CopyOnWriteArrayList<BitSet> {

    private static final long serialVersionUID = 1L;
    public final int maxCapacity;
    public final int width;
    public static final BitSet BIT_1 = new BitSet(1); //FIXME static, but bit set only after constructor
    public static final BitSet BIT_0 = new BitSet(1);

    /**
     * ArrayList with circular buffer functionality has a capacity, if full,
     * remove the oldest element
     *
     * @param capacity
     */
    public CircularList(int capacity, int width) {
        super();
        this.maxCapacity = capacity;
        this.width = width;
        BitSet defaultIN = new BitSet(width);
        super.add(defaultIN); //default zero input, to avoid init problems with other parts

        BIT_0.set(0, false);
        BIT_1.set(0, true);
    }

    /**
     * if full, remove oldest element
     *
     * @param index
     * @param element
     */
    @Override
    public void add(int index, BitSet element) {
        if (index >= width) {
            throw new IndexOutOfBoundsException("CircularList - adding out of bounds! width=" + width);
        }
        super.add(index, element);
        if (super.size() > maxCapacity) {
            remove(maxCapacity);
        }
    }

    @Override
    public boolean add(BitSet e) {
        this.add(0, e);
        return true;
    }

    /**
     * size - length of stored BitSet, !not capacity of this List, which is
     * equal to maxCapacity
     *
     * @return
     */
    @Override
    public int size() {
        return width;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < size(); i++) {
            if (get(0).get(i)) {
                s += 1 + " ";
            } else {
                s += 0 + " ";
            }
        }
        return s;
    }

    @Override
    public int hashCode() {
        return get(0).hashCode();
    }

    /**
     * top stored elements are equal
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        return get(0).equals(o);
    }
}
