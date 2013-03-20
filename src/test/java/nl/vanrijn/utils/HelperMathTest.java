/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vanrijn.utils;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author marek
 */
public class HelperMathTest {

    @Test
    public void checkSeq() {
        assertArrayEquals(HelperMath.seq(3).toArray(new Integer[3]), new Integer[]{0, 1, 2});
    }

    @Test
    public void checkSum() {
        List<Integer> l = new ArrayList<>();
        l.add(1);
        l.add(2);
        l.add(3);
        assertEquals(6, HelperMath.sum(l), 0);
    }
}
