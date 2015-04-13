import java.util.ArrayList;

/**
 * Classe PositionParcouru. Symbolise une position dans la pièce mais vu par le robot. Cet objet comporte :
 * 	- Les coordonnés x,y de la pièce à laquel il fait référence.
 * 	- Un ArrayList<Integer> sensfait qui contient toutes les directions déja prises par le robot à partir d'ici
 * @author Spazz
 *
 */
public class PositionParcouru{
	int x;
	int y;
	ArrayList<Integer> sensFait;

	PositionParcouru(int x,int y){
		this.x=x;
		this.y=y;
		sensFait=new ArrayList<Integer>();
	}
	
	public void ajouterSens(int sens){
		if(!sensFait.contains(sens)){
			sensFait.add(sens);
		}
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

	public ArrayList<Integer> getSensFait() {
		return sensFait;
	}

	public void setSensFait(ArrayList<Integer> sensFait) {
		this.sensFait = sensFait;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PositionParcouru other = (PositionParcouru) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	
	
	
	
}
