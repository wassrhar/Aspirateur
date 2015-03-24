
public class Base {
	boolean robotEnCharge;
	int x,y;
	
	Base(int tx, int ty, boolean rob){
		x=tx;
		y=ty;
		robotEnCharge=rob;
	}
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isRobotEnCharge() {
		return robotEnCharge;
	}

	public void setRobotEnCharge(boolean robotEnCharge) {
		this.robotEnCharge = robotEnCharge;
	}
	
}
