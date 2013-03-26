/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author marek
 */
public class SpatialPooler extends LayerAbstract<Column<SpatialPooler>[][]> { //TODO move [][] to ArrayList

    protected static final int DEFAULT_INHIBITION_RADIUS = 5;
    protected final AtomicInteger inhibitionRadius = new AtomicInteger(SpatialPooler.DEFAULT_INHIBITION_RADIUS); //averageReceptiveFieldSize

    public SpatialPooler(int dimX, int dimY, CircularList input) {
        super(new Column[dimX][dimY], dimX, dimY, 0, 1, input);

        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                parts[i][j] = new Column<>(this, i * dimX + j, 1);
            }
        }
    }

    public Point getCoordinates(int column_id) {
        int a = column_id % dimY;
        int b = (column_id - a) / dimX;
        return new Point(a, b); //TODO test
    }

    public Column<SpatialPooler> getColumn(int column_id) {
        Point c = getCoordinates(column_id);
        return parts[c.x][c.y];
    }

    /**
     * max moving average (EMA) from neighbors
     *
     * @param neighbor_idx
     * @return
     */
    public float maxNeighborsEMA(int[] neighbor_idx) {
        float maxEMA = -1;
        for (int i = 0; i < neighbor_idx.length; i++) {
            float m = getColumn(neighbor_idx[i]).emaActive;
            if (m > maxEMA) {
                maxEMA = m;
            }
        }
        return maxEMA;
    }

    protected int[] neighbors(ArrayList<Integer> overlapValues, int curColumnID) {
        ArrayList<Integer> found = new ArrayList<>();
        //TODO for for
        Point me = getCoordinates(curColumnID);
        Column cur;
        int inhib = inhibitionRadius.get();
        for (int x = me.x - inhib; x < me.x + inhib; x++) {
            for (int y = me.y - inhib; y < me.y + inhib; y++) {
                if (x == me.x && y == me.y) {
                    continue;
                }
                cur = parts[x][y];
                found.add(cur.id);
                overlapValues.add(cur.overlap.get());
            }
        }
        return ArrayUtils.toPrimitive(found.toArray(new Integer[found.size()]));
    }
}
