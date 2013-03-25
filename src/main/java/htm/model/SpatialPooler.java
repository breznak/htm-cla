/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.Input;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author marek
 */
public class SpatialPooler extends LayerAbstract<Object, Column[][]> {

    final int dimX;
    final int dimY;
    protected int inhibitionRadius = 0;
    protected static final int DEFAULT_INHIBITION_RADIUS = 5;

    public SpatialPooler(int dimX, int dimY, Input in) {
        super(new Column[dimX][dimY], null, 0, 1);
        this.dimX = dimX;
        this.dimY = dimY;

        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                parts[i][j] = new Column(in, this, (i - 1) * dimX + j, 1);
            }
        }

    }

    public Point getCoordinates(int column_id) {
        int a = column_id % dimY;
        int b = (column_id - a) / dimX;
        return new Point(a, b); //TODO test
    }

    public Column getColumn(int column_id) {
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
        for (int x = me.x - inhibitionRadius; x < me.x + inhibitionRadius; x++) {
            for (int y = me.y - inhibitionRadius; y < me.y + inhibitionRadius; y++) {
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
    protected static final AtomicInteger averageReceptiveFieldSize = new AtomicInteger(SpatialPooler.DEFAULT_INHIBITION_RADIUS);
}
