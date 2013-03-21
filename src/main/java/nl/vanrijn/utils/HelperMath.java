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
    private static final int LESSER = -1;

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
}
