/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.BinaryVectorInput;
import htm.model.input.Input;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        float ema = sp.maxNeighborsFiringRate(neighbors);
        System.out.println("Max neighbors' EMA=" + ema);
    }
    /*
     @Test
     public void checkBigMemory() {
     System.out.println("BigMemory test, cca 2gb @ 100k columns!");
     Input in = new BinaryVectorInput(1, 100000);
     sp = new SpatialPooler(100, 1000, 1, 10, in, 0.05);
     }

     @Test
     public void checkBigParallel() {
     Input in = new BinaryVectorInput(1, 1000000);
     in.setRawInput(in.randomSample());
     sp = new SpatialPooler(1000, 10, 1, 2, in, 0.02);
     System.out.println("start!");
     long s = System.currentTimeMillis();
     ExecutorService pool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
     int RUNS = 10;
     for (int r = 0; r < RUNS; r++) {

     for (int i = 0; i < sp.size(); i++) {
     pool.submit(sp.getColumn(i));
     }
     }
     try {
     pool.awaitTermination(1, TimeUnit.MILLISECONDS);
     System.out.println("" + sp.toString());
     }
     catch (InterruptedException ex) {
     Logger.getLogger(SpatialPoolerTest.class.getName()).log(Level.SEVERE, null, ex);
     }
     pool.shutdown();
     long t = System.currentTimeMillis();
     System.out.println("parallel took " + (t - s) + "ms!");
     System.out.println("" + sp.toString());
     }
     */

    @Test
    public void checkTestParallel() {
        Input in = new BinaryVectorInput(1, 1000);
        //create 5 test inputs
        List<BitSet> patterns = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            patterns.add(in.randomSample());
        }
        sp = new SpatialPooler(100, 100, 1, 2, in, 0.1);
        System.out.println("start!");
        long s = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(1, Executors.defaultThreadFactory());
        int RUNS = 5;
        for (BitSet p : patterns) {
            in.setRawInput(p);
            for (int r = 0; r < RUNS; r++) {

                for (int i = 0; i < sp.size(); i++) {
                    pool.submit(sp.getColumn(i));
                }
            }
            System.out.println("pattern " + p + " iter " + " \n" + sp);
        }
        try {
            pool.shutdown();
            boolean fin = pool.awaitTermination(10, TimeUnit.MILLISECONDS);
            System.out.println("FIN " + fin);
        }
        catch (InterruptedException ex) {
            Logger.getLogger(SpatialPoolerTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        long t = System.currentTimeMillis();
        System.out.println("parallel took " + (t - s) + "ms!");
        System.out.println("" + sp.toString());


        sp.learning(false);
        for (int i = 0; i < 2; i++) {
            in.setRawInput(patterns.get(i));
            System.out.println("" + sp.input.toString());
            sp.getColumn(0).run();
        }
    }
}
