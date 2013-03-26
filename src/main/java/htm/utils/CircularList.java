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
    public static final BitSet BIT_1 = new BitSet(1);
    public static final BitSet BIT_0 = new BitSet(1);

    /**
     * ArrayList with circular buffer functionality has a capacity, if full,
     * remove the oldest element
     *
     * @param capacity
     */
    public CircularList(int capacity) {
        super();
        this.maxCapacity = capacity;
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
        if (super.size() == 0) {
            return 0;
        }
        return get(0).size();
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
}
