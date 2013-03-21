/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vanrijn.model;

public class Synapse extends SynapseAbstract {

    private int sourceInput;
    private int inputSpaceIndex;
    private final int xPos;
    private final int yPos;

    public Synapse(int inputSpaceIndex, int xPos, int yPos, double initPerm) {
        super(initPerm);
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

    public int getSourceInput() {
        return sourceInput;
    }

    public void setSourceInput(int sourceInput) {
        this.sourceInput = sourceInput;
    }

    public int getInputSpaceIndex() {
        return inputSpaceIndex;
    }

    @Override
    public String toString() {
        return "Synapse inputspaceIndex=" + this.inputSpaceIndex + " perm=" + getPermanance() + " input=" + this.sourceInput + " x,y:" + this.xPos + "," + this.yPos;
    }
}
