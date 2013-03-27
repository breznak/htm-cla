/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.utils.CircularList;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author marek
 */
public class SpatialPooler extends LayerAbstract<Column<SpatialPooler>> {

    protected static final int DEFAULT_INHIBITION_RADIUS = 5;
    protected final AtomicInteger inhibitionRadius = new AtomicInteger(SpatialPooler.DEFAULT_INHIBITION_RADIUS); //averageReceptiveFieldSize
    protected boolean learning = true;

    public SpatialPooler(int dimX, int dimY, int id, int timeSteps, CircularList input, double sparsity) {
        super(dimX, dimY, id, timeSteps, input);

        for (int i = 0; i < this.dimX * this.dimY; i++) { //! this.dimX is important, as dimX/Y get reordered in LayerAbstract, dimX==bigger
            Point p = getCoordinates(i);
            int x = p.x;
            int y = p.y;
            setPart(new Column<>(this, i, timeSteps, sparsity), x, y);
        }
        learning(true);
    }

    public Point getCoordinates(int column_id) {
        int a = column_id % dimX;
        int b = (column_id - a) / dimX;
        return new Point(a, b);
    }

    public Column<SpatialPooler> getColumn(int column_id) {
        Point c = getCoordinates(column_id);
        return part(c.x, c.y);
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
        List<Integer> found = new ArrayList<>();
        Point me = getCoordinates(curColumnID);
        Column<SpatialPooler> cur;
        int inhib = inhibitionRadius.get();
        /**
         * here we can switch geometry interpretations of the spatial pooler, a)
         * planar -- rectangle - with edges and corners
         *
         * b) planar -- sphere - no edges, values flip to "other" side
         */
        for (int x = me.x - inhib; x < me.x + inhib; x++) {
            for (int y = me.y - inhib; y < me.y + inhib; y++) {
                if (x == me.x && y == me.y) {
                    continue;
                }
                // a) rectangle
                //cur = part(HelperMath.inRange(x, 0, dimX - 1), HelperMath.inRange(y, 0, dimY - 1));
                //b) sphere
                cur = part(((x % dimX) + dimX) % dimX, ((y % dimY) + dimY) % dimY); // fuckin hack for broken modulo, must be non-negative!
                if (!found.contains(cur.id)) {
                    found.add(cur.id);
                    overlapValues.add(cur.overlap);
                }
            }
        }
        return ArrayUtils.toPrimitive(found.toArray(new Integer[found.size()]));
    }

    protected void learning(boolean onOff) {
        for (Column c : parts) {
            c.learning(onOff);
        }
        this.learning = onOff;
    }

    @Override
    public String toString() {
        String s = this.getClass().getSimpleName() + " [id=" + id + " inhibR=" + inhibitionRadius.get() + " " + dimX + "x" + dimY + " ] \n";
        for (int i = 0; i < dimY; i++) {
            for (int j = 0; j < dimX; j++) {
                s += part(i, j).toString(1) + " ";
            }
            s += "\n";
        }
        return s;
    }
}
