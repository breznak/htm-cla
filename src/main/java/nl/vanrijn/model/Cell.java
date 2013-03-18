/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import java.util.List;

import nl.vanrijn.model.helper.SegmentUpdate;

public class Cell {

	public static final int	ACTIVE_STATE	= 1;

	public static final int	LEARN_STATE		= 2;

	public static final int	NOW				= 1;

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public static final int		BEFORE				= 0;

	/**
	 * segmentUpdateList A list of segmentUpdate structures. segmentUpdateList(c,i) is the list of changes for cell i in
	 * column c.
	 */
	private List<SegmentUpdate>	segmentUpdateList	= new ArrayList<SegmentUpdate>();

	private final int					columnIndex;

	private final int					cellIndex;

	private int					time;

	/**
	 * predictiveState(c, i, t) A boolean vector with one number per cell. It represents the prediction of the column c
	 * cell i at time t, given the bottom-up activity of other columns and the past temporal context. predictiveState(c,
	 * i, t) is the contribution of column c cell i at time t. If 1, the cell is predicting feed-forward input in the
	 * current temporal context.
	 */

	private boolean				predictiveState;

	/**
	 * learnState(c, i, t) A boolean indicating whether cell i in column c is chosen as the cell to learn on.
	 */

	private boolean				learnState;

	/**
	 * activeState(c, i, t) A boolean vector with one number per cell. It represents the active state of the column c
	 * cell i at time t given the current feed-forward input and the past temporal context. activeState(c, i, t) is the
	 * contribution from column c cell i at time t. If 1, the cell has current feed-forward input as well as an
	 * appropriate temporal context.
	 */
	private boolean				activeState;

	public boolean hasPredictiveState() {
		return predictiveState;
	}

	public void setPredictiveState(boolean predictiveState) {
		this.predictiveState = predictiveState;
	}

	private final List<Segment>	segments;

	private final int				xpos;

	private final int				ypos;

	private List<Cell> neighbors=null;

	public boolean hasActiveState() {
		return activeState;
	}

	public Cell(int columnIndex, int cellIndex, int time, int xx, int yy, List<Segment> segments) {
		this.columnIndex = columnIndex;
		this.cellIndex = cellIndex;
		this.time = time;
		this.ypos=yy;
		this.xpos=xx;
		this.segments=segments;
	}

	public int getColumnIndex() {
		return columnIndex;
	}	

	public int getCellIndex() {
		return cellIndex;
	}	

	public List<Segment> getSegments() {
		return segments;
	}

	public void setLearnState(boolean learnState) {
		this.learnState = learnState;

	}

	public boolean hasLearnState() {
		return this.learnState;
	}

	public List<SegmentUpdate> getSegmentUpdateList() {
		return segmentUpdateList;
	}

	public void setSegmentUpdateList(List<SegmentUpdate> segmentUpdateList) {
		this.segmentUpdateList = segmentUpdateList;
	}

	public void setActiveState(boolean activeState) {
		this.activeState = activeState;

	}	

	@Override
	public String toString() {
		return "cell=" + this.columnIndex + "," + this.getCellIndex() + "," + this.time + ",activeState="
				+ this.activeState + ",learnState=" + this.learnState + ",predictivestate=" + this.predictiveState
				+ ",segments.size=" + this.segments.size() + "x,y=" + this.getXpos() + "," + this.getYpos() + " "
				+ this.segmentUpdateList.size();
	}	

	public int getXpos() {
		return xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setNeigbors(List<Cell> neighbors) {
		this.neighbors=neighbors;
		
	}

	public List<Cell> getNeighbors() {
		return this.neighbors;
	}
}