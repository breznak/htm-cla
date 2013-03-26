/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model.input;

/**
 *
 * @author marek
 */
public class StringInput extends Input<String> {

    public StringInput(int mode, int maxStrLength) {
        super(mode, maxStrLength * 8);  //strLen==#chars==#bytes==*8 to bits
    }
}
