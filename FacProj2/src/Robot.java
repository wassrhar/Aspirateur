
public class Robot extends Thread{
	private float capaciteBatterie;
	private int reservePoussiere;
	private int etatReserve;
	private String directionCourante;
	private Capteurs droit;
	private Capteurs gauche;
	private Capteurs haut;
	private Capteurs bas;
	private Capteurs antiVide;
	private int cartographie;
	private String positionBase;
	private Position positionCourante;
	private int puissance;
	private boolean aspire;
	
	public boolean aspirer(){
		int poussiereAspire=0;
		while(positionCourante.getQtePoussiere()>0 && poussiereAspire<puissance){
			if(etatReserve+1<reservePoussiere){
				positionCourante.setQtePoussiere(positionCourante.getQtePoussiere()-1);
				reservePoussiere++;
				poussiereAspire++;
			}
			else{
				break;
			}
		}
		return aspire;	
	}
	public boolean isAspire() {
		return aspire;
	}
	public void setAspire(boolean aspire) {
		this.aspire = aspire;
	}
	public Position getPositionCourante() {
		return positionCourante;
	}
	public void setPositionCourante(Position positionCourante) throws InterruptedException, outOfEnergy {
		Robot.sleep(250);
		boolean noMoreEnergy=false;
		
		if(positionCourante.getType().charAt(0)=='T'){
			if(this.capaciteBatterie-1.5>0){
				this.capaciteBatterie=(float) (this.capaciteBatterie-1.5);
			}
			else{
				noMoreEnergy=true;
				throw new outOfEnergy();
			}
		}
		else{
			this.capaciteBatterie--;
		}
		
		if(aspire && !noMoreEnergy){
			if(this.capaciteBatterie-1>0){
				this.capaciteBatterie--;
			}
			else{
				noMoreEnergy=true;
				throw new outOfEnergy();
			}
		}
		
		if(!noMoreEnergy){
			this.positionCourante = positionCourante;
		}
	}	
	public float getCapaciteBatterie() {
		return capaciteBatterie;
	}
	public void setCapaciteBatterie(float capaciteBatterie) {
		this.capaciteBatterie = capaciteBatterie;
	}
	public int getReservePoussiere() {
		return reservePoussiere;
	}
	public void setReservePoussiere(int reservePoussiere) {
		this.reservePoussiere = reservePoussiere;
	}
	public String getDirectionCourante() {
		return directionCourante;
	}
	public void setDirectionCourante(String directionCourante) {
		this.directionCourante = directionCourante;
		this.capaciteBatterie--;
	}
	public Capteurs getDroit() {
		return droit;
	}
	public void setDroit(Capteurs droit) {
		this.droit = droit;
	}
	public Capteurs getGauche() {
		return gauche;
	}
	public void setGauche(Capteurs gauche) {
		this.gauche = gauche;
	}
	public Capteurs getHaut() {
		return haut;
	}
	public void setHaut(Capteurs haut) {
		this.haut = haut;
	}
	public Capteurs getBas() {
		return bas;
	}
	public void setBas(Capteurs bas) {
		this.bas = bas;
	}
	public Capteurs getAntiVide() {
		return antiVide;
	}
	public void setAntiVide(Capteurs antiVide) {
		this.antiVide = antiVide;
	}
	public int getCartographie() {
		return cartographie;
	}
	public void setCartographie(int cartographie) {
		this.cartographie = cartographie;
	}
	public String getPositionBase() {
		return positionBase;
	}
	public void setPositionBase(String positionBase) {
		this.positionBase = positionBase;
	}
	public int getPuissance() {
		return puissance;
	}
	public void setPuissance(int puissance) {
		this.puissance = puissance;
	}
}
