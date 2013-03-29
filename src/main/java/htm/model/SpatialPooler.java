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

/**
 *
 * @author marek
 */
public class SpatialPooler extends LayerAbstract<Column<SpatialPooler>> {

    protected static final int DEFAULT_INHIBITION_RADIUS = 5;
    private final AtomicInteger inhibitionRadius = new AtomicInteger(); //averageReceptiveFieldSize
    protected boolean learning = true;

    public SpatialPooler(int dimX, int dimY, int id, int timeSteps, CircularList input, double sparsity) {
        super(dimX, dimY, id, timeSteps, input);
        setInhibitionRadius(DEFAULT_INHIBITION_RADIUS);
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
    public float maxNeighborsFiringRate(List<Column> neighbors) {
        float maxFiringR = -1;
        for (Column nb : neighbors) {
            float m = nb.emaActive;
            if (m > maxFiringR) {
                maxFiringR = m;
            }
        }
        return maxFiringR;
    }

    /**
     * internally, we keep a float there, so need to cast appropriately
     */
    protected float getInhibitionRadius() {
        return Float.intBitsToFloat(inhibitionRadius.get());
    }

    protected void setInhibitionRadius(float f) {
        inhibitionRadius.set(Float.floatToRawIntBits(f));
    }

    protected List<Column> neighbors(int curColumnID) {
        List<Column> found = new ArrayList<>();
        Point me = getCoordinates(curColumnID);
        Column<SpatialPooler> cur;
        int inhib = (int) Math.floor(getInhibitionRadius());
        /**
         * here we can switch geometry interpretations of the spatial pooler, a)
         * planar -- rectangle - with edges and corners
         *
         * b) planar -- sphere - no edges, values flip to "other" side
         */
        for (int x = me.x - inhib; x < me.x + inhib; x++) {
            for (int y = me.y - inhib; y < me.y + inhib; y++) {
                // a) rectangle
                //cur = part(HelperMath.inRange(x, 0, dimX - 1), HelperMath.inRange(y, 0, dimY - 1));
                //b) sphere
                cur = part(((x % dimX) + dimX) % dimX, ((y % dimY) + dimY) % dimY); // fuckin hack for broken modulo, must be non-negative!

                if (found.contains(cur) || getCoordinates(cur.id).equals(me)) {
                    continue;
                }
                found.add(cur);
            }
        }
        return found;
    }

    protected void learning(boolean onOff) {
        for (Column c : parts) {
            c.learning(onOff);
        }
        this.learning = onOff;
    }

    @Override
    public String toString() {
        String s = this.getClass().getSimpleName() + " [id=" + id + " inhibR=" + getInhibitionRadius() + " " + dimX + "x" + dimY + " ] \n";
        for (int i = 0; i < dimY; i++) {
            for (int j = 0; j < dimX; j++) {
                s += part(i, j).toString(1) + " ";
            }
            s += "\n";
        }
        return s;
    }
}
