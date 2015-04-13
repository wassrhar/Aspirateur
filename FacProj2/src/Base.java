/**
 * 
 * @author Wassim
 *
 */
public class Base extends Thread{

	int x,y;
	
	Base(int tx, int ty, boolean rob){
		x=tx;
		y=ty;
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

	/**
	 * Méthode run de la Thread Base. Envoie un signal au robot lorsque celui est sur la base (et peut se recharger).
	 */
	@Override
	public void run() {
		System.out.println("BASE : Ok");
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
		System.out.println("BASE : Fin");
	}
	
}
