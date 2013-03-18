package nl.vanrijn.model.helper;

public class InputSpace implements Comparable<InputSpace> {

    private int xPos;
    private int yPos;
    private int sourceInput;

    public InputSpace(int xPos, int yPos, int sourceInput) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.sourceInput = sourceInput;
    }

    @Override
    public String toString() {
        return "x=" + xPos + ",y=" + yPos + "input=" + sourceInput;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(this.xPos == ((InputSpace) obj).getxPos() && (this.yPos == ((InputSpace) obj).getyPos())
                && this.sourceInput == ((InputSpace) obj).getSourceInput()) {
            return true;
        }
        return false;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getSourceInput() {
        return sourceInput;
    }

    public void setSourceInput(int sourceInput) {
        this.sourceInput = sourceInput;
    }

    public int compareTo(InputSpace o) {
        if(this.equals(o)) {
            return 0;
        } else if((this.yPos > o.getyPos())
                || (this.yPos == o.getyPos() && this.xPos > o.getxPos())
                || (this.yPos == o.getyPos() && this.xPos == o.getxPos() && this.sourceInput == 1 && o.sourceInput == 0)) {
            return -1;
        } else {
            return 1;
        }
    }
}