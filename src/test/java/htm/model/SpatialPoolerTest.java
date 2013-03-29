/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.BinaryVectorInput;
import htm.model.input.Input;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
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
        int id = 0;
        List<Column> neighbors = new ArrayList<>(sp.neighbors(id));
        System.out.println("Neighbors of #" + id);
        for (int i = 0; i < neighbors.size(); i++) {
            System.out.println("nb =" + neighbors.get(i));
        }
    }

    @Test
    public void checkMaxNeighborsEMA() {
        sp = new SpatialPooler(10, 2, 1, 1, in, 0.02);
        int id = 0;
        Collection<Column> neighbors = sp.neighbors(id);
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
        Input in = new BinaryVectorInput(1, 10000);
        //create 5 test inputs
        List<BitSet> patterns = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            patterns.add(in.randomSample());
        }
        sp = new SpatialPooler(50, 10, 1, 2, in, 0.02);
        System.out.println("start!");
        long s = System.currentTimeMillis();
        ExecutorService pool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
        int RUNS = 10;
        for (int r = 0; r < RUNS; r++) {

            for (BitSet p : patterns) {
                in.setRawInput(p);

                pool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
                for (int i = 0; i < sp.size(); i++) {
                    pool.submit(sp.getColumn(i));
                }
                pool.shutdown();

                try {
                    boolean fin = pool.awaitTermination(20, TimeUnit.SECONDS);
                    System.out.println("FIN " + fin);
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(SpatialPoolerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("pattern " + p + " iter " + " \n" + sp);

            }
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
        //  System.out.println("" + sp.toString());

        System.out.flush();
        sp.learning(false);
        for (int i = 0; i < patterns.size(); i++) {
            in.setRawInput(patterns.get(i));
            System.out.println("TEST #" + i + " INPUT=" + sp.input.toString());


            pool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
            for (int c = 0; c < sp.size(); c++) {
                pool.submit(sp.getColumn(c));
            }
            pool.shutdown();
            try {
                boolean fin = pool.awaitTermination(10, TimeUnit.SECONDS);
                System.out.println("FIN " + fin);
            }
            catch (InterruptedException ex) {
                Logger.getLogger(SpatialPoolerTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(">>> \n" + sp);
        }
    }
}
