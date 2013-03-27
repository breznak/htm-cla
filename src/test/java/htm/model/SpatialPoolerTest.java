/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.BinaryVectorInput;
import htm.model.input.Input;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author marek
 */
public class SpatialPoolerTest {

    SpatialPooler sp;
    Input in = new BinaryVectorInput(1, 1000);

    @Before
    public void init() {
        in.setRawInput(in.randomSample());
    }

    @Test
    public void checkSpatialPooler() {
        sp = new SpatialPooler(10, 2, 1, 1, in, 0.02);
        System.out.println("IN=" + in + "\n" + sp);

        sp = new SpatialPooler(2, 10, 1, 1, in, 0.02);
        System.out.println("IN=" + in + "\n" + sp);
    }

    @Test
    public void checkNeighbors() {
        sp = new SpatialPooler(10, 2, 1, 1, in, 0.02);
        List<Integer> overlap = new ArrayList<>();
        int id = 0;
        int[] neighbors = sp.neighbors(overlap, id);
        System.out.println("Neighbors of #" + id);
        for (int i = 0; i < neighbors.length; i++) {
            System.out.println("nb idx=" + neighbors[i] + " overlap=" + overlap.get(i));
        }
    }

    @Test
    public void checkMaxNeighborsEMA() {
        sp = new SpatialPooler(10, 2, 1, 1, in, 0.02);
        List<Integer> overlap = new ArrayList<>();
        int id = 0;
        int[] neighbors = sp.neighbors(overlap, id);
        float ema = sp.maxNeighborsEMA(neighbors);
        System.out.println("Max neighbors' EMA=" + ema);
    }
}
