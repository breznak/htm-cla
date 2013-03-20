/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import java.util.List;

public class Column implements Comparable<Column> {

    private final int columnIndex;
    private final int xPos;
    private final int yPos;
    /**
     * boost(c) The boost value for column c as computed during learning - used
     * to increase the overlap value for inactive columns.
     */
    private double boost = 1.0; //TODO choose value for boost
    /**
     * overlap(c) The spatial pooler overlap of column c with a particular input
     * pattern. RANGE 0..
     */
    private double overlap;
    private ArrayList<Boolean> activeList = new ArrayList<>();
    private ArrayList<Boolean> timesGreaterOverlapThanMinOverlap = new ArrayList<>();
    /**
     * neighbors(c) A list of all the columns that are within inhibitionRadius
     * of column c.
     */
    private List<Column> neigbours;
    /**
     * potentialSynapses(c) The list of potential synapses and their permanence
     * values.
     */
    private final Synapse[] potentialSynapses;
    /**
     * activeDutyCycle(c) A sliding average representing how often column c has
     * been active after inhibition (e.g. over the last 1000 iterations).
     */
    private double activeDutyCycle;
    private double minimalLocalActivity;
    /**
     * minDutyCycle(c) A variable representing the minimum desired firing rate
     * for a cell. If a cell's firing rate falls below this value, it will be
     * boosted. This value is calculated as 1% of the maximum firing rate of its
     * neighbors.
     */
    private double minimalDutyCycle;
    /**
     * A sliding average representing how often column c has had significant
     * overlap (i.e. greater than minOverlap) with its inputs (e.g. over the
     * last 1000 iterations).
     */
    private double overlapDutyCycle;
    // for temoral pooler
    /**
     * cellsPerColumn Number of cells in each column.
     */
    public static final int CELLS_PER_COLUMN = 3;
    private static final int COLUMN_MAX_ACTIVE = 1000;
    /**
     * this object is "bigger"
     */
    public static final int COMPARE_GREATER = -1;
    /**
     * this Column is "equal"
     */
    public static final int COMPARE_EQUAL = 0;
    /**
     * this Column is "smaller" than the other
     */
    public static final int COMPARE_LESSER = 1;

    public Column(int index, int xx, int yy) {
        this(index, xx, yy, null);
    }

    public Column(int index, int x, int y, Synapse[] synapses) {
        this.columnIndex = index;
        this.xPos = x;
        this.yPos = y;
        this.potentialSynapses = synapses;
    }

    @Override
    public String toString() {
        return "column " + this.columnIndex + "," + this.xPos + "," + this.yPos;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    /**
     * boostFunction(c) Returns the boost value of a column. The boost value is
     * a scalar >= 1. If activeDutyCyle(c) is above minDutyCycle(c), the boost
     * value is 1. The boost increases linearly once the column's activeDutyCyle
     * starts falling below its minDutyCycle.
     *
     * @param minimalDesiredDutyCycle
     */
    public void calculateBoost(double minimalDesiredDutyCycle) {
        if (this.activeDutyCycle > minimalDesiredDutyCycle) {
            this.boost = 1.0;
        } else {
            this.boost += minimalDesiredDutyCycle;
        }
        // logger.log(Level.INFO, "new calculated boost=" + this.boost);
    }

    private void addGreaterThanMinimalOverlap(boolean greaterThanMinimalOverlap) {
        // logger.log(Level.INFO, "timesGreate" + timesGreaterOverlapThanMinOverlap.size());
        this.timesGreaterOverlapThanMinOverlap.add(0, greaterThanMinimalOverlap);
        if (timesGreaterOverlapThanMinOverlap.size() > COLUMN_MAX_ACTIVE) {
            timesGreaterOverlapThanMinOverlap.remove(COLUMN_MAX_ACTIVE);
        }
        updateOverlapDutyCycle();
    }

    public double getOverlapDutyCycle() {
        return overlapDutyCycle;
    }

    public double getActiveDutyCycle() {
        return activeDutyCycle;
    }

    public double getMinimalDutyCycle() {
        return minimalDutyCycle;
    }

    public void setMinimalDutyCycle(double minimalDutyCycle) {
        this.minimalDutyCycle = minimalDutyCycle;
    }

    public double getOverlap() {
        return overlap;
    }

    public void setOverlap(double d, double minimalOverlap) {
        this.overlap = d;
        addGreaterThanMinimalOverlap(d >= minimalOverlap);
    }

    public Synapse[] getPotentialSynapses() {
        return potentialSynapses.clone();
    }

    public List<Column> getNeigbours() {
        return neigbours;
    }

    public void setNeigbours(List<Column> neigbours) {
        this.neigbours = neigbours;
    }

    /**
     * connectedSynapses(c) A subset of potentialSynapses(c) where the
     * permanence value is greater than connectedPerm. These are the bottom-up
     * inputs that are currently connected to column c.
     *
     * @param connectedPermanance
     * @return
     */
    public Synapse[] getConnectedSynapses(double connectedPermanance) {
        ArrayList<Synapse> connectedSynapses = new ArrayList<>();
        for (Synapse potentialSynapse : this.potentialSynapses) {
            if (potentialSynapse.isConnected(connectedPermanance)) {
                connectedSynapses.add(potentialSynapse);
            }
        }
        return connectedSynapses.toArray(new Synapse[connectedSynapses.size()]);
    }

    public double getBoost() {
        return boost;
    }

    /**
     * increasePermanences(c, s) Increase the permanence value of every synapse
     * in column c by a scale factor s.
     *
     * @param d
     */
    public void increasePermanances(double d) {
        for (Synapse potenSynapse : potentialSynapses) {
            potenSynapse.setPermanance(potenSynapse.getPermanance() + d);
        }
    }

    /**
     * updateOverlapDutyCycle(c) Computes a moving average of how often column c
     * has overlap greater than minOverlap.
     *
     * @return
     */
    private double updateOverlapDutyCycle() {
        int totalGt = 0;
        for (boolean greater : this.timesGreaterOverlapThanMinOverlap) {
            if (greater) {
                totalGt++;
            }
        }
        this.overlapDutyCycle = (double) totalGt / timesGreaterOverlapThanMinOverlap.size();
        return overlapDutyCycle;
    }

    /**
     * updateActiveDutyCycle(c) Computes a moving average of how often column c
     * has been active after inhibition.
     *
     * @return
     */
    private double updateActiveDutyCycle() {
        int totalActive = 0;
        for (boolean act : activeList) {
            if (act) {
                totalActive++;
            }
        }
        this.activeDutyCycle = (double) totalActive / activeList.size();
        return activeDutyCycle;
    }

    public void setActive(boolean active) {
        // logger.log(Level.INFO, "activeList" + activeList.size());
        activeList.add(0, active);
        if (activeList.size() > COLUMN_MAX_ACTIVE) {
            activeList.remove(COLUMN_MAX_ACTIVE);
        }
        updateActiveDutyCycle();
    }

    public boolean isActive() {
        return this.activeList.get(0);
    }

    //TODO verify +1/0/-1 are return correctly, or -1/+1 are switched???
    @Override
    public int compareTo(Column column) {
        if (this.overlap > column.getOverlap()) {
            return COMPARE_GREATER;
        } else if (this.overlap < column.getOverlap()) {
            return COMPARE_LESSER;
        } else { // ==
            return COMPARE_EQUAL;
        }
    }

    public void setMinimalLocalActivity(double minimalLocalActivity) {
        this.minimalLocalActivity = minimalLocalActivity;

    }

    public double getMinimalLocalActivity() {
        return minimalLocalActivity;
    }
}
