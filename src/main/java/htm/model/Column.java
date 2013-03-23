/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.Input;
import java.util.Random;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

/**
 *
 * @author marek
 */
public class Column extends LayerAbstract {

    private static int colCounter = 0;
    int syn_idx[];
    float[] perm;

    public Column(Input in) {
        super(in, null, colCounter++, 2);
        center = new Random().nextInt(numInputSynapses);
        syn_idx = initSynapsesIdx();
        perm = initSynapsePerm();
    }
    static final int numInputSynapses = 60;
    static final float connetedSynapse = 0.4f;
    final int center;
    static final int minOverlap = 2;
    private float boost = 1.1f;

    // new synapse indexes
    int[] initSynapsesIdx() {
        return new UniformIntegerDistribution(0, input.get(0).length()).sample(numInputSynapses);
    }

    int overlap() {
        int o = 0;
        for (int i = 0; i < syn_idx.length; i++) {
            if (perm[i] > connetedSynapse && (input.get(0).get(syn_idx[i]))) {
                o++;
            }
        }
        if (o < minOverlap) {
            o = 0;
        }
        return o *= boost;
    }

    //new synapse permanence
    float[] initSynapsePerm() {
        float[] tmp = new float[numInputSynapses];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = (float) new NormalDistribution(0, Math.abs(center - syn_idx[i])).probability(syn_idx[i]);
        }
        return tmp;
    }
}
