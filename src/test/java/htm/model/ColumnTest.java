/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.BinaryVectorInput;
import htm.model.input.Input;
import java.util.BitSet;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author marek
 */
public class ColumnTest {

    Column<SpatialPooler> col;
    Input in;
    BitSet pattern = new BitSet(4);
    SpatialPooler sp;

    @Before
    public void init() {
        in = new BinaryVectorInput(1, 4);
        sp = new SpatialPooler(3, 5, in);
        pattern.set(0);
        pattern.set(3);
    }

    @Test
    public void checkColumn() {
        col = new Column<>(sp, 0, 1, 0.02);
        System.out.println(col);

        col = sp.getColumn(10);
        System.out.println("" + col);
    }
}
