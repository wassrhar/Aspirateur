import java.util.ArrayList;


public class ResultatRecurrence {
	ArrayList<PositionParcouru> chemin;
	boolean estTermine;
	
	ResultatRecurrence(ArrayList<PositionParcouru> c,boolean t){
		chemin=c;
		estTermine=t;
	}
	public ArrayList<PositionParcouru> getChemin() {
		return chemin;
	}
	public void setChemin(ArrayList<PositionParcouru> chemin) {
		this.chemin = chemin;
	}
	public boolean isEstTermine() {
		return estTermine;
	}
	public void setEstTermine(boolean estTermine) {
		this.estTermine = estTermine;
	}
}
