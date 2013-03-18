/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import java.util.List;

public class Column implements Comparable<Column> {

	public Column(int index, int xx, int yy) {
		this.columnIndex = index;
		this.xPos = xx;
		this.yPos = yy;
		this.potentialSynapses=null;
	}

	public Column(int i, int x, int y, Synapse[] synapses) {

		this.columnIndex = i;
		this.xPos = x;
		this.yPos = y;
		this.potentialSynapses = synapses;
	}

	@Override
	public String toString() {

		return "column " + this.getColumnIndex() + "," + this.getxPos() + ","
				+ this.getyPos();
	}

	private final int columnIndex;

	public int getColumnIndex() {
		return columnIndex;
	}

	private final int xPos;

	private final int yPos;

	/**
	 * boost(c) The boost value for column c as computed during learning - used
	 * to increase the overlap value for inactive columns.
	 */
	private double boost = 1.0;
	// choose value for boost

	/**
	 * overlap(c) The spatial pooler overlap of column c with a particular input
	 * pattern.
	 */

	private double overlap;

	private boolean active;

	private ArrayList<Boolean> activeList = new ArrayList<Boolean>();

	public void setActiveList(ArrayList<Boolean> activeList) {
		this.activeList = activeList;
	}

	public void setTimesGreaterOverlapThanMinOverlap(
			ArrayList<Boolean> timesGreaterOverlapThanMinOverlap) {
		this.timesGreaterOverlapThanMinOverlap = timesGreaterOverlapThanMinOverlap;
	}

	

	public void setOverlapDutyCycle(double overlapDutyCycle) {
		this.overlapDutyCycle = overlapDutyCycle;
	}

	private ArrayList<Boolean> timesGreaterOverlapThanMinOverlap = new ArrayList<Boolean>();

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
	public static int CELLS_PER_COLUMN = 3;

	public static int getCELLS_PER_COLUMN() {
		return CELLS_PER_COLUMN;
	}

	public static void setCELLS_PER_COLUMN(int cELLSPERCOLUMN) {
		CELLS_PER_COLUMN = cELLSPERCOLUMN;
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

		if (this.getActiveDutyCycle() > minimalDesiredDutyCycle) {
			this.boost = 1.0;
		} else {
			this.boost += minimalDesiredDutyCycle;
		}
		// logger.log(Level.INFO, "new calculated boost=" + this.boost);
	}

	public void addGreaterThanMinimalOverlap(boolean greaterThanMinimalOverlap) {

		// logger.log(Level.INFO, "timesGreate"
		// + timesGreaterOverlapThanMinOverlap.size());
		this.timesGreaterOverlapThanMinOverlap
				.add(0, greaterThanMinimalOverlap);
		if (timesGreaterOverlapThanMinOverlap.size() > 1000) {
			timesGreaterOverlapThanMinOverlap.remove(1000);
		}
	}

	public ArrayList<Boolean> getTimesGreaterOverlapThanMinOverlap() {
		return timesGreaterOverlapThanMinOverlap;
	}

	public void addActive(boolean active) {
		// logger.log(Level.INFO, "activeList" + activeList.size());
		activeList.add(0, active);
		if (activeList.size() > 1000) {
			activeList.remove(1000);
		}
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

	public void setOverlap(double d) {
		this.overlap = d;
	}

	public Synapse[] getPotentialSynapses() {
		return potentialSynapses;
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
		ArrayList<Synapse> connectedSynapses = new ArrayList<Synapse>();
		for (Synapse potentialSynapse : this.potentialSynapses) {
			if (potentialSynapse.isConnected(connectedPermanance)) {
				connectedSynapses.add(potentialSynapse);
			}
		}
		Object[] objects = connectedSynapses.toArray();
		Synapse[] synapses = new Synapse[objects.length];
		System.arraycopy(objects, 0, synapses, 0, objects.length);
		connectedSynapses = null;
		return synapses;
	}

	public double getBoost() {
		return boost;
	}

	public void setBoost(double boost) {
		this.boost = boost;
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
	public double updateOverlapDutyCycle() {

		int totalGt = 0;
		for (boolean greater : this.timesGreaterOverlapThanMinOverlap) {
			if (greater) {
				totalGt++;
			}
		}
		this.overlapDutyCycle = (double) totalGt
				/ timesGreaterOverlapThanMinOverlap.size();

		return overlapDutyCycle;

	}

	/**
	 * updateActiveDutyCycle(c) Computes a moving average of how often column c
	 * has been active after inhibition.
	 * 
	 * @return
	 */
	public double updateActiveDutyCycle() {

		int totalActive = 0;
		for (boolean active : activeList) {
			if (active) {
				totalActive++;
			}
		}
		this.activeDutyCycle = (double) totalActive / activeList.size();

		return activeDutyCycle;
	}

	public ArrayList<Boolean> getActiveList() {
		return activeList;
	}

	public void setActive(ArrayList<Boolean> activeList) {
		this.activeList = activeList;
	}

	public void setActive(boolean active) {
		addActive(active);
		this.active = active;

	}

	public boolean isActive() {
		return this.active;
	}

	public int compareTo(Column column) {
		int returnValue = 0;
		if (this.getOverlap() > column.getOverlap()) {
			returnValue = -1;
		} else {
			if (this.getOverlap() == column.getOverlap()) {
				returnValue = 0;
			} else {
				if (this.getOverlap() < column.getOverlap()) {
					returnValue = 1;
				}
			}
		}
		return returnValue;
	}

	public void setMinimalLocalActivity(double minimalLocalActivity) {
		this.minimalLocalActivity = minimalLocalActivity;

	}

	public double getMinimalLocalActivity() {
		return minimalLocalActivity;
	}

}
