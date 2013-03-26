/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model.input;

import java.util.BitSet;

/**
 *
 * @author marek
 */
public class BinaryVectorInput extends Input<BitSet> {

    /**
     *
     * @param mode
     * @param numBits
     */
    public BinaryVectorInput(int mode, int numBits) {
        super(mode, numBits);
    }
}
