/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

/**
 * synapse A data structure representing a synapse - contains a permanence value
 * and the source input index.
 *
 * @author vanrijn
 */
public class Synapse {

    private int sourceInput;
    /**
     * range 0..1
     */
    private double permanance;
    private int inputSpaceIndex;
    private final int xPos;
    private final int yPos;

    public Synapse(int inputSpaceIndex, int xPos, int yPos) {
        this.inputSpaceIndex = inputSpaceIndex;
        this.xPos = xPos;
        this.yPos = yPos;
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

    /**
     * set permanence value to d, ensure range is between 0..1
     *
     * @param d
     */
    public void setPermanance(double d) {
        this.permanance = Math.min(Math.max(d, 0), 1);
    }

    public int getInputSpaceIndex() {
        return inputSpaceIndex;
    }

    @Override
    public String toString() {
        return "Synapse inputspaceIndex=" + this.inputSpaceIndex + " perm=" + this.permanance + " input=" + this.sourceInput + " x,y:" + this.xPos + "," + this.yPos;
    }
}
