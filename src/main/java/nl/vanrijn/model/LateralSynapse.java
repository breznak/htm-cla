/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import nl.vanrijn.pooler.TemporalPooler;

public class LateralSynapse {	

	@Override
	public String toString() {

		return "LateralSynapse from " + this.fromColumnIndex + "," + this.fromCellIndex + ", on " + this.columnIndex
				+ "," + this.getCellIndex() + "," + this.segmentIndex + ",perm " + this.permanance;
	}

	private final int	fromColumnIndex;

	private final int	fromCellIndex;

	public LateralSynapse(int c, int i, int s, int fromColumnIndex, int fromCellIndex, double initialPerm) {
		this.columnIndex = c;
		this.cellIndex = i;
		this.segmentIndex = s;
		this.fromColumnIndex = fromColumnIndex;
		this.fromCellIndex = fromCellIndex;
		this.permanance = initialPerm;
	}

	public int getFromColumnIndex() {
		return fromColumnIndex;
	}

	public int getFromCellIndex() {
		return fromCellIndex;
	}


	private  double	permanance;

	public void setPermanance(double permanance) {
		this.permanance = permanance;
	}

	private final int		columnIndex;

	private final int		cellIndex;

	private final int		segmentIndex;

	public int getSegmentIndex() {
		return segmentIndex;
	}

	public double getPermanance() {
		return permanance;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public int getCellIndex() {
		return cellIndex;
	}

	public boolean isConnected() {
		// logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+
		// "input="+sourceInput);
		return this.permanance >= TemporalPooler.CONNECTED_PERMANANCE;
	}

}
