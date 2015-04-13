
public class Base extends Thread{
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

	@Override
	public void run() {
		while(!Piece.estPropre){
			try {
				if(Robot.positionCourante.getX()==x && Robot.positionCourante.getY()==y){
					Robot.seCharge=true;
				}
				else{
					Robot.seCharge=false;
				}
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void setRobotEnCharge(boolean robotEnCharge) {
		this.robotEnCharge = robotEnCharge;
	}
	
}
