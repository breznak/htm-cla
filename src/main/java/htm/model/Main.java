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

/**
 *
 * @author marek
 */
public class Main {

    public static void main(String[] args) {
        Main m = new Main();
        m.checkTestParallel();
    }
    SpatialPooler sp;

    public void checkTestParallel() {
        Input in = new BinaryVectorInput(1, 100);
        //create 5 test inputs
        List<BitSet> patterns = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            patterns.add(in.randomSample());
        }
        sp = new SpatialPooler(10, 10, 1, 2, in, 0.5);
        sp.learning(true);

        for (int i = 0; i < 2; i++) {
            for (int m = 0; m < 6; m++) {
                in.setRawInput(patterns.get(i));
                System.out.println("" + sp.input.toString());
                sp.getColumn(0).run();
            }
            System.out.println("" + sp);
        }
    }
}
