/**
 * 
 * @author Wassim
 *
 */
public class Capteurs {
	boolean obstacle;
	int side;//0=Haut 1=Droit 2=Gauche 3=Bas
	Capteurs(int side){
		obstacle=true;
		this.side=side;
	}
	public boolean isObstacle() {
		return obstacle;
	}

	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
	}
	/**
	 * Selon le côté du capteurs (side) retourne si la position connexe est accessible.
	 * @param pos
	 * @return vrai si la position est accessible, faux sinon
	 */
	public boolean isAccessible(Position pos){
		switch(side){
			case 0://HAUT
				return Piece.estAccessiblePosition(pos.getX(),pos.getY()+1);
			case 1://DROIT
				return Piece.estAccessiblePosition(pos.getX()+1,pos.getY());
			case 2://GAUCHE
				return Piece.estAccessiblePosition(pos.getX()-1,pos.getY());
			case 3:
				return Piece.estAccessiblePosition(pos.getX(),pos.getY()-1);
			default:
				return false;
		}
	}
}
