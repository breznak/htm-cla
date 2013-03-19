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
        if (this == obj) {
            return true;
        } else if (obj != null && obj.getClass().equals(this.getClass())
                && this.xPos == ((InputSpace) obj).xPos && (this.yPos == ((InputSpace) obj).yPos)
                && this.sourceInput == ((InputSpace) obj).sourceInput) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(InputSpace o) {
        if (this.equals(o)) {
            return 0;
        } else if ((this.yPos > o.yPos)
                || (this.yPos == o.yPos && this.xPos > o.xPos)
                || (this.yPos == o.yPos && this.xPos == o.xPos && this.sourceInput == 1 && o.sourceInput == 0)) {
            return -1;
        } else {
            return 1;
        }
    }
}
