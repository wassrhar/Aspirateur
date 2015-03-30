
public class Position {
	private int x;
	private int y;
	private String type;
	private int qtePoussiere;
	
	Position(int tX, int tY, String ttype, int tQtePoussiere){
		x=tX;
		y=tY;
		type=ttype;
		qtePoussiere=tQtePoussiere;
	}
	public int getQtePoussiere() {
		return qtePoussiere;
	}
	public void setQtePoussiere(int qtePoussiere) {
		this.qtePoussiere = qtePoussiere;
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
	public void afficher(){
		System.out.println("("+getX()+","+getY()+")"+getType());
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
