/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.BinaryVectorInput;
import htm.model.input.Input;
import htm.model.input.StringInput;
import java.util.BitSet;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author marek
 */
public class InputTest {

    Input in;

    @Test
    public void checkBinaryInput() {
        in = new BinaryVectorInput(1, 5);
        BitSet bb = new BitSet();
        bb.set(1, true);
        bb.set(2, true);
        bb.set(3, false);
        bb.set(4);
        in.setRawInput(bb);
        System.out.println(in);
        assertEquals("BinaryVectorInput: id= 1 :: 0 1 1 0 1 ", in.toString());
    }

    @Test
    public void checkStringInput() {
        in = new StringInput(1, 6);
        in.setRawInput("Hello");
        System.out.println(in);
        assertEquals("StringInput: id= 1 :: 0 0 0 1 0 0 1 0 1 0 1 0 0 1 1 0 0 0 1 1 0 1 1 0 0 0 1 1 0 1 1 0 1 1 1 1 0 1 1 0 0 0 0 0 0 0 0 0 ", in.toString());
    }
}
