/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.utils;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author marek
 */
public class Playground {

    public Playground() {
        int loop = 1000000;
        long t, tt;

        // empty for
        System.out.print("Empty for =" + loop + " = ");
        t = System.currentTimeMillis();
        // new Playground();
        long[] l = new long[100];
        List<Integer> sto = HelperMath.seq(10);
        for (int i = 0;
                i < l.length;
                i++) {
            Collections.shuffle(sto);
        }
        tt = System.currentTimeMillis();

        System.out.println(tt - t + "ms" + (byte) ((char) -1));
    }

    @org.junit.Test
    public void makeArray() {
        for (int z = 0; z < 100; z++) {
            Runnable t = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        long l[] = new long[1000];
                    }
                }
            };
            new Thread(t).start();
        }
    }
}
