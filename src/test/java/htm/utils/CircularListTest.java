/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.utils;

import java.util.BitSet;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author marek
 */
public class CircularListTest {

    CircularList cl;
    BitSet bs;

    public CircularListTest() {
        cl = new CircularList(1, 4);
        bs = new BitSet(5);
        bs.set(3);
    }

    @Test
    public void checkCircularListSizeEmpty() {
        assertEquals(1, new CircularList(1, 1).size());
    }

    @Test
    public void checkCircularListSize() {
        cl.add(bs);
        assertEquals(4, cl.size());
    }

    @Test
    public void checkEquals() {
        cl = new CircularList(1, 1);
        BitSet bs_one = new BitSet(1);
        bs_one.set(0);
        cl.add(bs_one);
        assertEquals(true, cl.equals(CircularList.BIT_1));
    }
}
