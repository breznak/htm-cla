package nl.vanrijn.games;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import nl.vanrijn.model.Column;
import nl.vanrijn.pooler.TemporalPooler;

public class Pong extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Graphics graphics;
	private Image image;
	public int yPos;
	public int ballX;
	public int ballY=180;
	protected boolean moveRight;
	private boolean scored;
	private boolean moveUp;
	private boolean moveStraight=true;
	private int scoreLeft=0;
	private int scoreRight=0;
	private TemporalPooler tempo=new TemporalPooler(39, 28);
	private int[]				columns				= new int[3];
	

	@Override
	public void paint(Graphics graphics) {
		graphics.drawImage(image, 0, 0, this);
	}

	@Override
	
	
	public void init() {
		super.init();
		tempo.init();
		
		image = createImage(getSize().width, getSize().height);
		graphics = image.getGraphics();
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				movePlayer(e.getY());
			}
		});
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				startGame();
			}
		});
	}

	protected void movePlayer(int yPos) {
		if (yPos > 260) {
			yPos = 260;
		} else if(yPos<20){
			yPos=20;
			
		}
		graphics.clearRect(420, 40 + this.yPos, 10, 40);
		graphics.clearRect(20, 40 + this.yPos, 10, 40);	
		this.yPos = yPos;
		graphics.fillRect(420, 40 + yPos, 10, 40);
		
		

		graphics.fillRect(20, 40 + this.yPos, 10, 40);		
		repaint();
	}

	@Override
	public void start() {
		super.start();
		drawField();
	}

	

	private void startGame() {

		scored=false;
		
		Thread thread = new Thread(new Runnable() {

			public void run() {
				if(scoreLeft==9 || scoreRight==9){
					scoreLeft=0;
					scoreRight=0;
					graphics.clearRect(170, 0, 50, 50);
					graphics.drawString(""+scoreLeft, 190, 49);
					graphics.clearRect(230, 0,50, 50);
					graphics.drawString(""+scoreRight, 240, 49);
				}
				ballX=130;
				ballY=180;
				while (!scored) {
					moveBall();
					repaint();
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						System.out.println("fucked");
					}					
				} 
				graphics.clearRect(100 + ballX, 30+ballY, 10, 10);
			}
			

		});
		thread.start();
	}

	protected void moveBall() {
		if(ballX!=120){//middle line should not be deleted
			graphics.clearRect(100 + ballX, 30+ballY, 10, 10);
		}		
		if(ballX>310){
			scoreLeft++;
			moveRight=true;

			moveStraight=true;
			this.scored=true;
			graphics.clearRect(170, 0, 50, 50);
			if(scoreLeft==9){
				graphics.setColor(Color.red);
			} else{
				graphics.setColor(Color.green);
			}
			graphics.drawString(""+scoreLeft, 190, 49);
			graphics.setColor(Color.green);
			
			
		} else if ( ballX<-70){
			scoreRight++;
			moveRight=false;
			moveStraight=true;
			this.scored=true;
			graphics.clearRect(230, 0,50, 50);
			if(scoreRight==9){
				graphics.setColor(Color.red);
			} else{
				graphics.setColor(Color.green);
			}
			graphics.drawString(""+scoreRight, 240, 49);
			graphics.setColor(Color.green);
		}
		
		if(ballY<=30){
			Toolkit.getDefaultToolkit().beep();
			moveUp=true;
		} else if(ballY>290){
			moveUp=false;
			Toolkit.getDefaultToolkit().beep();			
		}
		
		if(ballX==310 && yPos+20<=ballY && yPos+30>=ballY){//middle
			Toolkit.getDefaultToolkit().beep();
			moveStraight=true;
			moveRight=!moveRight;
		} else		
		if(ballX==310 && yPos<=ballY && yPos+20>=ballY){//topend
			Toolkit.getDefaultToolkit().beep();
			
			moveUp=false;
			moveStraight=false;
			
			moveRight=!moveRight;
		}else if(ballX==310 && yPos+30<=ballY && yPos+50>=ballY){//lowerend
			Toolkit.getDefaultToolkit().beep();
			
			moveUp=true;
			moveStraight=false;
			
			moveRight=!moveRight;
		} else if(ballX==-70 && yPos+20<=ballY && yPos+30>=ballY){//middle
			Toolkit.getDefaultToolkit().beep();
			moveStraight=true;
			moveRight=!moveRight;
		} else if(ballX==-70 && yPos<=ballY && yPos+20>=ballY){//end
			Toolkit.getDefaultToolkit().beep();
			
			moveUp=false;
			moveStraight=false;
			
			moveRight=!moveRight;
		} else if(ballX==-70 && yPos+30<=ballY && yPos+50>=ballY){//end
			Toolkit.getDefaultToolkit().beep();
			
			moveUp=true;
			moveStraight=false;
			
			moveRight=!moveRight;
		}
		if(!moveStraight){
			if(moveUp){
				ballY +=10;
			} else{
				ballY -=10;
			}
		}
		if(moveRight){
			ballX += 10;
		} else{
			ballX -= 10;
		}
		graphics.fillRect(100 + ballX, 30+ballY, 10, 10);
		//System.out.println(ballX+","+ballY);
		temporal();
		//System.out.println(yPos);
		
	}
	
	private void temporal(){
		ArrayList<Column> activeColumns = new ArrayList<Column>();
		int index = -1;
		for (int yy = 30; yy < 320; yy+=10) {
			
			for (int xx = -70; xx < 320; xx+=10) {
				index++;
				
				if(yPos>= yy && yPos<yy+10 && xx==-70){
					//System.out.println(yy+"");
					Column column = new Column(index,-80,yy);//is -80 TODO right?
					column.setActive(true);
					activeColumns.add(column);				
				}
				if(ballX==xx && ballY==yy){
					
					//System.out.print("found");
					Column column = new Column(index,xx,yy);
					column.setActive(true);
					activeColumns.add(column);
				}
			}
		}
		
//TODO set mouse cordinate as active column between 20 and 260
		//System.out.println(activeColumns.size());
		tempo.setActiveColumns(activeColumns);
		tempo.computeActiveState();
		tempo.calculatePredictedState();
		tempo.updateSynapses();
	}

	private void drawField() {

		graphics.setColor(Color.green);
		graphics.fillRect(20, 50, 410, 10);
		graphics.fillRect(20, 340, 410, 10);
		graphics.fillRect(220, 50, 10, 300);
		graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
		graphics.drawString(""+scoreLeft, 190, 49);
		graphics.drawString(""+scoreRight, 240, 49);
		
		repaint();
	}
	
}
