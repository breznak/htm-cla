/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.view;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import nl.vanrijn.model.Cell;
import nl.vanrijn.model.Column;
import nl.vanrijn.model.LateralSynapse;
import nl.vanrijn.model.Segment;
import nl.vanrijn.pooler.TemporalPooler;

public class TemporaalPoolerApplet extends Applet implements Runnable {

	private boolean				mouseDragged		= false;

	private boolean				mousePressed		= false;

	Button						addPattern			= null;

	private boolean				black				= true;

	/**
	 * input(t,j) The input to this level at time t. input(t, j) is 1 if the j'th input is on.
	 */
	private int[]				columns				= new int[144];

	private List<int[]>			patterns			= new ArrayList<int[]>();

	private static final long	serialVersionUID	= 1L;

	private Graphics			graphics;

	private Image				image;

	// private Column[] columns;

	private TemporalPooler		tempo				= new TemporalPooler(12,12);

	DecimalFormat				df2					= new DecimalFormat("#,###,###,##0.00");

	private Cell[][][]			cells;

	private Thread				runner;

	private boolean				starting			= true;

	private int					counter;

	public void init() {
		Button submitButton = new Button("Temporal Pooler");
		submitButton.setActionCommand("temporal");
		submitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("temporal")) {
					// this.invokeTemporalPooler()();
					invokeTemporalPooler();

					columns = new int[144];
				}
			}
		});
		add(submitButton);

		Button reset = new Button("reset");
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("reset"))
				// System.out.println("reset");
					reset();
			}
		});
		add(reset);

		Button running = new Button("run");
		running.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("run")) running();
			}
		});
		add(running);

		Button stop = new Button("stop");
		stop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("stop")) stopping();
			}
		});
		add(stop);

		addPattern = new Button("addPattern       ");
		addPattern.setActionCommand("addPattern");
		addPattern.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("addPattern")) {
					addPattern();

				}
			}
		});
		add(addPattern);

		Button resetPatterns = new Button("resetPatterns");
		resetPatterns.setActionCommand("resetPatterns");
		resetPatterns.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("resetPatterns")) {
					patterns = new ArrayList<int[]>();
					addPattern.setLabel("addPattern");
				}
			}
		});
		add(resetPatterns);

		image = createImage(getSize().width, getSize().height);
		graphics = image.getGraphics();

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				mousePressed = true;
				mouseOver(e.getX(), e.getY());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// After the release of mouseDrag, no mouseRelease should occur.
				if (!mouseDragged) {
					mouseOver(e.getX(), e.getY());
				}
				mouseDragged = false;
			}

		});
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseDragged = true;
				mouseOver(e.getX(), e.getY());
			}
		});
		tempo.init();

		draw();
	}

	protected void stopping() {
		this.starting = false;

	}

	protected void addPattern() {

		int[] pattern = new int[columns.length];
		System.arraycopy(columns, 0, pattern, 0, columns.length);
		patterns.add(pattern);

		// System.out.println("pattern two saved");
		redraw();
		invokeTemporalPooler();
		addPattern.setLabel("addPattern(" + patterns.size() + ")");
		columns = new int[144];

	}

	protected void running() {
		runner = new Thread(this);

		runner.start();
	}

	public void reset() {

		clearAll();
		for (int i = 0; i < columns.length; i++) {
			columns[i] = 0;
		}
		tempo.init();
		draw();
	}

	@Override
	public void paint(Graphics graphics) {
		graphics.drawImage(image, 0, 0, this);
	}

	private void mouseOver(int x, int y) {

		int index = -1;
		outer: for (int yy = 0; yy < 12; yy++) {

			for (int xx = 0; xx < 12; xx++) {
				index++;
				if (y < 19 * yy + 116 && y > 19 * yy + 100) {
					if (x > 19 * xx && x < 19 * xx + 16) {

						if (mousePressed) {
							if (columns[index] == 1) {
								black = false;
							} else {
								black = true;
							}
							mousePressed = false;

						} else {
							if (mouseDragged) {

								if (black) {
									drawBlackOval(xx, yy);
									setInputValue(index, 1);
								} else {
									drawWhiteOval(xx, yy);

									setInputValue(index, 0);
								}

							} else {

								if (columns[index] == 1) {
									drawWhiteOval(xx, yy);

									setInputValue(index, 0);
								} else {
									drawBlackOval(xx, yy);

									setInputValue(index, 1);
								}

							}

							repaint();
						}

						break outer;
					} else
						if (x > 19 * xx + 260 && x < 19 * xx + 260 + 16) {
							if (!mousePressed) {
								logCell(0, index, xx, yy);
							}
							if (mousePressed) {
								mousePressed = false;
							}
							break outer;
						} else
							if (x > 19 * xx + 520 && x < 19 * xx + 520 + 16) {
								if (!mousePressed) {
									logCell(1, index, xx, yy);
								}
								if (mousePressed) {
									mousePressed = false;
								}
								break outer;

							} else
								if (x > 19 * xx + 780 && x < 19 * xx + 780 + 16) {
									if (!mousePressed) {
										logCell(2, index, xx, yy);
									}
									if (mousePressed) {
										mousePressed = false;
									}
									break outer;
								}

				}
			}
		}
	}

	private void logCell(int layer, int index, int xPos, int yPos) {

		Cell cell = tempo.getCells()[index][layer][Cell.BEFORE];
		System.out.println(cell);
		for (Segment segment : cell.getSegments()) {

			for (LateralSynapse synapse : segment.getConnectedSynapses()) {

				if (tempo.getCells()[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][Cell.BEFORE]
						.hasActiveState()) {
					System.out.println(segment);
					System.out.println(synapse);
					System.out
							.println(tempo.getCells()[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][Cell.BEFORE]);
				}
			}

			if (this.tempo.segmentActive(segment, Cell.BEFORE, Cell.ACTIVE_STATE)) {
				System.out.println(segment + " is active");
			}
		}
	}

	/**
	 * Draws the columns and .Without input/output
	 */
	public void draw() {
		graphics.setColor(Color.BLACK);
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 12; y++) {
				graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);

				graphics.drawOval(19 * x + 260, 100 + (19 * y), 16, 16);
				graphics.drawOval(19 * x + 520, 100 + (19 * y), 16, 16);
				graphics.drawOval(19 * x + 780, 100 + (19 * y), 16, 16);

			}
		}
		repaint();
	}

	public void redraw() {
		// System.out.println("redraw");
		graphics.setColor(Color.black);
		int index = 0;
		for (int y = 0; y < 12; y++) {
			for (int x = 0; x < 12; x++) {

				graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
				// System.out.print(columns[index]+",");
				if (columns[index] == 1) {
					// System.out.println("nu");
					graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
				}
				index++;
			}
		}
		repaint();
	}

	private void drawBlackOval(int x, int y) {
		graphics.setColor(Color.black);
		// graphics.setColor(Color.getHSBColor(10, 0.5f,0.5f));
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);

	}

	private void drawWhiteOval(int x, int y) {
		graphics.setColor(Color.white);
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
		graphics.setColor(Color.black);
		graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);

	}

	private void setInputValue(int index, int value) {
		columns[index] = value;
	}

	private void clearAll() {
		graphics.clearRect(0, 0, 1050, 1050);
		draw();
	}

	public void invokeTemporalPooler() {

		clearAll();
		//
		ArrayList<Column> activeColumns = new ArrayList<Column>();
		int index = -1;
		for (int yy = 0; yy < 12; yy++) {

			for (int xx = 0; xx < 12; xx++) {
				index++;
				if (columns[index] == 1) {
					// System.out.println(index+" "+xx+","+yy);
					Column column = new Column(index,xx,yy);
					column.setActive(true);
					activeColumns.add(column);
				}
			}
		}

		tempo.setActiveColumns(activeColumns);
		tempo.computeActiveState();
		tempo.calculatePredictedState();
		tempo.updateSynapses();
		this.cells = tempo.getCells();

		for (int c = 0; c < 144; c++) {
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				Cell cell = cells[c][i][1];
				if (cell.hasActiveState()) {
					// System.out.println(cell
					// +" "+cell.getXpos()+","+cell.getYpos()
					// );
					graphics.setColor(Color.black);

					switch (cell.getCellIndex()) {
						case 0: {

							// System.out.println("nuuuuu0" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(19 * cell.getXpos() + 260, 100 + (19 * cell.getYpos()), 16, 16);
							break;
						}
						case 1: {
							// System.out.println("nuuuuu1" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(19 * cell.getXpos() + 520, 100 + (19 * cell.getYpos()), 16, 16);
							break;
						}
						case 2: {
							// System.out.println("nuuuuu2" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(19 * cell.getXpos() + 780, 100 + (19 * cell.getYpos()), 16, 16);
							break;
						}
						default:
							break;
					}
				}
				if (cell.hasPredictiveState()) {
					// System.out.println(cell
					// +" "+cell.getXpos()+","+cell.getYpos()
					// );
					graphics.setColor(Color.blue);

					switch (cell.getCellIndex()) {

						case 0: {

							// System.out.println("nuuuuu0" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(19 * cell.getXpos() + 263, 103 + (19 * cell.getYpos()), 10, 10);
							break;
						}
						case 1: {
							// System.out.println("nuuuuu1" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(19 * cell.getXpos() + 523, 103 + (19 * cell.getYpos()), 10, 10);
							break;
						}
						case 2: {
							// System.out.println("nuuuuu2" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(19 * cell.getXpos() + 783, 103 + (19 * cell.getYpos()), 10, 10);
							break;
						}
						default:
							break;
					}
				}
				if (cell.hasLearnState()) {
					// System.out.println(cell
					// +" "+cell.getXpos()+","+cell.getYpos()
					// );
					graphics.setColor(Color.red);
					switch (cell.getCellIndex()) {
						case 0: {

							// System.out.println("nuuuuu0" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(19 * cell.getXpos() + 266, 105 + (19 * cell.getYpos()), 5, 5);
							break;
						}
						case 1: {
							// System.out.println("nuuuuu1" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(19 * cell.getXpos() + 526, 105 + (19 * cell.getYpos()), 5, 5);
							break;
						}
						case 2: {
							// System.out.println("nuuuuu2" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(19 * cell.getXpos() + 786, 105 + (19 * cell.getYpos()), 5, 5);
							break;
						}
						default:
							break;
					}

				}
			}
		}
		repaint();
		tempo.nextTime();
	}

	public void run() {
		if (patterns.size() != 0) {
			this.starting = true;

			this.counter = 0;
			do {

				System.arraycopy(patterns.get(this.counter), 0, columns, 0, columns.length);

				invokeTemporalPooler();
				redraw();

				try {
					Thread.sleep(200);
				} catch (Exception e) {
					System.out.println("fucked");
				}
				if (counter < patterns.size() - 1) {
					counter++;
				} else {
					counter = 0;
				}
				// System.out.println(this.starting);
			} while (this.starting);
		}

	}
}
