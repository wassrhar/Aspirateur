import java.util.ArrayList;


public class PositionParcouru{
	int x;
	int y;
	ArrayList<Integer> sensFait;
	ArrayList<PositionParcouru> cheminLePlusCourt;
	
	public ArrayList<PositionParcouru> getCheminLePlusCourt() {
		return cheminLePlusCourt;
	}

	public void setCheminLePlusCourt(ArrayList<PositionParcouru> cheminLePlusCourt) {
		this.cheminLePlusCourt = cheminLePlusCourt;
	}

	PositionParcouru(int x,int y){
		this.x=x;
		this.y=y;
		sensFait=new ArrayList<Integer>();
		cheminLePlusCourt=null;
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
