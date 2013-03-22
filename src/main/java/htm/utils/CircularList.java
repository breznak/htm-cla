/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.utils;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ArrayList with circular buffer functionality has a capacity, if full, remove
 * the oldest element
 *
 * @author marek
 */
public class CircularList<T> extends CopyOnWriteArrayList<T> {

    private static final long serialVersionUID = 1L;
    private final int maxCapacity;

    /**
     * ArrayList with circular buffer functionality has a capacity, if full,
     * remove the oldest element
     *
     * @param capacity
     */
    public CircularList(int capacity) {
        super();
        this.maxCapacity = capacity;
    }

    /**
     * if full, remove oldest element
     *
     * @param index
     * @param element
     */
    @Override
    public void add(int index, T element) {
        super.add(index, element);
        if (this.size() > maxCapacity) {
            this.remove(maxCapacity);
        }
    }
}
