/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.Input;
import htm.utils.HelperMath;
import java.util.Random;

/**
 *
 * @author marek
 */
public class Column extends LayerAbstract {

    private static int colCounter = 0;
    int syn_idx[];

    public Column(Input in) {
        super(in, null, colCounter++, 2);

        syn_idx = initSynapses();
    }
    static final int numInputSynapses = 60;
    float[] inputSynapses = new float[numInputSynapses];
    float connetedSynapse = 0.4f;
    static final int FANOUT = 5;

    static int[] initSynapses() {
        int tmp[] = new int[numInputSynapses];
        for (int i = 0; i < tmp.length; i++) {
            int center = new Random().nextInt(numInputSynapses); //TODO not inRange - makes edges more active
            tmp[i] = HelperMath.inRange(HelperMath.normalDistribution(center, FANOUT), 0, tmp.length); //TODO FANOUT randomization + proportional to input size
        }
        return tmp;
    }
}
