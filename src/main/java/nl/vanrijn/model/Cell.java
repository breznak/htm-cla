/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nl.vanrijn.model.helper.SegmentUpdate;
import nl.vanrijn.utils.CircularList;
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
    // time
    /**
     * cell will keep a buffer of its last TIME_STEPS output values
     */
    private static int TIME_STEPS = 2;
    /**
     * Cell's output state: can have 3 values: Cell.ACTIVE/PREDICT/INACTIVE
     *
     * use setState/getState to query
     */
    private List<Integer> output = new CircularList<>(TIME_STEPS);
    /**
     * learnState(c, i, t) A boolean indicating whether cell i in column c is
     * chosen as the cell to learn on.
     */
    private List<Boolean> learnState = new CircularList<>(TIME_STEPS);
    public static final int BEFORE = 1;
    public static final int NOW = 0;
    /**
     * segmentUpdateList A list of segmentUpdate structures.
     * segmentUpdateList(c,i) is the list of changes for cell i in column c.
     */
    private List<SegmentUpdate> segmentUpdateList = new ArrayList<>();
    /**
     * total number of cells. increasing with each new instance Cell()
     */
    private static int cellCounter = 0;
    /**
     * unique ID of cell, use getName()
     */
    private int uniqName;
    private final List<DendriteSegment> segments;
    private final int xpos;
    private final int ypos;
    private List<Cell> neighbors = null;
    private final Column partOfColumn;

    public Cell(Column belongsToColumn, int xx, int yy, List<DendriteSegment> segments) {
        assertEquals(segments != null, true); // not null
        this.partOfColumn = belongsToColumn;
        this.uniqName = Cell.cellCounter; //unique name
        Cell.cellCounter++;
        this.ypos = yy;
        this.xpos = xx;
        this.segments = segments;
    }

    /**
     * query cell's output state: {active,predict,inactive} at given time
     *
     * when requested time exceeds cell's history buffer, oldest is returned.
     *
     * @param time - when was this state. 0==Cell.NOW==actual, 1=BEFORE, ..upto
     * TIME_STEPS
     *
     * @return 0/1/2 only!
     */
    public int output(int time) {
        if (time >= TIME_STEPS) {
            System.err.println("! i dont remember so much, asshole");
            time = TIME_STEPS - 1;
        }
        return this.output.get(time);
    }

    /**
     * set cell's state. can be: ACTIVE/INACTIVE/PREDICT
     *
     * @param state
     */
    public void setOutput(int state) {
        assertEquals(state == Cell.ACTIVE || state == Cell.INACTIVE || state == Cell.PREDICT, true);
        this.output.add(0, state);
    }

    public Column getColumn() {
        return this.partOfColumn;
    }

    public int getName() {
        return uniqName;
    }

    public List<DendriteSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

//TODO what is learnState??
    public void setLearnState(boolean learnState) {
        this.learnState.add(NOW, learnState);
    }

    public boolean getLearnState(int time) {
        return this.learnState.get(time);
    }

    public List<SegmentUpdate> getSegmentUpdateList() {
        return segmentUpdateList;
    }

    public void setSegmentUpdateList(List<SegmentUpdate> segmentUpdateList) {
        this.segmentUpdateList = segmentUpdateList;
    }

    @Override
    public String toString() {
        return "cell=" + this.partOfColumn + "," + this.uniqName + ", State=" + this.output
                + ",learnState=" + this.learnState + ",segments.size=" + this.segments.size() + "x,y=[" + this.xpos + "," + this.ypos + "], up= "
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
