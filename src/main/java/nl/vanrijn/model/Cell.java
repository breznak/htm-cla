/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nl.vanrijn.model.helper.SegmentUpdate;
import static org.junit.Assert.*;

/**
 * Cell is a representation of neuron, like in (feed-forward) neural nets, it's
 * similar to spiking NNs' neurons
 *
 * @author marek
 */
public class Cell {
    // 3 output states:

    /**
     * active from feed-forward input
     *
     * activeState(c, i, t) A boolean vector with one number per cell. It
     * represents the active state of the column c cell i at time t given the
     * current feed-forward input and the past temporal context. activeState(c,
     * i, t) is the contribution from column c cell i at time t. If 1, the cell
     * has current feed-forward input as well as an appropriate temporal
     * context.
     */
    public static final int ACTIVE = 1;
    /**
     * active from lateral input = prediction
     *
     * predictiveState(c, i, t) A boolean vector with one number per cell. It
     * represents the prediction of the column c cell i at time t, given the
     * bottom-up activity of other columns and the past temporal context.
     * predictiveState(c, i, t) is the contribution of column c cell i at time
     * t. If 1, the cell is predicting feed-forward input in the current
     * temporal context.
     */
    public static final int PREDICT = 2;
    /**
     * inactive, inhibited
     */
    public static final int INACTIVE = 0;
    /**
     * Cell's output state: can have 3 values: Cell.ACTIVE/PREDICT/INACTIVE
     *
     * use setState/getState to query
     */
    private int output = Cell.INACTIVE;
    //
    public static final int NOW = 1;
    public static final int BEFORE = 0;
    /**
     * segmentUpdateList A list of segmentUpdate structures.
     * segmentUpdateList(c,i) is the list of changes for cell i in column c.
     */
    private List<SegmentUpdate> segmentUpdateList = new ArrayList<>();
    private final int columnIndex;
    private final int cellIndex;
    private int time;
    /**
     * learnState(c, i, t) A boolean indicating whether cell i in column c is
     * chosen as the cell to learn on.
     */
    private boolean learnState;
    private final List<Segment> segments;
    private final int xpos;
    private final int ypos;
    private List<Cell> neighbors = null;

    public Cell(int columnIndex, int cellIndex, int time, int xx, int yy, List<Segment> segments) {
        assertEquals(segments != null, true); // not null
        this.columnIndex = columnIndex;
        this.cellIndex = cellIndex;
        this.time = time;
        this.ypos = yy;
        this.xpos = xx;
        this.segments = segments;
    }

    public void setTime(int time) {
        this.time = time;
    }

    /**
     * query cell's output state: {active,predict,inactive}
     *
     * @return 0/1/2 only!
     */
    public int output() {
        return this.output;
    }

    /**
     * set cell's state. can be: ACTIVE/INACTIVE/PREDICT
     *
     * @param state
     */
    public void setOutput(int state) {
        assertEquals(state == Cell.ACTIVE || state == Cell.INACTIVE || state == Cell.PREDICT, true);
        this.output = state;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

//TODO what is learnState??
    public void setLearnState(boolean learnState) {
        this.learnState = learnState;
    }

    public boolean getLearnState() {
        return this.learnState;
    }

    public List<SegmentUpdate> getSegmentUpdateList() {
        return Collections.unmodifiableList(segmentUpdateList);
    }

    public void setSegmentUpdateList(List<SegmentUpdate> segmentUpdateList) {
        this.segmentUpdateList = segmentUpdateList;
    }

    @Override
    public String toString() {
        return "cell=" + this.columnIndex + "," + this.cellIndex + "," + this.time + ",activeState="
                + this.activeState + ",learnState=" + this.learnState + ",predictivestate=" + this.predictiveState
                + ",segments.size=" + this.segments.size() + "x,y=[" + this.xpos + "," + this.ypos + "], up= "
                + this.segmentUpdateList.size();
    }

    public int getXpos() {
        return xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public void setNeigbors(List<Cell> neighbors) {
        this.neighbors = neighbors;
    }

    public List<Cell> getNeighbors() {
        return Collections.unmodifiableList(this.neighbors);
    }
}
