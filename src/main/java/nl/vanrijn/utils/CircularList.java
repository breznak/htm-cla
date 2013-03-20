/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vanrijn.utils;

import java.util.ArrayList;

/**
 * ArrayList with circular buffer functionality has a capacity, if full, remove
 * the oldest element
 *
 * @author marek
 */
public class CircularList<T> extends ArrayList<T> {

    private static final long serialVersionUID = 1L;
    private ArrayList<T> buffer;
    private int maxCapacity;

    /**
     * ArrayList with circular buffer functionality has a capacity, if full,
     * remove the oldest element
     *
     * @param capacity
     */
    public CircularList(int capacity) {
        this.buffer = new ArrayList<>(capacity);
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
        if (buffer.size() > maxCapacity) {
            buffer.remove(maxCapacity);
        }
    }
}
