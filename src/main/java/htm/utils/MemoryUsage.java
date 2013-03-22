/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.utils;

import com.javamex.classmexer.MemoryUtil;
import com.javamex.classmexer.MemoryUtil.VisibilityFilter;
import java.util.BitSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author marek
 */
public class MemoryUsage {

    /**
     * measure memory footprint of java objects! run: $ javac -classpath
     * /home/marek/devel/DP/htm-java/target/classes:/home/marek/.m2/repository/junit/junit/4.8.2/junit-4.8.2.jar:/home/marek/devel/DP/htm-java/src/main/java/com/javamex/classmexer/classmexer.jar
     * htm/utils/MemoryUsage.java
     *
     * $ java -classpath
     * /home/marek/devel/DP/htm-java/target/classes:/home/marek/.m2/repository/junit/junit/4.8.2/junit-4.8.2.jar:/home/marek/devel/DP/htm-java/src/main/java/com/javamex/classmexer/classmexer.jar
     * -javaagent:./com/javamex/classmexer/classmexer.jar htm.utils.MemoryUsage
     *
     * @param args
     */
    public static void main(String[] args) {
        Object o;
        long noBytes;
        int elements = 1000000;

        o = new long[elements];
        noBytes = MemoryUtil.deepMemoryUsageOf(o, VisibilityFilter.ALL);
        System.out.println("Memory usage of long[" + elements + "] =" + noBytes + " Bytes");

        o = new Boolean[elements];
        noBytes = MemoryUtil.deepMemoryUsageOf(o, VisibilityFilter.ALL);
        System.out.println("Memory usage of Boolean[" + elements + " ] =" + noBytes + " Bytes");

        o = new CopyOnWriteArrayList<Boolean>(new Boolean[elements]);
        noBytes = MemoryUtil.deepMemoryUsageOf(o, VisibilityFilter.ALL);
        System.out.println("Memory usage of COWArrayList<Boolean(Boolean[" + elements + "]) =" + noBytes + " Bytes");

        o = new byte[elements];
        noBytes = MemoryUtil.deepMemoryUsageOf(o, VisibilityFilter.ALL);
        System.out.println("Memory usage of byte[" + elements + "] =" + noBytes + " Bytes");

        o = new BitSet(elements);
        noBytes = MemoryUtil.deepMemoryUsageOf(o, VisibilityFilter.ALL);
        System.out.println("Memory usage of BitSet[" + elements + "] =" + noBytes + " Bytes");


    }
}
