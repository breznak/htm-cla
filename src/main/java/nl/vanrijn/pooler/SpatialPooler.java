/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.pooler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import nl.vanrijn.model.Column;
import nl.vanrijn.model.Synapse;
import nl.vanrijn.model.helper.InputSpace;

public class SpatialPooler {

	private List<Integer>		inhibitionRadiuses			= new ArrayList<Integer>();

	private static final int	AMMOUNT_OF_COLLUMNS			= 144;

	/**
	 * if learning is on, the spatial pooler can learn new patterns
	 */
	public static boolean		LEARNING					= true;

	/**
	 * desiredLocalActivity A parameter controlling the number of columns that will be winners after the inhibition
	 * step.
	 */

	private int					desiredLocalActivity		= 1;

	/**
	 * connectedPerm If the permanence value for a synapse is greater than this value, it is said to be connected.
	 */

	private double				connectedPermanance			= 0.7;

	/**
	 * minOverlap A minimum number of inputs that must be active for a column to be considered during the inhibition
	 * step.
	 */

	private int					minimalOverlap				= 2;

	/**
	 * permanenceDec Amount permanence values of synapses are decremented during learning.
	 */
	private double				permananceDec				= 0.05;

	/**
	 * permanenceInc Amount permanence values of synapses are incremented during learning.
	 */
	private double				permananceInc				= 0.05;

	private int					amountOfSynapses			= 60;

	/**
	 * inhibitionRadius Average connected receptive field size of the columns.
	 */

	private double				inhibitionRadius			= 5.0;

	private double				inhibitionRadiusBefore		= 0.0;

	/**
	 * columns List of all columns.
	 */
	private Column[]			columns;

	private double				connectedPermananceMarge	= 0.2;

	/**
	 * activeColumns(t) List of column indices that are winners due to bottom-up input.
	 */

	public ArrayList<Column>	activeColumns				= new ArrayList<Column>();

	Logger						logger						= Logger.getLogger(SpatialPooler.class.getName());

	/**
	 * the amount of columns over y
	 */
	private int					yyMax						= 12;

	/**
	 * the amount of columns over x
	 */
	private int					xxMax						= 12;

	private int[]				inputSpace;

	private Column[]			columsSaved;

	public Column[] getColumns() {
		return columns;
	}

	public void setColumns(Column[] columns) {
		this.columns = columns;
	}

	public ArrayList<Column> getActiveColumns() {
		return activeColumns;
	}

	public void conectSynapsesToInputSpace(int[] inputSpace) {
		this.inputSpace = inputSpace;
		// int index = 0;
		// for (int y = 0; y < yyMax; y++) {
		// for (int x = 0; x < xxMax; x++) {
		// int i = inputSpace[index];
		// System.out.print(" "+i);
		// index++;
		// }
		// }

		for (Column column : this.columns) {
			for (Synapse synapse : column.getPotentialSynapses()) {
				synapse.setSourceInput(inputSpace[synapse.getInputSpaceIndex()]);
			}
		}
	}

	public SpatialPooler(int desiredLocalActivity, double connectedPermanance, int minimalOverlap,
			double permananceDec, double permananceInc, int amountOfSynapses, double inhibitionRadius) {
		this.inhibitionRadius = inhibitionRadius;
		this.connectedPermanance = connectedPermanance;
		this.minimalOverlap = minimalOverlap;
		this.permananceDec = permananceDec;
		this.permananceInc = permananceInc;
		this.amountOfSynapses = amountOfSynapses;
		this.inhibitionRadius = inhibitionRadius;

		init();
	}

	private void saveSetup() {
		columsSaved = new Column[AMMOUNT_OF_COLLUMNS];

		for (Column column : this.columns) {
			Synapse[] synapsesSaved = new Synapse[amountOfSynapses];
			System.arraycopy(column.getPotentialSynapses(), 0, synapsesSaved, 0, column.getPotentialSynapses().length);
			columsSaved[column.getColumnIndex()] = new Column(column.getColumnIndex(), column.getxPos(), column
					.getyPos(), synapsesSaved);
		}
	}

	public void restoreSavedSetup() {
		System.out.println("restoring");
		this.columns = columsSaved;
	}

	/**
	 * Initialization Prior to receiving any inputs, the region is initialized by computing a list of initial potential
	 * synapses for each column. This consists of a random set of inputs selected from the input space. Each input is
	 * represented by a synapse and assigned a random permanence value. The random permanence values are chosen with two
	 * criteria. First, the values are chosen to be in a small range around connectedPerm (the minimum permanence value
	 * at which a synapse is considered "connected"). This enables potential synapses to become connected (or
	 * disconnected) after a small number of training iterations. Second, each column has a natural center over the
	 * input region, and the permanence values have a bias towards this center (they have higher values near the
	 * center).
	 */
	// TODo A synapse can be connected but not active. And maybe also the other way arround
	private void init() {
		// TODO the input space has to be the same size is the column space. That is not desireable .Make this better.
		// logger.log(Level.INFO, "SpatialPooler");
		columns = new Column[AMMOUNT_OF_COLLUMNS];

		Random random = new Random();
		int i = 0;

		List<Integer> synapsesToInputt = new ArrayList<Integer>();
		for (int k = 0; k < xxMax * yyMax; k++) {
			synapsesToInputt.add(k);
		}

		for (int y = 0; y < yyMax; y++) {
			for (int x = 0; x < xxMax; x++) {

				Collections.shuffle(synapsesToInputt);
				Iterator<Integer> iter = synapsesToInputt.iterator();

				Synapse[] synapses = new Synapse[amountOfSynapses];

				for (int j = 0; j < synapses.length; j++) {

					Integer inputSpaceIndex = iter.next();
					synapses[j] = new Synapse(inputSpaceIndex, inputSpaceIndex % 12, inputSpaceIndex / 12);

					// TODO 4 is not correct permananceMarge should be responsible for this value
					synapses[j].setPermanance(connectedPermanance - connectedPermananceMarge
							+ (((double) random.nextInt(4)) / 10));
					// logger.info(""+synapses[j].getPermanance());
				}
				columns[i] = new Column(i, x, y, synapses);
				i++;// next column
			}
		}
		saveSetup();
	}

	public double getInhibitionRadius() {
		return inhibitionRadius;
	}

	/**
	 * Phase 1: Overlap Given an input vector, the first phase calculates the overlap of each column with that vector.
	 * The overlap for each column is simply the number of connected synapses with active inputs, multiplied by its
	 * boost. If this value is below minOverlap, we set the overlap score to zero.
	 */
	public void computOverlap() {
		for (Column column : this.columns) {
			double overlap = 0.0;
			for (Synapse connectedSynapse : column.getConnectedSynapses(connectedPermanance)) {
				int t = 1;
				overlap += input(t, connectedSynapse.getSourceInput());
			}

			if (overlap < minimalOverlap) {
				column.setOverlap(0);
				column.addGreaterThanMinimalOverlap(false);
			} else {
				column.setOverlap(overlap * column.getBoost());

				column.addGreaterThanMinimalOverlap(true);
			}
			column.updateOverlapDutyCycle();
		}
	}

	/**
	 * Phase 2: Inhibition The second phase calculates which columns remain as winners after the inhibition step.
	 * desiredLocalActivity is a parameter that controls the number of columns that end up winning. For example, if
	 * desiredLocalActivity is 10, a column will be a winner if its overlap score is greater than the score of the 10'th
	 * highest column within its inhibition radius.
	 */
	public void computeWinningColumsAfterInhibition() {

		activeColumns = new ArrayList<Column>();
		for (Column column : this.columns) {
			if (Math.round(this.inhibitionRadius) != Math.round(this.inhibitionRadiusBefore)
					|| column.getNeigbours() == null) {
				column.setNeigbours(getNeigbors(column));
			}
			double minimalLocalActivity = kthScore(column.getNeigbours(), desiredLocalActivity);
			// TODO if inhibitionRadius changes, shouldn't this also change?
			column.setMinimalLocalActivity(minimalLocalActivity);
			if (column.getOverlap() > 0 && column.getOverlap() >= minimalLocalActivity) {
				column.setActive(true);
				activeColumns.add(column);
			} else {
				column.setActive(false);
			}
			column.updateActiveDutyCycle();
		}
	}

	/**
	 * Phase 3: Learning The third phase performs learning; it updates the permanence values of all synapses as
	 * necessary, as well as the boost and inhibition radius. The main learning rule is implemented in lines 20-26. For
	 * winning columns, if a synapse is active, its permanence value is incremented, otherwise it is decremented.
	 * Permanence values are constrained to be between 0 and 1. Lines 28-36 implement boosting. There are two separate
	 * boosting mechanisms in place to help a column learn connections. If a column does not win often enough (as
	 * measured by activeDutyCycle), its overall boost value is increased (line 30-32). Alternatively, if a column's
	 * connected synapses do not overlap well with any inputs often enough (as measured by overlapDutyCycle), its
	 * permanence values are boosted (line 34-36). Note: once learning is turned off, boost(c) is frozen. Finally, at
	 * the end of Phase 3 the inhibition radius is recomputed (line 38).
	 */
	public void updateSynapses() {
		if (LEARNING) {
			for (Column activeColumn : activeColumns) {
				for (Synapse potentialSynapse : activeColumn.getPotentialSynapses()) {
					double permanance = potentialSynapse.getPermanance();
					// See page 29 point 6) For each....vice-versa.
					if (potentialSynapse.getSourceInput() == 1) {

						potentialSynapse.setPermanance(permanance + permananceInc);
						potentialSynapse.setPermanance(Math.min(potentialSynapse.getPermanance(), 1.0));

					} else {
						potentialSynapse.setPermanance(permanance - permananceDec);
						potentialSynapse.setPermanance(Math.max(potentialSynapse.getPermanance(), 0.0));
					}
				}

			}
			for (Column column : this.columns) {
				double minimalDutyCycle = (0.01 * (getMaxDutyCycle(column.getNeigbours())));
				column.setMinimalDutyCycle(minimalDutyCycle);
				column.calculateBoost(minimalDutyCycle);

				double overlapDutyCycle = column.updateOverlapDutyCycle();

				if (overlapDutyCycle < minimalDutyCycle) {
					column.increasePermanances(0.1 * connectedPermanance);
				}
				for (Synapse synapse : column.getConnectedSynapses(connectedPermanance)) {

					this.inhibitionRadiuses.add(Math.max(Math.abs(column.getxPos() - synapse.getxPos()), Math
							.abs(column.getyPos() - synapse.getyPos())));
				}
			}

			// for performance I also save inhibitionRadius of the time step before.Neighbors don't need to be
			// calculated if
			// inhib didn't change
			this.inhibitionRadiusBefore = inhibitionRadius;
			this.inhibitionRadius = averageReceptiveFieldSize();

		}
	}

	/**
	 * averageReceptiveFieldSize() The radius of the average connected receptive field size of all the columns. The
	 * connected receptive field size of a column includes only the connected synapses (those with permanence values >=
	 * connectedPerm). This is used to determine the extent of lateral inhibition between columns.
	 * 
	 * @return
	 */

	private double averageReceptiveFieldSize() {
		double averageReceptiveFieldSize = 0;

		for (Integer integer : inhibitionRadiuses) {
			averageReceptiveFieldSize += integer;
		}
		averageReceptiveFieldSize = averageReceptiveFieldSize / inhibitionRadiuses.size();
		inhibitionRadiuses = new ArrayList<Integer>();
		return averageReceptiveFieldSize;
	}

	public double getConnectedPermanance() {
		return connectedPermanance;
	}

	private int input(int t, int sourceInput) {

		return sourceInput;
	}

	/**
	 * maxDutyCycle(cols) Returns the maximum active duty cycle of the columns in the given list of columns.
	 * 
	 * @param neighbors
	 * @return
	 */
	private double getMaxDutyCycle(List<Column> neighbors) {

		Column highestNeighbor = null;
		if (neighbors.size() > 0) {
			highestNeighbor = neighbors.get(0);
		}
		for (Column neighbor : neighbors) {
			if (neighbor.getActiveDutyCycle() > highestNeighbor.getActiveDutyCycle()) {
				highestNeighbor = neighbor;
			}
		}
		return highestNeighbor.getActiveDutyCycle();
	}

	/**
	 * kthScore(cols, k) Given the list of columns, return the k'th highest overlap value.
	 * 
	 * @param neighbors
	 * @param disiredLocalActivity
	 * @return
	 */
	private double kthScore(List<Column> neighbors, int disiredLocalActivity) {

		if (disiredLocalActivity > neighbors.size()) {
			disiredLocalActivity = neighbors.size();
		}

		Collections.sort(neighbors);
		double ktScore = neighbors.get(disiredLocalActivity - 1).getOverlap();

		return ktScore;
	}

	private List<Column> getNeigbors(Column column) {

		List<Column> neighbors = new ArrayList<Column>();
		int inhib = (int) Math.round(inhibitionRadius);
		int xxStart = Math.max(0, column.getxPos() - inhib);
		int xxEnd = Math.min(xxMax, column.getxPos() + inhib + 1);
		int yyStart = Math.max(0, column.getyPos() - inhib);
		int yyEnd = Math.min(yyMax, column.getyPos() + inhib + 1);

		for (int y = yyStart; y < yyEnd; y++) {
			for (int x = xxStart; x < xxEnd; x++) {
				if (!(y == column.getyPos() && x == column.getxPos())) {
					neighbors.add(this.columns[y * xxMax + x]);
				}
			}
		}

		column.setNeigbours(neighbors);
		return neighbors;
	}

	public double reconstructionQuality() {
		int ammountOk = 0;
		int ammountWrong = 0;
		Set<InputSpace> inputSpaces = new TreeSet<InputSpace>();

		for (Column activeColumn : activeColumns) {
			for (Synapse connectedSynapse : activeColumn.getConnectedSynapses(this.connectedPermanance)) {
				inputSpaces.add(new InputSpace(connectedSynapse.getxPos(), connectedSynapse.getyPos(), connectedSynapse
						.getSourceInput()));
			}
		}
		// for(InputSpace inputSpace : inputSpaces){
		// System.out.println(inputSpace);
		// }
		// System.out.println("aantal aktief e " + inputSpaces.size());
		int ammountAcive = 0;
		int index = 0;
		for (int y = 0; y < yyMax; y++) {
			for (int x = 0; x < xxMax; x++) {
				int i = inputSpace[index];
				if (i == 1) {

					ammountAcive++;
				}

				if (i == 1 && inputSpaces.contains(new InputSpace(x, y, 1))) {
					ammountOk++;

				} else
					if (i == 0 && inputSpaces.contains(new InputSpace(x, y, 0))) {
						ammountWrong++;

					} else
						if (i == 1 && !inputSpaces.contains(new InputSpace(x, y, 1))) {
							ammountWrong++;
						}
				index++;
			}

		}
		// System.out.println();
		// System.out.println(ammountAcive + " ammountAcive");
		// System.out.println(ammountOk + " ok");
		// System.out.println(ammountWrong + " wrong");
		if (ammountOk != 0) {
			return ((double) ammountOk / (double) (ammountAcive) * 100);
		}
		return 0;

	}

	public void setInputSpace(int[] inputSpace) {
		this.inputSpace = inputSpace;
	}
}
