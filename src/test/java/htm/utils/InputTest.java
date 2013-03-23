/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.utils;

import htm.model.input.BinaryVectorInput;
import htm.model.input.Input;
import org.junit.Test;

/**
 *
 * @author marek
 */
public class InputTest {

    @Test
    public void checkBinaryInput() {
        BinaryVectorInput bi = new BinaryVectorInput(Input.INPUT_MODE_SYNC);
        Boolean[] bb = new Boolean[]{true, false, true, true};
        bi.setRawInput(bb);
        System.out.println(bi);
    }
}
