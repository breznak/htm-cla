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
    private final int maxCapacity;
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
        if (this.size() > maxCapacity) {
            this.remove(maxCapacity);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " -- " + this.get(0).toString();
    }

    @Override
    public int hashCode() {
        return get(0).hashCode();
    }
}
