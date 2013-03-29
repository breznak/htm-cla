/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;
import htm.utils.HelperMath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private List<Column> neighbors;
    private final int syn_idx[]; //TODOoptimize to Bit mask
    private final float[] perm;
    protected final PARENT parent;
    public final int id;
    protected final CircularList output;
    private boolean learning = true;
    //synapses
    private static final int DEFAULT_NUM_INPUT_SYNAPSES = 10;
    private final int NUM_INPUT_SYNAPSES;// = 60;
    public static final float CONNECTED_SYNAPSE_PERM = 0.2f;
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
    private static final int SLIDING_WINDOW = 2; //window size for moving average //FIXME should be same as HISTORY_STEPS ??
    //helper
    private static final double _ALPHA = 1 / (double) SLIDING_WINDOW; //helper for moving avg
    private int _output = 0; // current output as int
    private int _oldHash = 0;
    private float _inhibitionRadiusOld = -1; //trick != inhibitionRadius
    private SummaryStatistics stats = new SummaryStatistics();
    private SpatialPooler sp;
    private List<Integer> nbOverlapValues = new ArrayList<>();

    public Column(PARENT parent, int id, int histSize, double sparsity) {
        this.parent = parent;
        sp = (SpatialPooler) parent;
        this.id = id;
        this.output = new CircularList(histSize, 1);
        int diff = (int) (new Random().nextGaussian() * DEFAULT_NUM_INPUT_SYNAPSES * 0.2); //+-20%
        NUM_INPUT_SYNAPSES = HelperMath.inRange(DEFAULT_NUM_INPUT_SYNAPSES + diff, 1, parent.input.size());
        MIN_OVERLAP = HelperMath.inRange(NUM_INPUT_SYNAPSES / 30, 1, 3);//TODO what range?
        DESIRED_LOCAL_ACTIVITY = (int) HelperMath.inRange((int) (parent.size() * sparsity), 0, Math.min(parent.size(), Math.pow(sp.DEFAULT_INHIBITION_RADIUS * 2, 2)) - 1);
        int center = new Random().nextInt(NUM_INPUT_SYNAPSES);
        syn_idx = initSynapsesIdx();
        perm = initSynapsePerm(center, syn_idx);
    }

    // new synapse indexes
    protected int[] initSynapsesIdx() {
        return new UniformIntegerDistribution(0, parent.input.size() - 1).sample(NUM_INPUT_SYNAPSES);
    }

    //new synapse permanence
    protected float[] initSynapsePerm(int center, int[] synapse_idx) {
        //67% samples lie within 1std radius -> 0.9std==50%
        double std = parent.input.size() / (double) parent.size(); //input size / #peers
        NormalDistribution gauss = new NormalDistribution(center, std);
        float[] tmp = new float[NUM_INPUT_SYNAPSES];
        double scale = 1.5 * CONNECTED_SYNAPSE_PERM / gauss.probability(center, center + 0.68 * std); // scale to make 50% samples >= CONNECTED_SYNAPSE_PERM
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = (float) ((0.5 - gauss.probability(center, center + Math.abs(center - synapse_idx[i]))) * scale); //FIXME correctly scale to CONNECTED_PERM
            ///  System.err.println("std " + std + " scale " + scale + " center " + center + "  idx=" + syn_idx[i] + " perm " + tmp[i]);
            tmp[i] = (float) new NormalDistribution(CONNECTED_SYNAPSE_PERM, PERMANENCE_INC / 2d).sample();
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
        float tmp;
        //while ((tmp = parent.input.hashCode()) == _oldHash) {
        //  System.gc();
        //Thread.yield();
        // }
        //_oldHash = tmp;

        //phase 1
        if (this.id == 0) {
            System.out.println("here sir");
        }
        overlap = overlap();
        Thread.yield();

        //phase 2
        tmp = sp.getInhibitionRadius();
        if ((int) tmp != (int) this._inhibitionRadiusOld) { //caching
            neighbors = sp.neighbors(this.id);
            this._inhibitionRadiusOld = tmp;
        }

        //kth best
        nbOverlapValues = getSortedOverlaps(neighbors);
        if (overlap > 0 && (nbOverlapValues.size() < DESIRED_LOCAL_ACTIVITY || overlap >= nbOverlapValues.get(DESIRED_LOCAL_ACTIVITY))) {//>=minLocalActivity //TODO speedup
            output.add(CircularList.BIT_1);
            _output = 1;
        } else {
            output.add(CircularList.BIT_0);
            _output = 0;
        }
        Thread.currentThread().yield();

        //phase 3
        if (_output == 1) {
            updateSynapses();
        }
        Thread.yield();
        float minDutyCycle = 0.01f * sp.maxNeighborsFiringRate(neighbors);
        //compute moving average
        emaActive = (float) (_ALPHA * emaActive + (1 - _ALPHA) * _output);
        if (learning) {
            if (emaActive > minDutyCycle) {
                boost = 1; //TODO add Thread priorities?
            } else {
                // too uncompetitive compared to other columns
                boost *= BOOST_ACCEL;
            }
        }
        emaOverlap = (float) (_ALPHA * emaOverlap + (1 - _ALPHA) * overlap);
        if (emaOverlap <= minDutyCycle) { //FIXME really use minDutyCycle here??
            //wrong subset of synapses is being used now, try to find useful ones
            increaseAllPermanences(0.1f * CONNECTED_SYNAPSE_PERM);
        }
        Thread.yield();

        //update avg receptive field size
        tmp = sp.getInhibitionRadius();
        /// if (inh != _inhibitionRadiusOld) { //cache multithreaded, off-sync
        sp.setInhibitionRadius(((parent.size() - 1) * tmp + receptiveFieldSize()) / parent.size()); //avg//FIXME this is growing!
        _inhibitionRadiusOld = tmp;
        /// }
    }

    protected float receptiveFieldSize() {
//FIXME how to do it? :)...i think ok now, just check
        /*
         * The radius of the average connected receptive field size of all the columns.
         >>>The connected receptive field size<<<< of a column includes only the connected
         synapses (those with permanence values >= connectedPerm). This is used
         to determine the extent of lateral inhibition between columns.
         */
        for (int i = 0; i < syn_idx.length; i++) {
            if (perm[i] >= CONNECTED_SYNAPSE_PERM) {
                stats.addValue(syn_idx[i]);
            }
        }
        float i = Math.round(stats.getStandardDeviation());
        stats.clear();
        return i;
    }

    protected void increaseAllPermanences(float inc) {
        for (int i = 0; i < perm.length; i++) {
            perm[i] += inc;
        }
    }

    protected void learning(boolean onOff) {
        learning = onOff;
    }

    @Override
    public String toString() {
        SpatialPooler sp = ((SpatialPooler) parent);
        return "Column id=" + id + " (" + sp.getCoordinates(id) + ") is active=" + _output + " inhibitionR=" + sp.getInhibitionRadius() + " boost=" + boost + " overalap=" + overlap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }
        return ((Column) obj).id == this.id;
    }

    public String toString(int i) {
        return "" + _output;
    }

    private void updateSynapses() {
        for (int i = 0; i < perm.length; i++) {
            if (parent.input.get(0).get(syn_idx[i])) { //inc syn on ON input, dec on OFF input
                perm[i] += PERMANENCE_INC;
            } else {
                perm[i] -= PERMANENCE_DEC;
            }
            perm[i] = (float) HelperMath.inRange(perm[i], 0, 1);
        }
    }

    private List<Integer> getSortedOverlaps(List<Column> neighbors) {
        List<Integer> overlaps = new ArrayList<>();
        for (Column c : neighbors) {
            overlaps.add(c.overlap);
        }
        Collections.sort(overlaps);
        Collections.reverse(overlaps);  //TODO use reverse sort
        return overlaps;
    }
}
