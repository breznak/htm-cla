/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.pooler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import nl.vanrijn.model.Cell;
import nl.vanrijn.model.Column;
import nl.vanrijn.model.LateralSynapse;
import nl.vanrijn.model.Segment;
import nl.vanrijn.model.helper.SegmentUpdate;

public class TemporalPooler {

	/**
	 * this boolean should be switched of if no learning is desirable anymore.
	 */
	private final boolean LEARNING=true;
	/**
	 * permanenceInc Amount permanence values of synapses are incremented when
	 * activity-based learning occurs.
	 */
	public static final double PERMANANCE_INC = 0.1; // TODO

	// choose
	// reasonable

	// value for
	// PERMANANCE_INC

	/**
	 * permanenceDec Amount permanence values of synapses are decremented when
	 * activity-based learning occurs.
	 */
	public static final double PERMANANCE_DEC = 0.1; // TODO

	// choose
	// reasonable

	// value for
	// PERMANANCE_DEC

	/**
	 * initialPerm Initial permanence value for a synapse.
	 */
	public static final double INITIAL_PERM = 0.4;

	/**
	 * connectedPerm If the permanence value for a synapse is greater than this
	 * value, it is said to be connected.
	 */
	public static double CONNECTED_PERMANANCE = 0.5;

	/**
	 * minThreshold Minimum segment activity for learning.
	 */
	private static final int MIN_TRESHOLD = 1;

	// TODO value for min treshold

	// TODO choose value for min treshold
	/**
	 * activationThreshold Activation threshold for a segment. If the number of
	 * active connected synapses in a segment is greater than
	 * activationThreshold, the segment is said to be active.
	 */
	private static int ACTIVATION_TRESHOLD = 1;

	// TODO value for activasion treshold

	private static int AMMOUNT_TIME = 2;

	public static int AMMOUNT_OF_SEGMENTS = 10;

	// TODO value for ammount segments

	// TODO choose value maybe first same ammount as cells
	private static int AMMOUNT_OF_SYNAPSES = 30;

	// TODO choose value for ammount of synapse

	/**
	 * newSynapseCount The maximum number of synapses added to a segment during
	 * learning.
	 */
	private static int NEW_SYNAPSE_COUNT = 5;

	// TODO for new synapse count

	// TODO choose value for learning radius

	/**
	 * learningRadius The area around a temporal pooler cell from which it can
	 * get lateral connections.
	 */
	// TODO implement learning radius implementation
	private static int LEARNING_RADIUS =2;

	/**
	 * activeColumns Array of columns that are winners due to bottom-up
	 * input (this is the output of the spatial pooler).
	 */
	private Column[] activeColumns;

	/**
	 * cell[c][i][t] An array of all cells, indexed by c(column index) and i(cell index) and  t(time).
	 */
	private Cell[][][] cells;

	private int xxMax;

	private int yyMax;

	public TemporalPooler(int xxMax, int yyMax) {
		this.xxMax = xxMax;
		this.yyMax = yyMax;
	}

	public void init() {
		cells = new Cell[xxMax * yyMax][Column.CELLS_PER_COLUMN][TemporalPooler.AMMOUNT_TIME];

		ArrayList<Integer> collumnIndexes = new ArrayList<Integer>();
		for (int c = 0; c < xxMax * yyMax; c++) {
			collumnIndexes.add(c);

		}
		Random random = new Random();
		int c = 0;
		for (int yy = 0; yy < yyMax; yy++) {
			for (int xx = 0; xx < xxMax; xx++) {
				for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
					for (int t = 0; t < TemporalPooler.AMMOUNT_TIME; t++) {
						List<Segment> segments = new ArrayList<Segment>();
						for (int s = 0; s < AMMOUNT_OF_SEGMENTS; s++) {
							List<LateralSynapse> synapses = new ArrayList<LateralSynapse>();
							Collections.shuffle(collumnIndexes);
							for (int y = 0; y < AMMOUNT_OF_SYNAPSES; y++) {
								// TODO can a cell predict itself?

								synapses.add(new LateralSynapse(c, i, s,
										collumnIndexes.get(y), random
												.nextInt(3),
										TemporalPooler.INITIAL_PERM));
							}
							segments.add(new Segment(c, i, s, synapses));
							// System.out.println(c);
						}
						cells[c][i][t] = new Cell(c, i, t, xx, yy, segments);						
					}
				}
				c++;
			}
		}
	}

	/**
	 * This method returns the cells that this cell can learn from These cells
	 * have to be in Learninradius of this cell
	 * 
	 * @param cell
	 * @return
	 */
	private List<Cell> getNeighbors(Cell cell) {
		
		List<Cell> neighbors = new ArrayList<Cell>();
		int xxStart=Math.max(0, cell.getXpos()-LEARNING_RADIUS);
		int xxEnd=Math.min(xxMax, cell.getXpos()+LEARNING_RADIUS+1);
		int yyStart=Math.max(0, cell.getYpos()-LEARNING_RADIUS);
		int yyEnd=Math.min(yyMax, cell.getYpos()+LEARNING_RADIUS+1);
		int c;
		for (int yy = yyStart; yy < yyEnd; yy++) {
			for (int xx = xxStart; xx < xxEnd; xx++) {
				c=yy*xxMax+xx;
				for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
					Cell potentialNeigbor = cells[c][i][Cell.NOW];

					neighbors.add(potentialNeigbor);
				
				}
			}
		}
		return neighbors;
	}

	public Cell[][][] getCells() {
		return cells;
	}

	public void setCells(Cell[][][] cells) {
		this.cells = cells;
	}

	/**
	 * Phase 1 The first phase calculates the activeState for each cell that is
	 * in a winning column. For those columns, the code further selects one cell
	 * per column as the learning cell (learnState). The logic is as follows: if
	 * the bottom-up input was predicted by any cell (i.e. its predictiveState
	 * output was 1 due to a sequence segment), then those cells become active
	 * (lines 23-27). If that segment became active from cells chosen with
	 * learnState on, this cell is selected as the learning cell (lines 28-30).
	 * If the bottom-up input was not predicted, then all cells in the column
	 * become active (lines 32-34). In addition, the best matching cell is
	 * chosen as the learning cell (lines 36-41) and a new segment is added to
	 * that cell.
	 */
	public void computeActiveState() {
		for (int c = 0; c < activeColumns.length; c++) {
			Column column = activeColumns[c];
			boolean buPredicted = false;
			boolean lcChosen = false;//used for learning
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
				if (cells[column.getColumnIndex()][i][Cell.BEFORE]
						.hasPredictiveState()) {
					// get the segment that became active in the time step
					// before.(That made this cell active).
					// So the synapses that made this segment active where also
					// from one time step before. and the cells
					// connected to these synapses also.
					Segment segment = getActiveSegment(column.getColumnIndex(),
							i, Cell.BEFORE, Cell.ACTIVE_STATE);
					if (segment != null && segment.isSsequenceSegment()) {
						buPredicted = true;
						cells[column.getColumnIndex()][i][Cell.NOW]
								.setActiveState(true);
						// if these cells also had learnstate
						if (segmentActive(segment, Cell.BEFORE,
								Cell.LEARN_STATE)&& LEARNING) {

							lcChosen = true;
							cells[column.getColumnIndex()][i][Cell.NOW]
									.setLearnState(true);
						}
					}
				}
			}
			if (!buPredicted) {
				for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
					cells[column.getColumnIndex()][i][Cell.NOW]
							.setActiveState(true);
				}
			}
			if (!lcChosen && LEARNING) {
				Cell cell = getBestMatchingCell(column.getColumnIndex(),
						Cell.BEFORE);

				// TODO Maybe now a new segment should be created in stead of
				// getting the best matching segment
				Segment segment = getBestMatchingSegment(column
						.getColumnIndex(), cell.getCellIndex(), Cell.BEFORE);
				SegmentUpdate sUpdate = getSegmentActiveSynapses(column
						.getColumnIndex(), cell.getCellIndex(), segment,
						Cell.BEFORE, Segment.GETS_NEW_SYNAPSE);
				sUpdate.setSequenceSegment(true);

				Cell cellToUpdate = cells[cell.getColumnIndex()][cell
						.getCellIndex()][Cell.NOW];
				cellToUpdate.setLearnState(true);

				cellToUpdate.getSegmentUpdateList().add(sUpdate);
			}
		}
	}

	/**
	 * Phase 2 The second phase calculates the predictive state for each cell. A
	 * cell will turn on its predictive state output if one of its segments
	 * becomes active, i.e. if enough of its lateral inputs are currently active
	 * due to feed-forward input. In this case, the cell queues up the following
	 * changes: a) reinforcement of the currently active segment (lines 47-48),
	 * and b) reinforcement of a segment that could have predicted this
	 * activation, i.e. a segment that has a (potentially weak) match to
	 * activity during the previous time step (lines 50-53).
	 */
	public void calculatePredictedState() {
		for (int c = 0; c < xxMax * yyMax; c++) {
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				Cell cell = cells[c][i][Cell.NOW];

				for (int s = 0; s < cell.getSegments().size(); s++) {
					Segment segment = cell.getSegments().get(s);
					// is this segment active from cells that are active now?(In
					// phase 1)
					if (segmentActive(segment, Cell.NOW, Cell.ACTIVE_STATE)) {

						cell.setPredictiveState(true);
						if(LEARNING){
							SegmentUpdate activeUpdate = getSegmentActiveSynapses(
									c, i, segment, Cell.NOW,
									Segment.GETS_NO_NEW_SYNAPSE);
							cell.getSegmentUpdateList().add(activeUpdate);
							// TODO This should not happen so often. Only once for
							// an active cell. because it will always be the same segment
							Segment predSegment = getBestMatchingSegment(c, i,
									Cell.BEFORE);
	
							SegmentUpdate predUpdate = getSegmentActiveSynapses(c,
									i, predSegment, Cell.BEFORE,
									Segment.GETS_NEW_SYNAPSE);
							cell.getSegmentUpdateList().add(predUpdate);
						}
					}
				}
			}
		}
	}

	/**
	 * Phase 3 The third and last phase actually carries out learning. In this
	 * phase segment updates that have been queued up are actually implemented
	 * once we get feedforward input and the cell is chosen as a learning cell
	 * (lines 56-57). Otherwise, if the cell ever stops predicting for any
	 * reason, we negatively reinforce the segments (lines 58-60).
	 */
	public void updateSynapses() {
		if(LEARNING){
			for (int c = 0; c < xxMax * yyMax; c++) {
				for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
	
					Cell cell = cells[c][i][Cell.NOW];
					if (cell.hasLearnState()) {
						adaptSegments(cell.getSegmentUpdateList(),
								SegmentUpdate.POSITIVE_REINFORCEMENT);
						cell.getSegmentUpdateList().clear();
	
					} else
					// TODO I have the feeling that this is wrong. It should be:if
					// the cell was predicted but is not
					// active now. (or maybe not)
					if (!cells[c][i][Cell.NOW].hasPredictiveState()
							&& cells[c][i][Cell.BEFORE].hasPredictiveState()) {
						adaptSegments(cell.getSegmentUpdateList(),
								SegmentUpdate.NO_POSITIVE_REINFORCEMENT);
						cell.getSegmentUpdateList().clear();
	
					}
				}
			}
		}
	}

	/**
	 * getActiveSegment(c, i, t, state) For the given column c cell i, return a
	 * segment index such that segmentActive(s,t, state) is true. If multiple
	 * segments are active, sequence segments are given preference. Otherwise,
	 * segments with most activity are given preference.
	 * 
	 * @param c
	 * @param i
	 * @param time
	 * @param state
	 * @return
	 */
	// this should only return a segment index. not a segment. the time of the
	// segment doen't mather!!
	public Segment getActiveSegment(int c, int i, final int time, int state) {

		Segment returnValue = null;
		Cell cell = cells[c][i][time];
		List<Segment> activeSegments = new ArrayList<Segment>();

		for (Segment segment : cell.getSegments()) {
			if (segmentActive(segment, time, state)) {
				activeSegments.add(segment);
			}
		}
		if (activeSegments.size() == 1) {
			returnValue = activeSegments.get(0);
		} else {
			Collections.sort(activeSegments, new Comparator<Segment>() {

				public int compare(Segment segment, Segment segmentToCompare) {
					// 1 sequence most activity
					// 2 sequence and active
					// 3 most activity
					// 4 least activity
					int returnValue = 0;
					//
					int ammountActiveCells = 0;
					int ammountActiveCellsToCompare = 0;
					for (LateralSynapse synapse : segment.getSynapses()) {
						if (cells[synapse.getFromColumnIndex()][synapse
								.getFromCellIndex()][time].hasActiveState()) {
							ammountActiveCells++;
						}
					}
					segment.setAmmountActiveCells(ammountActiveCells);
					for (LateralSynapse synapse : segmentToCompare
							.getSynapses()) {
						if (cells[synapse.getFromColumnIndex()][synapse
								.getFromCellIndex()][time].hasActiveState()) {
							ammountActiveCellsToCompare++;
						}
					}
					segmentToCompare
							.setAmmountActiveCells(ammountActiveCellsToCompare);

					if (segment.isSsequenceSegment() == segmentToCompare
							.isSsequenceSegment()
							&& segment.getAmmountActiveCells() == segmentToCompare
									.getAmmountActiveCells()) {
						returnValue = 0;
					} else if ((segment.isSsequenceSegment() && !segmentToCompare
							.isSsequenceSegment())
							|| (segment.isSsequenceSegment() == segmentToCompare
									.isSsequenceSegment() && segment
									.getAmmountActiveCells() > segmentToCompare
									.getAmmountActiveCells())) {
						returnValue = 1;
					} else {
						returnValue = -1;
					}
					return returnValue;
				}
			});

			if (activeSegments.size() > 0) {
				returnValue = activeSegments.get(activeSegments.size() - 1);
			}
		}
		return returnValue;
	}

	/**
	 * adaptSegments(segmentList, positiveReinforcement) This function iterates
	 * through a list of segmentUpdate's and reinforces each segment. For each
	 * segmentUpdate element, the following changes are performed. If
	 * positiveReinforcement is true then synapses on the active list get their
	 * permanence counts incremented by permanenceInc. All other synapses get
	 * their permanence counts decremented by permanenceDec. If
	 * positiveReinforcement is false, then synapses on the active list get
	 * their permanence counts decremented by permanenceDec. After this step,
	 * any synapses in segmentUpdate that do yet exist get added with a
	 * permanence count of initialPerm. I have the feeling this text is wrong it
	 * should be After this step, any synapses in segmentUpdate that do not yet
	 * exist get added with a permanence count of initialPerm.
	 * 
	 * @param segmentUpdateList2
	 * @param b
	 */
	protected void adaptSegments(List<SegmentUpdate> segmentUpdateList,
			boolean positiveReinforcement) {
		if (segmentUpdateList != null) {
			for (SegmentUpdate segmentUpdate : segmentUpdateList) {
				Cell cell = cells[segmentUpdate.getColumnIndex()][segmentUpdate
						.getCellIndex()][Cell.NOW];
				if (segmentUpdate.getSegmentUpdateIndex() != -1) {
					Segment segment = null;
					for (Segment segmentq : cell.getSegments()) {
						if (segmentq.getSegmentIndex() == segmentUpdate
								.getSegmentUpdateIndex()) {
							segment = segmentq;
							break;
						}
					}

					if (segmentUpdate.isSequenceSegment()) {
						segment.setSequenceSegment(true);
					}
					// TODO All other synapses get their permanence counts
					// decremented by permanenceDec

					for (LateralSynapse synapse2 : segmentUpdate
							.getActiveSynapses()) {
						if (segment.getSynapses().contains(synapse2)) {
							if (positiveReinforcement) {
								synapse2.setPermanance(Math.min(synapse2
										.getPermanance()
										+ TemporalPooler.PERMANANCE_INC, 1.0));
							} else {
								
								synapse2.setPermanance(Math.max(synapse2
										.getPermanance()
										- TemporalPooler.PERMANANCE_DEC, 0.0));
							}
						} else {
							segment.getSynapses().add(synapse2);
						}
					}

					for (LateralSynapse synapse3 : segment.getSynapses()) {
						if (!segmentUpdate.getActiveSynapses().contains(
								synapse3)) {
							synapse3.setPermanance(Math.max(synapse3
									.getPermanance()
									- TemporalPooler.PERMANANCE_DEC, 0.0));
						}
					}
				} else {

					// TODO Maybe We should create a new Segment now
				}

			}
		}
	}

	/**
	 * getBestMatchingCell(c) For the given column, return the cell with the
	 * best matching segment (as defined above). If no cell has a matching
	 * segment, then return the cell with the fewest number of segments.
	 * 
	 * @param c
	 * @param time
	 * @return
	 */
	protected Cell getBestMatchingCell(int c, int time) {

		Cell returnValue = null;
		List<Segment> bestMatchingSegments = new ArrayList<Segment>();
		// TODO all cells have the same amount of segments. Do they mean
		// connected synapses?
		int cellIndexWithFewestNumberOfSegments = -1;
		int lowestAmmountOfSegments = 0;
		if (Column.CELLS_PER_COLUMN > 0) {
			cellIndexWithFewestNumberOfSegments = 0;
			lowestAmmountOfSegments = cells[c][0][time].getSegments().size();
		}
		// find the cell with the fewest amount of segments and on the same time
		// find the bestMatching segment from the
		// cell
		for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
			if (cells[c][i][time].getSegments().size() < lowestAmmountOfSegments) {
				cellIndexWithFewestNumberOfSegments = i;
				lowestAmmountOfSegments = cells[c][i][time].getSegments()
						.size();
			}
			Segment bestMatchingSegmentPerCell = getBestMatchingSegment(c, i,
					time);
			if (bestMatchingSegmentPerCell != null) {
				bestMatchingSegments.add(bestMatchingSegmentPerCell);
			}
		}
		if (bestMatchingSegments.size() != 0) {
			// List<Segment> segments = new ArrayList<Segment>();
			//
			// for (Segment segment : bestMatchingSegments) {
			// segments.add(segment);
			// }
			Segment bestMatchingSegment = getBestMatchingSegment(
					bestMatchingSegments, time);
			returnValue = cells[c][bestMatchingSegment.getCellIndex()][time];
		} else {
			returnValue = cells[c][cellIndexWithFewestNumberOfSegments][time];
			// return the cell with the fewest number of segments.
		}

		return returnValue;
	}

	/**
	 * getSegmentActiveSynapses(c, i, t, s, newSynapses= false) Return a
	 * segmentUpdate data structure containing a list of proposed changes to
	 * segment s. Let activeSynapses be the list of active synapses where the
	 * originating cells have their activeState output = 1 at time step t. (This
	 * list is empty if s = -1 since the segment doesn't exist.) newSynapses is
	 * an optional argument that defaults to false. If newSynapses is true, then
	 * newSynapseCount - count(activeSynapses) synapses are added to
	 * activeSynapses. These synapses are randomly chosen from the set of cells
	 * that have learnState output = 1 at time step t. 
	 * In my version an active synapse doesn't have to be connected because this 
	 * is the place where learning should be happening. Also, The synapses that 
	 * are being updated are sometimes chosen in getBestmatchingegment. 
	 * And in that method it doesn't matter if the synapse is connected or not.
	 * 
	 * @param c
	 * @param i
	 * @param segment
	 * @param t
	 * @param b
	 * @return
	 */
	protected SegmentUpdate getSegmentActiveSynapses(int c, int i,
			Segment segment, int time, boolean newSynapses) {
		SegmentUpdate returnValue = null;
		// TODO Maybe if segment= null add synapses and a new segment.
		List<LateralSynapse> activeSynapses = new ArrayList<LateralSynapse>();
		if (segment != null) {
			for (LateralSynapse synapse : segment.getSynapses()) {
				Cell cell = cells[synapse.getFromColumnIndex()][synapse
						.getFromCellIndex()][time];

				if (cell.hasActiveState()) {
					activeSynapses.add(synapse);
				}
			}
			int ammountNewSynapsesToAdd = TemporalPooler.NEW_SYNAPSE_COUNT
					- activeSynapses.size();
			if (newSynapses && ammountNewSynapsesToAdd > 0) {

				List<Cell> cellsWithLearnstate = new ArrayList<Cell>();
				//TODO test this
				if((LEARNING_RADIUS<xxMax||LEARNING_RADIUS<yyMax) &&LEARNING){
					//If the cell doesn't have its neighbors calculated yet, that will happen now.
					//The neighbors are set on the cells with time NOW.(In that case we don't loose them if the time is recalculated.
					if(cells[c][i][Cell.NOW].getNeighbors()==null){
						cells[c][i][Cell.NOW].setNeigbors(getNeighbors(cells[c][i][Cell.NOW]));						
					}
					
					for (Cell neighborCell : cells[c][i][Cell.NOW].getNeighbors()) {
						Cell potentialCellWithLearnState=cells[neighborCell.getColumnIndex()][neighborCell.getCellIndex()][time];
						if(potentialCellWithLearnState.hasLearnState()){
							cellsWithLearnstate.add(potentialCellWithLearnState);
						}
					}
				} else{
					for (int ci = 0; ci < xxMax * yyMax; ci++) {
						for (int ii = 0; ii < Column.CELLS_PER_COLUMN; ii++) {
	
							Cell cell = cells[ci][ii][time];
							if (cell.hasLearnState()) {
								cellsWithLearnstate.add(cell);
							}
						}
					}
				}
				if (cellsWithLearnstate.size() > 0) {
					Collections.shuffle(cellsWithLearnstate);

					if (cellsWithLearnstate.size() < ammountNewSynapsesToAdd) {
						ammountNewSynapsesToAdd = cellsWithLearnstate.size();
					}
					for (int k = 0; k < ammountNewSynapsesToAdd; k++) {

						Cell cell = cellsWithLearnstate.get(k);

						activeSynapses.add(new LateralSynapse(c, i, segment
								.getSegmentIndex(), cell.getColumnIndex(), cell
								.getCellIndex(), TemporalPooler.INITIAL_PERM));
					}
				}
			}

			returnValue = new SegmentUpdate(c, i, segment.getSegmentIndex(),
					activeSynapses);
		} else {
			// TODO Maybe add new Segment
			returnValue = new SegmentUpdate(c, i, -1, activeSynapses);
		}

		return returnValue;
	}

	/**
	 * getBestMatchingSegment(c, i, t) For the given column c cell i at time t,
	 * find the segment with the largest number of active synapses. This routine
	 * is aggressive in finding the best match. The permanence value of synapses
	 * is allowed to be below connectedPerm. The number of active synapses is
	 * allowed to be below activationThreshold, but must be above minThreshold.
	 * The routine returns the segment index. If no segments are found, then an
	 * index of -1 is returned.
	 * 
	 * @param cell
	 * @return
	 */

	protected Segment getBestMatchingSegment(int c, int i, final int time) {

		Cell cell = cells[c][i][time];
		List<Segment> segments = cell.getSegments();
		return (getBestMatchingSegment(segments, time));
	}

	protected Segment getBestMatchingSegment(List<Segment> segments,
			final int time) {
		Segment returnValue = null;

		Collections.sort(segments, new Comparator<Segment>() {

			public int compare(Segment segment, Segment toCompare) {
				int ammountActiveCells = 0;
				int ammountActiveCellsToCompare = 0;
				for (LateralSynapse synapse : segment.getSynapses()) {
					if (cells[synapse.getFromColumnIndex()][synapse
							.getFromCellIndex()][time].hasActiveState()) {

						ammountActiveCells++;
					}
				}
				segment.setAmmountActiveCells(ammountActiveCells);
				for (LateralSynapse synapse : toCompare.getSynapses()) {
					if (cells[synapse.getFromColumnIndex()][synapse
							.getFromCellIndex()][time].hasActiveState()) {
						ammountActiveCellsToCompare++;
					}
				}
				toCompare.setAmmountActiveCells(ammountActiveCellsToCompare);
				int returnValue = 0;
				if (ammountActiveCells == ammountActiveCellsToCompare) {
					returnValue = 0;
				} else if (ammountActiveCells > ammountActiveCellsToCompare) {
					returnValue = 1;
				} else {
					returnValue = -1;
				}
				return returnValue;

			}
		});
		if (segments.get(segments.size() - 1) != null
				&& segments.get(segments.size() - 1).getAmmountActiveCells() > TemporalPooler.MIN_TRESHOLD) {
			returnValue = segments.get(segments.size() - 1);
		}
		return returnValue;
	}

	/**
	 * segmentActive(s, t, state) This routine returns true if the number of
	 * connected synapses on segment s that are active due to the given state at
	 * time t is greater than activationThreshold. The parameter state can be
	 * activeState, or learnState.
	 * 
	 * @param segment
	 * @param time
	 *            can be 1 meaning now or 0 meaning t-1
	 * @param learnState
	 * @return
	 */
	public boolean segmentActive(Segment segment, int time, int state) {
		List<LateralSynapse> synapses = segment.getSynapses();
		int ammountConnected = 0;
		Cell fromCell = null;
		// TODO take the synapses from now not other time.
		for (LateralSynapse synapse : synapses) {

			if (state == Cell.LEARN_STATE) {
				// TODO are all cells that have learnstate also Active or should we also check if the cell is/was active?
				if (synapse.isConnected()
						&& cells[synapse.getFromColumnIndex()][synapse
								.getFromCellIndex()][time].hasLearnState()) {
					ammountConnected++;
				}
			} else {
				if (state == Cell.ACTIVE_STATE) {

					if (synapse.isConnected()
							&& cells[synapse.getFromColumnIndex()][synapse
									.getFromCellIndex()][time].hasActiveState()) {
						ammountConnected++;

					}
				}
			}

		}
		return ammountConnected > TemporalPooler.ACTIVATION_TRESHOLD;

	}

	public void setActiveColumns(ArrayList<Column> activeColumns) {

		Object[] objects = activeColumns.toArray();
		Column[] actives = new Column[objects.length];
		System.arraycopy(objects, 0, actives, 0, objects.length);
		this.activeColumns = actives;
	}

	public void nextTime() {
		for (int c = 0; c < xxMax * yyMax; c++) {
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				cells[c][i][Cell.BEFORE] = cells[c][i][1];// old cell is new cell
				cells[c][i][Cell.BEFORE].setTime(Cell.BEFORE);
				
				cells[c][i][Cell.NOW] = new Cell(c, i, 1, cells[c][i][Cell.BEFORE].getXpos(),
				cells[c][i][Cell.BEFORE].getYpos(), cells[c][i][Cell.BEFORE].getSegments());
				
				if((LEARNING_RADIUS<xxMax||LEARNING_RADIUS<yyMax) &&LEARNING &&cells[c][i][Cell.BEFORE].getNeighbors()!=null){
					cells[c][i][Cell.NOW].setNeigbors(cells[c][i][Cell.BEFORE].getNeighbors());
				}
				// TODO there is discussion if this should be remembered over
				// time
				// cell.setSegmentUpdateList(cells[c][i][0].getSegmentUpdateList());
			}
		}
	}
}
