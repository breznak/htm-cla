/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.pooler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import nl.vanrijn.model.Cell;
import nl.vanrijn.model.Column;
import nl.vanrijn.model.DendriteSegment;
import nl.vanrijn.model.LateralSynapse;
import nl.vanrijn.model.helper.SegmentUpdate;
import nl.vanrijn.utils.HelperMath;

public class TemporalPooler {

    /**
     * this boolean should be switched of if no learning is desirable anymore.
     */
    private final boolean LEARNING = true;
    /**
     * permanenceInc Amount permanence values of synapses are incremented when
     * activity-based learning occurs.
     */
    public static final double PERMANANCE_INC = 0.1;
    // TODO: choose reasonable value for PERMANANCE_INC
    /**
     * permanenceDec Amount permanence values of synapses are decremented when
     * activity-based learning occurs.
     */
    public static final double PERMANANCE_DEC = 0.1;
    // TODO choose reasonable value for PERMANANCE_DEC
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
    private static int LEARNING_RADIUS = 2;
    /**
     * activeColumns Array of columns that are winners due to bottom-up input
     * (this is the output of the spatial pooler).
     */
    private Column[] activeColumns;
    /**
     * cell[c][i] An array of all cells, indexed by c(column index) and i(cell
     * index) (and cells themselves have t(time) property).
     */
    private Cell[][] cells;
    private int xxMax;
    private int yyMax;

    public TemporalPooler(int xxMax, int yyMax) {
        this.xxMax = xxMax;
        this.yyMax = yyMax;

        // init
        cells = new Cell[xxMax * yyMax][Column.CELLS_PER_COLUMN];

        List<Integer> collumnIndexes = HelperMath.seq(xxMax * yyMax);

        Random random = new Random();
        int c = 0;
        for (int yy = 0; yy < yyMax; yy++) {
            for (int xx = 0; xx < xxMax; xx++) {
                for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
                    List<DendriteSegment> segments = new ArrayList<>();
                    Column col = new Column(i, xx, yy);
                    cells[c][i] = new Cell(col, segments);  //! hack: cells with segments must be here, below we add elements to segments, which link to cell
                    for (int s = 0; s < AMMOUNT_OF_SEGMENTS; s++) {
                        List<LateralSynapse> synapses = new ArrayList<>();
                        Collections.shuffle(collumnIndexes);
                        for (int y = 0; y < AMMOUNT_OF_SYNAPSES; y++) {
                            // TODO can a cell predict itself?
                            //FIXME 3?!
                            synapses.add(new LateralSynapse(cells[collumnIndexes.get(y)][random.nextInt(3)], TemporalPooler.INITIAL_PERM));
                        }
                        segments.add(new DendriteSegment(cells[c][i], s, synapses));
                        // System.out.println(c);
                    }
                }
                c++;
            }
        }
    }

    /**
     * This method returns the cells that this cell can learn from. These cells
     * have to be in Learninradius of this cell
     *
     * @param cell
     * @return
     */
    private List<Cell> neighborhoodCells(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        Point pos = SpatialPooler.getColumnPosition(cell.getColumn(), cell.getColumn());
        int xxStart = HelperMath.inRange(cell.getColumn().getxPos() - LEARNING_RADIUS, 0, xxMax); //TODO use Helper fn
        int xxEnd = HelperMath.inRange(cell.getColumn().getxPos() + LEARNING_RADIUS + 1, 0, xxMax);
        int yyStart = HelperMath.inRange(cell.getColumn().getyPos() - LEARNING_RADIUS, 0, yyMax);
        int yyEnd = HelperMath.inRange(cell.getColumn().getyPos() + LEARNING_RADIUS + 1, 0, yyMax);
        int c;
        for (int yy = yyStart; yy < yyEnd; yy++) {
            for (int xx = xxStart; xx < xxEnd; xx++) {
                //TODO remove 2 fors, c should be yy*yMax+xx ?
                c = yy * xxMax + xx;
                for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
                    // potentialNeigbor
                    neighbors.add(cells[c][i]);
                }
            }
        }
        return neighbors;
    }

    /**
     *
     * @return cells in this temporal pooler
     */
    public Cell[][] getCells() {
        return cells.clone();
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
                if (cells[column.getColumnIndex()][i].output(HelperMath.BEFORE) == Cell.PREDICT) {
                    // get the segment that became active in the time step
                    // before.(That made this cell active).
                    // So the synapses that made this segment active where also
                    // from one time step before. and the cells
                    // connected to these synapses also.
                    DendriteSegment segment = getActiveSegment(column.getColumnIndex(), i, HelperMath.BEFORE, Cell.ACTIVE);
                    if (segment != null && segment.isSequenceSegment()) {
                        buPredicted = true;
                        cells[column.getColumnIndex()][i].setOutput(Cell.ACTIVE);
                        // if these cells also had learnstate
                        if (segmentActive(segment, HelperMath.BEFORE, Cell.PREDICT) && LEARNING) {
                            lcChosen = true;
                            cells[column.getColumnIndex()][i].setLearnState(true);
                        }
                    }
                }
            }
            if (!buPredicted) {
                for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
                    cells[column.getColumnIndex()][i].setOutput(Cell.ACTIVE);
                }
            }
            if (!lcChosen && LEARNING) {
                Cell cellToUpdate = getBestMatchingCell(column.getColumnIndex(), HelperMath.BEFORE);
                // TODO Maybe now a new segment should be created in stead of
                // getting the best matching segment
                DendriteSegment segment = getBestMatchingSegment(column.getColumnIndex(), cellToUpdate.getName(), HelperMath.BEFORE);
                SegmentUpdate sUpdate = getSegmentActiveSynapses(column.getColumnIndex(), cellToUpdate.getName(), segment, HelperMath.BEFORE, true);
                sUpdate.setSequenceSegment(true);
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
                Cell cell = cells[c][i];

                for (int s = 0; s < cell.getSegments().size(); s++) {
                    DendriteSegment segment = cell.getSegments().get(s);
                    // is this segment active from cells that are active now?(In phase 1)
                    if (segmentActive(segment, HelperMath.NOW, Cell.ACTIVE)) {
                        cell.setOutput(Cell.PREDICT);
                        if (LEARNING) {
                            SegmentUpdate activeUpdate = getSegmentActiveSynapses(c, i, segment, HelperMath.NOW, false);
                            cell.getSegmentUpdateList().add(activeUpdate);
                            // TODO This should not happen so often. Only once for
                            // an active cell. because it will always be the same segment
                            DendriteSegment predSegment = getBestMatchingSegment(c, i, HelperMath.BEFORE);
                            SegmentUpdate predUpdate = getSegmentActiveSynapses(c, i, predSegment, HelperMath.BEFORE, true);
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
        if (LEARNING) {
            for (int c = 0; c < xxMax * yyMax; c++) {
                for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
                    Cell cell = cells[c][i];
                    if (cell.getLearnState(HelperMath.NOW)) {
                        adaptSegments(cell.getSegmentUpdateList(), SegmentUpdate.POSITIVE_REINFORCEMENT);
                        cell.getSegmentUpdateList().clear();
                    } else // TODO I have the feeling that this is wrong. It should be:if
                    // the cell was predicted but is not
                    // active now. (or maybe not)
                    if (!(cells[c][i].output(HelperMath.NOW) == Cell.PREDICT) && (cells[c][i].output(HelperMath.BEFORE) == Cell.PREDICT)) {
                        adaptSegments(cell.getSegmentUpdateList(), SegmentUpdate.NO_POSITIVE_REINFORCEMENT);
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
    // TODO this should only return a segment index. not a segment. the time of the
    // segment doen't mather!!
    public DendriteSegment getActiveSegment(int c, int i, final int time, int state) {
        Cell cell = cells[c][i];
        List<DendriteSegment> activeSegments = new ArrayList<>();

        for (DendriteSegment segment : cell.getSegments()) {
            if (segmentActive(segment, time, state)) {
                activeSegments.add(segment);
            }
        }

        Collections.sort(activeSegments, new Comparator<DendriteSegment>() {
            //TODO move compare to DendriteSegment class?
            @Override
            public int compare(DendriteSegment segment, DendriteSegment segmentToCompare) {
                //FIXME check these retcodes and actual returned numbers differ!
                // 1 sequence most activity
                // 2 sequence and active
                // 3 most activity
                // 4 least activity
                //
                int ammountActiveCells = 0;
                int ammountActiveCellsToCompare = 0;
                for (LateralSynapse synapse : segment.getSynapses()) {
                    if (synapse.getFromCell().output(time) == Cell.ACTIVE) {
                        ammountActiveCells++;
                    }
                }
                segment.setAmmountActiveCells(ammountActiveCells);
                for (LateralSynapse synapse : segmentToCompare.getSynapses()) {
                    if (synapse.getFromCell().output(time) == Cell.ACTIVE) {
                        ammountActiveCellsToCompare++;
                    }
                }
                segmentToCompare.setAmmountActiveCells(ammountActiveCellsToCompare);

                if (segment.isSequenceSegment() == segmentToCompare.isSequenceSegment()
                        && segment.getAmmountActiveCells() == segmentToCompare.getAmmountActiveCells()) {
                    return 0;
                } else if ((segment.isSequenceSegment() && !segmentToCompare.isSequenceSegment())
                        || (segment.isSequenceSegment() == segmentToCompare.isSequenceSegment()
                        && segment.getAmmountActiveCells() > segmentToCompare.getAmmountActiveCells())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        if (activeSegments.size() > 0) {
            return activeSegments.get(activeSegments.size() - 1); // last one
        }

        return null; //otherwise
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
    protected void adaptSegments(List<SegmentUpdate> segmentUpdateList, boolean positiveReinforcement) {
        if (segmentUpdateList == null) {
            return;
        }
        for (SegmentUpdate su : segmentUpdateList) {
            Cell cell = cells[su.getColumnIndex()][su.getCellIndex()];
            if (su.getSegmentUpdateIndex() != -1) {
                DendriteSegment segment = null;
                for (DendriteSegment segmentq : cell.getSegments()) {
                    if (segmentq.getSegmentIndex() == su.getSegmentUpdateIndex()) {
                        segment = segmentq;
                        break;
                    }
                }
                if (segment == null) { //TODO is segment ever null?
                    System.err.println("segment=null");
                    System.exit(1);
                }
                if (su.isSequenceSegment()) {
                    segment.setSequenceSegment(true);
                }
                // TODO All other synapses get their permanence counts
                // decremented by permanenceDec
                for (LateralSynapse synapse2 : su.getActiveSynapses()) {
                    if (segment.getSynapses().contains(synapse2)) {
                        if (positiveReinforcement) {
                            synapse2.setPermanance(Math.min(synapse2.getPermanance() + TemporalPooler.PERMANANCE_INC, 1.0));
                        } else {
                            synapse2.setPermanance(Math.max(synapse2.getPermanance() - TemporalPooler.PERMANANCE_DEC, 0.0));
                        }
                    } else {
                        segment.getSynapses().add(synapse2);
                    }
                }

                for (LateralSynapse synapse3 : segment.getSynapses()) {
                    if (!su.getActiveSynapses().contains(synapse3)) {
                        synapse3.setPermanance(Math.max(synapse3.getPermanance() - TemporalPooler.PERMANANCE_DEC, 0.0));
                    }
                }
            } else {
                // TODO Maybe We should create a new DendriteSegment now
            }
        }
    }

    /**
     * getBestMatchingCell(c) For the given column, return the cell with the
     * best matching segment (as defined above). If no cell has a matching
     * segment, then return the cell with the fewest number of segments.
     *
     * @param col
     * @param time
     * @return
     */
    protected Cell getBestMatchingCell(int col, int time) {
        List<DendriteSegment> bestMatchingSegments = new ArrayList<>();
        // TODO all cells have the same amount of segments. Do they mean
        // connected synapses?
        Cell min = cells[col][0];
        // find the cell with the fewest amount of segments and on the same time
        // find the bestMatching segment from the cell
        for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
            if (cells[col][i].getSegments().size() < min.getSegments().size()) {
                min = cells[col][i];
            }
            DendriteSegment bestMatchingSegmentPerCell = getBestMatchingSegment(col, i, time);
            if (bestMatchingSegmentPerCell != null) {
                bestMatchingSegments.add(bestMatchingSegmentPerCell);
            }
        }
        if (!bestMatchingSegments.isEmpty()) {
            DendriteSegment bestMatchingSegment = getBestMatchingSegment(bestMatchingSegments, time);
            return bestMatchingSegment.getBelongingCell();
        }
        return min; // return the cell with the fewest number of segments.
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
     * that have learnState output = 1 at time step t. In my version an active
     * synapse doesn't have to be connected because this is the place where
     * learning should be happening. Also, The synapses that are being updated
     * are sometimes chosen in getBestmatchingegment. And in that method it
     * doesn't matter if the synapse is connected or not.
     *
     * @param c
     * @param i
     * @param segment
     * @param t
     * @param b
     * @return
     */
    protected SegmentUpdate getSegmentActiveSynapses(int c, int i, DendriteSegment segment, int time, boolean newSynapses) {
        // TODO Maybe if segment= null add synapses and a new segment.
        List<LateralSynapse> activeSynapses = new ArrayList<>();
        if (segment == null) {
            // TODO Maybe add new DendriteSegment
            return new SegmentUpdate(c, i, -1, activeSynapses);
        }  // else
        for (LateralSynapse synapse : segment.getSynapses()) {
            if (synapse.getFromCell().output(time) == Cell.ACTIVE) {
                activeSynapses.add(synapse);
            }
        }
        int ammountNewSynapsesToAdd = TemporalPooler.NEW_SYNAPSE_COUNT - activeSynapses.size();
        if (newSynapses && ammountNewSynapsesToAdd > 0) {
            List<Cell> cellsWithLearnstate = new ArrayList<>();
            //TODO test this
            if ((LEARNING_RADIUS < xxMax || LEARNING_RADIUS < yyMax) && LEARNING) {
                //If the cell doesn't have its neighbors calculated yet, that will happen now.
                //The neighbors are set on the cells with time NOW.(In that case we don't loose them if the time is recalculated.
                if (cells[c][i].getNeighbors() == null) {
                    cells[c][i].setNeigbors(neighborhoodCells(cells[c][i]));
                }

                for (Cell neighborCell : cells[c][i].getNeighbors()) {
                    if (neighborCell.getLearnState(time)) { //TODO must make learnState time vector
                        cellsWithLearnstate.add(neighborCell);
                    }
                }
            } else {
                for (int ci = 0; ci < xxMax * yyMax; ci++) {
                    for (int ii = 0; ii < Column.CELLS_PER_COLUMN; ii++) {
                        Cell cell = cells[ci][ii];
                        if (cell.getLearnState(time)) {
                            cellsWithLearnstate.add(cell);
                        }
                    }
                }
            }
            if (!cellsWithLearnstate.isEmpty()) {
                Collections.shuffle(cellsWithLearnstate);

                if (cellsWithLearnstate.size() < ammountNewSynapsesToAdd) {
                    ammountNewSynapsesToAdd = cellsWithLearnstate.size();
                }
                for (int k = 0; k < ammountNewSynapsesToAdd; k++) {
                    Cell cell = cellsWithLearnstate.get(k);
                    activeSynapses.add(new LateralSynapse(cell, INITIAL_PERM));
                }
            }
        }

        return new SegmentUpdate(c, i, segment.getSegmentIndex(), activeSynapses);
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
    protected DendriteSegment getBestMatchingSegment(int c, int i, final int time) {
        return getBestMatchingSegment(cells[c][i].getSegments(), time);
    }

    private DendriteSegment getBestMatchingSegment(List<DendriteSegment> segments, final int time) {
        Collections.sort(segments, new Comparator<DendriteSegment>() {
            @Override
            public int compare(DendriteSegment segment, DendriteSegment toCompare) { //TODO implement compare and reuse!
                int ammountActiveCells = 0;
                int ammountActiveCellsToCompare = 0;
                for (LateralSynapse synapse : segment.getSynapses()) {
                    if (synapse.getFromCell().output(time) == Cell.ACTIVE) {
                        ammountActiveCells++;
                    }
                }
                segment.setAmmountActiveCells(ammountActiveCells);
                for (LateralSynapse synapse : toCompare.getSynapses()) {
                    if (synapse.getFromCell().output(time) == Cell.ACTIVE) {
                        ammountActiveCellsToCompare++;
                    }
                }
                toCompare.setAmmountActiveCells(ammountActiveCellsToCompare);

                if (ammountActiveCells == ammountActiveCellsToCompare) {
                    return 0;
                } else if (ammountActiveCells > ammountActiveCellsToCompare) {
                    return 1;
                } else {
                    return -1; //TODO implement HelperMath.GREATER etc
                }
            }
        });
        if ((segments.get(segments.size() - 1) != null) && (segments.get(segments.size() - 1).getAmmountActiveCells() > TemporalPooler.MIN_TRESHOLD)) {
            return segments.get(segments.size() - 1);
        }
        return null;
    }

    /**
     * segmentActive(s, t, state) This routine returns true if the number of
     * connected synapses on segment s that are active due to the given state at
     * time t is greater than activationThreshold. The parameter state can be
     * activeState, or learnState.
     *
     * @param segment
     * @param time can be 1 meaning now or 0 meaning t-1
     * @param learnState
     * @return
     */
    public boolean segmentActive(DendriteSegment segment, int time, int state) {
        List<LateralSynapse> synapses = segment.getSynapses();
        int ammountConnected = 0;
        // TODO take the synapses from now not other time.
        for (LateralSynapse synapse : synapses) {
            //TODO incorporate PREDICT in getLearnState() ?
            if (state == Cell.PREDICT && synapse.isConnected(CONNECTED_PERMANANCE) && synapse.getFromCell().getLearnState(time)) {
                // TODO are all cells that have learnstate also Active or should we also check if the cell is/was active?
                ammountConnected++;
            } else if (state == Cell.ACTIVE && synapse.isConnected(CONNECTED_PERMANANCE) && synapse.getFromCell().output(time) == Cell.ACTIVE) {
                ammountConnected++;
            }
        }
        return ammountConnected > TemporalPooler.ACTIVATION_TRESHOLD;
    }

    public void setActiveColumns(ArrayList<Column> activeColumns) {
        this.activeColumns = activeColumns.toArray(this.activeColumns);
    }
}
