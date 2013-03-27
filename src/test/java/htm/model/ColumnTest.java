/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.BinaryVectorInput;
import htm.model.input.Input;
import java.util.BitSet;
import java.util.Random;
import static org.junit.Assert.*;
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
        in = new BinaryVectorInput(1, 1000);
        sp = new SpatialPooler(3, 5, in);
        col = new Column<>(sp, 0, 1, 0.02);
        System.out.println(col);

        col = sp.getColumn(10);
        System.out.println("" + col);
        assertEquals(1000, col.parent.input.size());
    }

    @Test
    public void checkInitSynapseIdx() {
        col = new Column<>(sp, 0, 1, 0.02);
        int[] idx = col.initSynapsesIdx();
        System.out.print("Synapse idx=");
        for (int i = 0; i < idx.length; i++) {
            System.out.print(idx[i] + " ");
        }
        System.out.println("");
    }

    @Test
    public void checkInitSynapsePerm() {
        in = new BinaryVectorInput(1, 10);
        sp = new SpatialPooler(2, 2, in);
        assertEquals(10, sp.input.size());
        System.err.println("##" + sp.input.size());

        col = new Column<>(sp, 0, 1, 0.02);
        int center = new Random().nextInt(sp.size());
        int[] idx = col.initSynapsesIdx();
        float[] perm = col.initSynapsePerm(center, idx);
        System.out.println("Synapse: { idx : perm } (center=" + center + ", connectedPerm=" + Column.CONNECTED_SYNAPSE_PERM);
        for (int i = 0; i < idx.length; i++) {
            System.out.println(" {" + idx[i] + " : " + perm[i] + "}  ");
        }
        System.out.println("");
    }
}
