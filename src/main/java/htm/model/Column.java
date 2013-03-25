/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.Input;
import htm.utils.CircularList;
import htm.utils.HelperMath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 *
 * @author marek
 */
public class Column<PARENT> extends LayerAbstract<PARENT, Input> implements Runnable {

    private int[] neighbor_idx;
    private final int syn_idx[]; //TODOoptimize to Bit mask
    private final float[] perm;
    private final int DESIRED_LOCAL_ACTIVITY = 5; //5 winning columns, TODO compute
    private static final float PERMANENCE_DEC = 0.05f;
    private static final float PERMANENCE_INC = 0.05f;

    public Column(Input in, PARENT parent, int id, int histSize) {
        super(in, parent, id, histSize);
        int center = new Random().nextInt(NUM_INPUT_SYNAPSES);
        syn_idx = initSynapsesIdx();
        perm = initSynapsePerm(center);
    }
    static final int NUM_INPUT_SYNAPSES = 60;
    static final float CONNECTED_SYNAPSE_PERM = 0.2f;
    static final int MIN_OVERLAP = 2;
    private float boost = 1.0f;
    final AtomicInteger overlap = new AtomicInteger(0);
    //moving average
    protected float emaActive = 0; //exponential moving average for Activation (=output ==1)
    private float emaOverlap = 0; //ema for overlap
    private static final int SLIDING_WINDOW = 100; //window size for moving average
    private static final double _ALPHA = 1 / SLIDING_WINDOW; //helper for moving avg
    //helper
    private int _output = 0; // current output as int
    private int _oldHash = 0;
    private int _inhibitionRadiusOld = -1; //trick != inhibitionRadius
    //boost
    private static final float BOOST_ACCEL = 1.2f; //>1

    // new synapse indexes
    int[] initSynapsesIdx() {
        return new UniformIntegerDistribution(0, input.get(0).length()).sample(NUM_INPUT_SYNAPSES);
    }

    //new synapse permanence
    float[] initSynapsePerm(int center) {
        float[] tmp = new float[NUM_INPUT_SYNAPSES];
        float maxNormalDist = 0.4f; // maximum of normal distribution, used to scale around our CONNECTED_SYNAPSE_PERM
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = (float) (new NormalDistribution(center, 2).probability(syn_idx[i]) / maxNormalDist) * CONNECTED_SYNAPSE_PERM; //TODO scale to CONNECTED_PERM
        }
        return tmp;
    }

    //overlap with input pattern
    private int overlap() {
        int o = 0;
        for (int i = 0; i < syn_idx.length; i++) {
            if (perm[i] > CONNECTED_SYNAPSE_PERM && (input.get(0).get(syn_idx[i]))) {
                o++;
            }
        }
        if (o < MIN_OVERLAP) {
            o = 0;
        }
        o *= boost;
        return o;
    }

    @Override
    public void run() {
        int tmp;
        while ((tmp = input.hashCode()) == _oldHash) {
            Thread.yield();
        }
        _oldHash = tmp;
        //phase 1
        overlap.set(overlap());
        SpatialPooler sp = ((SpatialPooler) parent);
        Thread.yield();

        //phase 2
        ArrayList<Integer> nbOverlapValues = new ArrayList<>();
        //caching
        if ((tmp = sp.inhibitionRadius) != this._inhibitionRadiusOld) {
            neighbor_idx = sp.neighbors(nbOverlapValues, this.id);
            this._inhibitionRadiusOld = tmp;
        }
        Collections.sort(nbOverlapValues);
        Collections.reverse(nbOverlapValues);  //TODO use reverse sort
        int minLocalActivity = nbOverlapValues.get(DESIRED_LOCAL_ACTIVITY); //kth best
        if (overlap.get() > 0 && overlap.get() >= minLocalActivity) {
            output.add(0, CircularList.BIT_1);
            _output = 1;
        } else {
            output.add(0, CircularList.BIT_0);
            _output = 0;
        }
        Thread.currentThread().yield();

        //phase 3
        if (output.get(0) == CircularList.BIT_1) {
            for (int i = 0; i < perm.length; i++) {
                if (perm[i] >= CONNECTED_SYNAPSE_PERM) {
                    perm[i] += PERMANENCE_INC;
                } else {
                    perm[i] -= PERMANENCE_DEC;
                }
                perm[i] = (float) HelperMath.inRange(perm[i], 0, 1);
            }
        }
        Thread.yield();
        //compute moving average
        float minDutyCycle = 0.01f * sp.maxNeighborsEMA(neighbor_idx);
        emaActive = (float) (_ALPHA * emaActive + (1 - _ALPHA) * _output);
        if (emaActive > minDutyCycle) {
            boost = 1; //TODO add Thread priorities?
        } else {
            // too uncompetitive compared to other columns
            boost *= BOOST_ACCEL;
        }
        emaOverlap = (float) (_ALPHA * emaOverlap + (1 - _ALPHA) * overlap.get());
        if (emaOverlap < minDutyCycle) {
            //wrong subset of synapses is being used now, try to find useful ones
            increaseAllPermanences(0.1f * CONNECTED_SYNAPSE_PERM);
        }
        Thread.yield();

        //update avg receptive field size
        if (sp.inhibitionRadius != _inhibitionRadiusOld) { //cache multithreaded, off-sync
            int n = sp.dimX * sp.dimY;
            sp.inhibitionRadius = Math.round(((n - 1) * sp.inhibitionRadius + receptiveFieldSize()) / n); //avg
        }
    }
    private SummaryStatistics stats = new SummaryStatistics();

    int receptiveFieldSize() {
//TODO how to do it? :)
        /*
         * The radius of the average connected receptive field size of all the columns.
         >>>The connected receptive field size<<<< of a column includes only the connected
         synapses (those with permanence values >= connectedPerm). This is used
         to determine the extent of lateral inhibition between columns.
         */
        for (float f : perm) {
            if (f >= CONNECTED_SYNAPSE_PERM) {
                stats.addValue(f);
            }
        }
        int i = (int) Math.round(stats.getStandardDeviation());
        stats.clear();
        return i;
    }

    private void increaseAllPermanences(float inc) {
        for (int i = 0; i < perm.length; i++) {
            perm[i] += inc;
        }
    }

    @Override
    public String toString() {
        SpatialPooler sp = ((SpatialPooler) parent);
        return "Column id=" + id + " (" + sp.getCoordinates(id) + ") is active=" + _output + " inhibitionR=" + sp.inhibitionRadius + " boost=" + boost;
    }

    public String toString(int i) {
        return "" + _output;
    }
}
