/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;


/**
 * synapse A data structure representing a synapse - contains a permanence value and the source input index.
 * 
 * @author vanrijn
 */
public class Synapse {

	private int		sourceInput;



	private double	permanance;

	private int		inputSpaceIndex;


	private final int		xPos;

	private final int		yPos;

	public Synapse(int inputSpaceIndex, int xPos, int yPos) {
		this.inputSpaceIndex=inputSpaceIndex;
		this.xPos=xPos;
		this.yPos=yPos;
	}

	public int getxPos() {
		return xPos;
	}

	

	public int getyPos() {
		return yPos;
	}

	

	public boolean isConnected(double connectedPermanance) {
		// logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+
		// "input="+sourceInput);
		return this.permanance >= connectedPermanance;
	}

	public int getSourceInput() {
		return sourceInput;
	}

	public void setSourceInput(int sourceInput) {
		this.sourceInput = sourceInput;
	}

	public double getPermanance() {
		return this.permanance;
	}

	public void setPermanance(double d) {
		this.permanance = d;
	}

	public int getInputSpaceIndex() {
		return inputSpaceIndex;
	}

	public void setInputSpaceIndex(int inputSpaceIndex) {
		this.inputSpaceIndex = inputSpaceIndex;
	}

	@Override
	public String toString() {
		return "Synapse inputspaceIndex="+this.inputSpaceIndex+" perm="+this.permanance+" input="+this.sourceInput+" x,y:"+this.xPos+","+this.yPos;
	}

}
