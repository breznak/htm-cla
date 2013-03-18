package nl.vanrijn.model.helper;

public class InputSpace implements Comparable<InputSpace>{

	@Override
	public String toString() {
		return "x="+xPos+",y="+yPos+"input="+sourceInput;
	}

	private int	xPos;

	private int	yPos;

	private int	sourceInput;

	public InputSpace(int xPos, int yPos, int sourceInput) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.sourceInput = sourceInput;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (this.xPos == ((InputSpace)obj).getxPos() && (this.yPos == ((InputSpace)obj).getyPos())
					&& this.sourceInput == ((InputSpace)obj).getSourceInput()) {
			return  true;
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
		int returnValue=-3;
		
		if(this.equals(o)){
//			System.out.println("gelijk");
			returnValue= 0;
			
		} else if((this.yPos>  o.getyPos())
				||
				(this.yPos==o.getyPos() && this.xPos>o.getxPos())
				||
				(this.yPos==o.getyPos() && this.xPos==o.getxPos() && this.sourceInput==1 && o.sourceInput==0)) {
			returnValue= -1;
		} else{
			returnValue= 1;
		}
//		if(returnValue==0){
//			System.out.println(this+"  "+o +" "+returnValue);
//		}
		return returnValue;
	}
}
