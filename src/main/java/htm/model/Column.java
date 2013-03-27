/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;
import htm.utils.HelperMath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 *
 * @author marek
 */
public class Column<PARENT extends LayerAbstract> implements Runnable {

    //local fields
    private int[] neighbor_idx;
    private final int syn_idx[]; //TODOoptimize to Bit mask
    private final float[] perm;
    private final PARENT parent;
    public final int id;
    private final CircularList output;
    //synapses
    private static final int DEFAULT_NUM_INPUT_SYNAPSES = 60;
    private final int NUM_INPUT_SYNAPSES;// = 60;
    private static final float CONNECTED_SYNAPSE_PERM = 0.2f;
    private static final float PERMANENCE_DEC = 0.05f;
    private static final float PERMANENCE_INC = 0.05f;
    //overlap columns
    private final int MIN_OVERLAP; // = 2;
    private final int DESIRED_LOCAL_ACTIVITY; // = 5; //5 winning columns,  computed lateral inhibition
    private float boost = 1.0f;
    private static final float BOOST_ACCEL = 1.2f; //>1
    protected int overlap = 0;
    private float emaOverlap = 0; //ema for overlap
    //moving average
    protected float emaActive = 0; //exponential moving average for Activation (=output ==1)
    private static final int SLIDING_WINDOW = 100; //window size for moving average
    //helper
    private static final double _ALPHA = 1 / SLIDING_WINDOW; //helper for moving avg
    private int _output = 0; // current output as int
    private int _oldHash = 0;
    private int _inhibitionRadiusOld = -1; //trick != inhibitionRadius
    private SummaryStatistics stats = new SummaryStatistics();

    public Column(PARENT parent, int id, int histSize, double sparsity) {
        this.parent = parent;
        this.id = id;
        this.output = new CircularList(histSize, 1);
        int diff = (int) (new Random().nextGaussian() * DEFAULT_NUM_INPUT_SYNAPSES * 0.2); //+-20%
        NUM_INPUT_SYNAPSES = HelperMath.inRange(DEFAULT_NUM_INPUT_SYNAPSES + diff, 1, parent.input.size());
        MIN_OVERLAP = Math.max(1, NUM_INPUT_SYNAPSES / 30);
        DESIRED_LOCAL_ACTIVITY = HelperMath.inRange((int) (parent.size() * sparsity), 0, parent.size() - 1);
        int center = new Random().nextInt(NUM_INPUT_SYNAPSES);
        syn_idx = initSynapsesIdx();
        perm = initSynapsePerm(center);
    }

    // new synapse indexes
    protected int[] initSynapsesIdx() {
        return new UniformIntegerDistribution(0, parent.input.size() - 1).sample(NUM_INPUT_SYNAPSES);
    }

    //new synapse permanence
    protected float[] initSynapsePerm(int center) {
        //67% samples lie within 1std radius -> 0.9std==50%
        double std = parent.input.size() / (double) parent.size(); //input size / #peers
        NormalDistribution gauss = new NormalDistribution(center, std);
        float[] tmp = new float[NUM_INPUT_SYNAPSES];
        double scale = CONNECTED_SYNAPSE_PERM / gauss.probability(center + 0.9 * std); // scale to make 50% samples >= CONNECTED_SYNAPSE_PERM
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = (float) (gauss.probability(syn_idx[i]) * scale); //FIXME correctly scale to CONNECTED_PERM
        }
        return tmp;
    }

    //overlap with input pattern
    protected int overlap() {
        int o = 0;
        for (int i = 0; i < syn_idx.length; i++) {
            if (perm[i] > CONNECTED_SYNAPSE_PERM && parent.input(syn_idx[i])) {
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
        while ((tmp = parent.input.hashCode()) == _oldHash) {
            System.gc();
            Thread.yield();
        }
        _oldHash = tmp;
        //phase 1
        overlap = overlap();
        Thread.yield();

        SpatialPooler sp = (SpatialPooler) parent;
        //phase 2
        ArrayList<Integer> nbOverlapValues = new ArrayList<>();
        //caching
        if ((tmp = sp.inhibitionRadius.get()) != this._inhibitionRadiusOld) {
            neighbor_idx = sp.neighbors(nbOverlapValues, this.id);
            this._inhibitionRadiusOld = tmp;
        }
        Collections.sort(nbOverlapValues);
        Collections.reverse(nbOverlapValues);  //TODO use reverse sort
        int minLocalActivity = nbOverlapValues.get(DESIRED_LOCAL_ACTIVITY); //kth best
        if (overlap > 0 && overlap >= minLocalActivity) {//TODO speedup
            output.add(CircularList.BIT_1);
            _output = 1;
        } else {
            output.add(CircularList.BIT_0);
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
        emaOverlap = (float) (_ALPHA * emaOverlap + (1 - _ALPHA) * overlap);
        if (emaOverlap < minDutyCycle) {
            //wrong subset of synapses is being used now, try to find useful ones
            increaseAllPermanences(0.1f * CONNECTED_SYNAPSE_PERM);
        }
        Thread.yield();

        //update avg receptive field size
        tmp = sp.inhibitionRadius.get();
        if (tmp != _inhibitionRadiusOld) { //cache multithreaded, off-sync
            sp.inhibitionRadius.set(Math.round(((parent.size() - 1) * tmp + receptiveFieldSize()) / parent.size())); //avg
            _inhibitionRadiusOld = tmp;
        }
    }

    protected int receptiveFieldSize() {
//FIXME how to do it? :)
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

    protected void increaseAllPermanences(float inc) {
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
