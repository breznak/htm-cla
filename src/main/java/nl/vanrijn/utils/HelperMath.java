/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vanrijn.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * HelperMath useful math functions
 *
 * @author marek
 */
public abstract class HelperMath {

    /**
     * this object is greater than the one passed as argument
     */
    public static final int GREATER = 1;
    public static final int EQUAL = 0;
    public static final int LESSER = -1;
    public static final int BEFORE = 1;
    public static final int NOW = 0;

    /**
     * make integer sequence 1..maxValue
     *
     * @param maxValue
     * @return
     */
    public static List<Integer> seq(int maxValue) {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < maxValue; i++) {
            l.add(i);
        }
        return l;
    }

    /**
     * compute sum of numeric elements
     *
     * @param numberList - an array, list, collection,..of numbers
     * @return
     */
    public static double sum(Iterable numberList) {
        double d = 0;
        for (Iterator it = numberList.iterator(); it.hasNext();) {
            d += (int) it.next();
        }
        return d;
    }

    /**
     * ensure returned value lies in <lowerBound, upperBound> range, if not,
     * return closest boundary
     *
     * @param value
     * @param lowerBound
     * @param upperBound
     * @return
     */
    public static int inRange(int value, int lowerBound, int upperBound) {
        return (int) inRange(value, (double) lowerBound, upperBound);
    }

    /**
     * ensure returned value lies in <lowerBound, upperBound> range, if not,
     * return closest boundary
     *
     * @param value
     * @param lowerBound
     * @param upperBound
     * @return
     */
    public static double inRange(double value, double lowerBound, double upperBound) {
        return Math.max(lowerBound, Math.min(upperBound, value));
    }
}
