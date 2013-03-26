/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.utils;

import htm.model.input.BinaryVectorInput;
import htm.model.input.Input;
import java.util.BitSet;
import org.junit.Test;

/**
 *
 * @author marek
 */
public class InputTest {

    @Test
    public void checkBinaryInput() {
        Input<BitSet> binaryVectorInput = new BinaryVectorInput(1, 4);
        BitSet bb = new BitSet();
        bb.set(1, true);
        bb.set(2, true);
        bb.set(3, false);
        bb.set(4);
        binaryVectorInput.setRawInput(bb);
        System.out.println(binaryVectorInput);
    }
}
