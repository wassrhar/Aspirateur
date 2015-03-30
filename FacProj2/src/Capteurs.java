
public class Capteurs {
	boolean obstacle;
	Capteurs(){
		obstacle=true;
	}
	public boolean isObstacle() {
		return obstacle;
	}

	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
	}

	public boolean isAccessible(int x,int y){
		return Piece.estAccessiblePosition(x,y);
	}
}
