import java.util.ArrayList;

/**
 * @author Wassim
 *
 */
public class Robot extends Thread{
	private float capaciteTotalBatterie;
	private float capaciteBatterie;
	private int reservePoussiere;
	private int etatReserve;
	private int directionCourante;
	/*
	 * 0=Haut
	 * 1=Droit
	 * 2=Gauche
	 * 3=Bas
	 */
	private Capteurs droit;
	private Capteurs gauche;
	private Capteurs haut;
	private Capteurs bas;
	private Capteurs antiVide;
	private Position positionBase;
	public static boolean  seCharge=false;
	public static Position positionCourante;
	private int puissance;
	private boolean aspire;
	private float consommationBase=1;
	private boolean estBloque=false;
	private ArrayList<PositionParcouru> positionParcouru;
	
	Robot(float cap, int reser, int puiss, int direc, Position laBase){
		droit=new Capteurs(1);
		gauche=new Capteurs(2);
		haut=new Capteurs(0);
		bas=new Capteurs(3);
		capaciteTotalBatterie=cap;
		capaciteBatterie=cap;
		reservePoussiere=reser;
		etatReserve=0;
		directionCourante=direc;
		//Cartographie
		//Capteurs
		puissance=puiss;
		aspire=false;
		positionBase=laBase;
		positionCourante=laBase;
		if(direc>=0 && direc<4){
			directionCourante=direc;
		}
		else{
			directionCourante=0;
		}
		positionParcouru=new ArrayList<PositionParcouru>();
	}
	
	public ArrayList<Position> vectorPourAffichage(){
		ArrayList<Position> toReturn = null;
		for(int i=0;i<positionParcouru.size();i++){
			toReturn.add(Piece.getPosition(positionParcouru.get(i).getX(), positionParcouru.get(i).getY()));
		}
		return toReturn;
	}
	@Override
	public String toString() {
		return "Robot [capaciteBatterie=" + capaciteBatterie
				+ ", reservePoussiere=" + reservePoussiere + ", etatReserve="
				+ etatReserve + ", directionCourante=" + directionCourante
				+ ", droit=" + droit + ", gauche=" + gauche + ", haut=" + haut
				+ ", bas=" + bas + ", antiVide=" + antiVide + ", positionCourante=" + positionCourante
				+ ", puissance=" + puissance + ", aspire=" + aspire
				+ ", consommationBase=" + consommationBase + "]";
	}

	@Override
	public void run() {
		System.out.println("Depart de ("+positionBase.getX()+","+positionBase.getY()+")");
		PositionParcouru deLaBase=new PositionParcouru(positionCourante.getX(),positionCourante.getY());
		positionParcouru.add(deLaBase);
		try {
			this.deplacer();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (outOfEnergy e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void emprunterChemin(ArrayList<PositionParcouru> chemin) throws InterruptedException, outOfEnergy{
		for(int i=0;i<chemin.size();i++){
			if(Piece.estAccessiblePosition(chemin.get(i).getX(), chemin.get(i).getY())){
				setPositionCourante(Piece.getPosition(chemin.get(i).getX(), chemin.get(i).getY()));
			}
		}
	}
	
	public void deplacer() throws InterruptedException, outOfEnergy{
		setAspire(true);
		int nbMove=100;
		while(nbMove>0){
			/*System.out.println("----------------------------------------");
			System.out.println("Le plus court chemin à partir de ("+positionCourante.getX()+","+positionCourante.getY()+") est le chemin : ");*/
			ResultatRecurrence res=determinerPlusCourtCheminBase(positionCourante.getX(),positionCourante.getY(),new ArrayList<PositionParcouru>());
			
		/*	for(int i=0;i<res.getChemin().size();i++){
				System.out.println("("+res.getChemin().get(i).getX()+","+res.getChemin().get(i).getY()+")");
			}*/
		/*	System.out.println("Cout du chemin : "+calculerCout(res.getChemin()));
			System.out.println("----------------------------------------");*/
			bouger();
			if(capaciteBatterie<calculerCout(res.getChemin())){
				throw new outOfEnergy();
			}
			if(capaciteBatterie<consommationBase*3+calculerCout(res.getChemin())){
				setAspire(false);
				emprunterChemin(res.getChemin());
				while(!seCharge){}
				Robot.sleep(5000);
			}
			if(aspire){
				aspirer();
				if(capaciteBatterie<calculerCout(res.getChemin())){
					throw new outOfEnergy();
				}
				if(capaciteBatterie<consommationBase*3+calculerCout(res.getChemin())){
					setAspire(false);
					emprunterChemin(res.getChemin());
					while(!seCharge){}
					System.out.println("---ROBOT EN CHARGE---");
					Robot.sleep(5000);
				}
				nbMove--;
			}
		}
	}
	private float calculerCout(ArrayList<PositionParcouru> chemin){
		float current=0;
		for(int i=0;i<chemin.size();i++){
			Position part=Piece.getPosition(chemin.get(i).getX(), chemin.get(i).getY());
			if(part.getType().substring(0, 1).equals("T")){
				current=(float) (current+(consommationBase*1.5));
			}
			else if(part.getType().substring(0,1).equals("0")){
				current=current+consommationBase;
			}
		}
		return current;
	}
	/**Permet au robot de bouger d'une case dans son sens de direction.
	 * Si la case n'est pas accessible, change le sens de direction du robot avant de le déplacer.
	 * @throws outOfEnergy 
	 * @throws InterruptedException 
	 */
	public void bouger() throws InterruptedException, outOfEnergy{
		boolean access=true;
		int currentX=positionCourante.getX();
		int currentY=positionCourante.getY();
		switch(directionCourante){
			case 0:
				access=haut.isAccessible(positionCourante);
				currentY++;
				break;
			case 1:
				access=droit.isAccessible(positionCourante);
				currentX++;
				break;
			case 2:
				access=gauche.isAccessible(positionCourante);
				currentX--;
				break;
			case 3:
				access=bas.isAccessible(positionCourante);
				currentY--;
				break;
		}
		if(!access){
			findNewDirection();
			bouger();
		}
		else{
			Position courante=Piece.getPosition(currentX, currentY);
			setPositionCourante(courante);
			PositionParcouru toAdd=new PositionParcouru(courante.getX(),courante.getY());
			if(!positionParcouru.contains(toAdd)){
				positionParcouru.add(toAdd);			
			}
			//positionCourante.afficher();
			if(estBloque){
				findNewDirection();//ICI
				estBloque=false;
			}
		}
	}
	/**
	 * Retourner l'indice dans positionParcouru des positions qui sont
	 * directement accessible depuis la case passé en paramètre.
	 */
	private ArrayList<Integer> estAccessible(int x,int y){
		ArrayList<Integer> toReturn=new ArrayList<Integer>();
		int monX=x;
		int monY=y;
		int resultat=getPositionParcouru(monX+1, monY);
		if(resultat>=0){
			toReturn.add(resultat);
		}
		resultat=getPositionParcouru(monX,monY+1);
		if(resultat>=0){
			toReturn.add(resultat);
		}
		resultat=getPositionParcouru(monX-1, monY);
		if(resultat>=0){
			toReturn.add(resultat);
		}
		resultat=getPositionParcouru(monX,monY-1);
		if(resultat>=0){
			toReturn.add(resultat);
		}
		return toReturn;
	}
	
	private double shortestDistanceBetweenPoints(int x1, int y1, int x2, int y2){
		return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
	}
	private boolean existDirectPath(int x, int y, ArrayList<PositionParcouru> aCheck, int previousX, int previousY){
		for(int i=0;i<aCheck.size();i++){
			if(	(aCheck.get(i).getX()+1==x && aCheck.get(i).getY()==y) ||
				(aCheck.get(i).getX()-1==x && aCheck.get(i).getY()==y) ||
				(aCheck.get(i).getX()==x && aCheck.get(i).getY()+1==y) ||
				(aCheck.get(i).getX()==x && aCheck.get(i).getY()-1==y)&&
				(aCheck.get(i).getX()!=previousX || aCheck.get(i).getY()!=previousY)
				){
				return true;
			}
		}
		return false;
	}
	private ResultatRecurrence determinerPlusCourtCheminBase(int courantX, int courantY,ArrayList<PositionParcouru> cheminMinimal) throws InterruptedException{
		int baseX=getPositionBase().getX();
		int baseY=getPositionBase().getY();
		ArrayList<Integer> nextOnes=estAccessible(courantX, courantY);
		Thread.sleep(100);
		//System.out.println("Taille de nextOnes : "+nextOnes.size()+" pour ("+courantX+","+courantY+"), taille chemin : "+cheminMinimal.size());
		if(nextOnes.size()>0 && (courantX!=baseX || courantY!=baseY)){
			if(nextOnes.size()==1){
				if(positionParcouru.get(nextOnes.get(0)).getX()==baseX && positionParcouru.get(nextOnes.get(0)).getY()==baseY || cheminMinimal.size()==0){
					//System.out.println("Ajout de ("+courantX+","+courantY+") dans nextones==1");
					cheminMinimal.add(positionParcouru.get(getPositionParcouru(courantX, courantY)));
					PositionParcouru tempo=positionParcouru.get(nextOnes.get(0));
					return determinerPlusCourtCheminBase(tempo.getX(),tempo.getY(),cheminMinimal);
				}
				else{
					return new ResultatRecurrence(cheminMinimal, false);
				}
			}
			else{
				int mini=Piece.getDimensions()[0]*Piece.getDimensions()[1];
				while(nextOnes.size()>0){
					double minimum=mini;
					int aTest=0;
					int maValue=0;
					for(int j=0;j<nextOnes.size();j++){//DETERMINE LE POINT LE PLUS PROCHE DE LA BASE POUR LE TEST
						PositionParcouru pos=positionParcouru.get(nextOnes.get(j));
						double test=shortestDistanceBetweenPoints(pos.getX(), pos.getY(), baseX, baseY);
						if(test<minimum){
							minimum=test;
							aTest=j;
							maValue=nextOnes.get(j);
						}
					}
					
					nextOnes.remove(aTest);//ON LE SUPPRIME DES POINTS A TESTER
					PositionParcouru tempo=positionParcouru.get(maValue);
					if(!cheminMinimal.contains(tempo)){
						if(!existDirectPath(tempo.getX(), tempo.getY(), cheminMinimal, courantX,courantY)){	
							//System.out.println("Ajout de ("+positionParcouru.get(getPositionParcouru(courantX, courantY)).getX()+","+positionParcouru.get(getPositionParcouru(courantX, courantY)).getY()+") dans while !existDirectPath avec tempo("+tempo.getX()+","+tempo.getY()+")");
							cheminMinimal.add(positionParcouru.get(getPositionParcouru(courantX, courantY)));
							ResultatRecurrence res=determinerPlusCourtCheminBase(tempo.getX(),tempo.getY(),cheminMinimal);
							if(res.isEstTermine()){
								return res;
							}
							else{
								//System.out.println("Suppresion de ("+cheminMinimal.get(cheminMinimal.size()-1).getX()+","+cheminMinimal.get(cheminMinimal.size()-1).getY()+") avec tempo("+tempo.getX()+","+tempo.getY()+")");
								cheminMinimal.remove(cheminMinimal.size()-1);
							}
						}
						else{
							//System.out.println("Suppresion de ("+cheminMinimal.get(cheminMinimal.size()-1).getX()+","+cheminMinimal.get(cheminMinimal.size()-1).getY()+")");
							cheminMinimal.remove(cheminMinimal.size()-1);
							ResultatRecurrence res=determinerPlusCourtCheminBase(tempo.getX(),tempo.getY(),cheminMinimal);
							if(res.isEstTermine()){
								return res;
							}
							else{
								//System.out.println("Suppresion de ("+cheminMinimal.get(cheminMinimal.size()-1).getX()+","+cheminMinimal.get(cheminMinimal.size()-1).getY()+") avec tempo("+tempo.getX()+","+tempo.getY()+")");
								cheminMinimal.remove(cheminMinimal.size()-1);
							}
						}
					}
				}
				return new ResultatRecurrence(cheminMinimal, false);
			}
		}
		if(courantX==baseX && courantY==baseY){
			//System.out.println("Ajout de ("+courantX+","+courantY+") dans dernier");
			cheminMinimal.add(positionParcouru.get(getPositionParcouru(baseX, baseY)));
			return new ResultatRecurrence(cheminMinimal,true);
		}
		else{
			return new ResultatRecurrence(cheminMinimal, false);
		}
		
	}
	/*private ResultatRecurrence determinerPlusCourtCheminBase(int courantX, int courantY,ArrayList<PositionParcouru> cheminMinimal) throws InterruptedException{
		int baseX=getPositionBase().getX();
		int baseY=getPositionBase().getY();
		ArrayList<Integer> nextOnes=estAccessible(courantX, courantY);
		Thread.sleep(100);
		//System.out.println("Taille de nextOnes : "+nextOnes.size()+" pour ("+courantX+","+courantY+"), taille chemin : "+cheminMinimal.size());
		if(nextOnes.size()>0 && (courantX!=baseX || courantY!=baseY)){
			if(nextOnes.size()==1){
				if(positionParcouru.get(nextOnes.get(0)).getX()==baseX && positionParcouru.get(nextOnes.get(0)).getY()==baseY || cheminMinimal.size()==0){
					//System.out.println("Ajout de ("+courantX+","+courantY+") dans nextones==1");
					cheminMinimal.add(positionParcouru.get(getPositionParcouru(courantX, courantY)));
					PositionParcouru tempo=positionParcouru.get(nextOnes.get(0));
					return determinerPlusCourtCheminBase(tempo.getX(),tempo.getY(),cheminMinimal);
				}
				else{
					return new ResultatRecurrence(cheminMinimal, false);
				}
			}
			else{
				int mini=Piece.getDimensions()[0]*Piece.getDimensions()[1];
				while(nextOnes.size()>0){
					double minimum=mini;
					int aTest=0;
					int maValue=0;
					for(int j=0;j<nextOnes.size();j++){//DETERMINE LE POINT LE PLUS PROCHE DE LA BASE POUR LE TEST
						PositionParcouru pos=positionParcouru.get(nextOnes.get(j));
						double test=shortestDistanceBetweenPoints(pos.getX(), pos.getY(), baseX, baseY);
						if(test<minimum){
							minimum=test;
							aTest=j;
							maValue=nextOnes.get(j);
						}
					}
					
					nextOnes.remove(aTest);//ON LE SUPPRIME DES POINTS A TESTER
					PositionParcouru tempo=positionParcouru.get(maValue);
					if(!cheminMinimal.contains(tempo)){
						if(!existDirectPath(tempo.getX(), tempo.getY(), cheminMinimal, courantX,courantY)){	
							//System.out.println("Ajout de ("+positionParcouru.get(getPositionParcouru(courantX, courantY)).getX()+","+positionParcouru.get(getPositionParcouru(courantX, courantY)).getY()+") dans while !existDirectPath avec tempo("+tempo.getX()+","+tempo.getY()+")");
							cheminMinimal.add(positionParcouru.get(getPositionParcouru(courantX, courantY)));
							ResultatRecurrence res=determinerPlusCourtCheminBase(tempo.getX(),tempo.getY(),cheminMinimal);
							if(res.isEstTermine()){
								return res;
							}
							else{
								//System.out.println("Suppresion de ("+cheminMinimal.get(cheminMinimal.size()-1).getX()+","+cheminMinimal.get(cheminMinimal.size()-1).getY()+") avec tempo("+tempo.getX()+","+tempo.getY()+")");
								cheminMinimal.remove(cheminMinimal.size()-1);
							}
						}
						else{
							//System.out.println("Suppresion de ("+cheminMinimal.get(cheminMinimal.size()-1).getX()+","+cheminMinimal.get(cheminMinimal.size()-1).getY()+")");
							cheminMinimal.remove(cheminMinimal.size()-1);
							ResultatRecurrence res=determinerPlusCourtCheminBase(tempo.getX(),tempo.getY(),cheminMinimal);
							if(res.isEstTermine()){
								return res;
							}
							else{
								//System.out.println("Suppresion de ("+cheminMinimal.get(cheminMinimal.size()-1).getX()+","+cheminMinimal.get(cheminMinimal.size()-1).getY()+") avec tempo("+tempo.getX()+","+tempo.getY()+")");
								cheminMinimal.remove(cheminMinimal.size()-1);
							}
						}
					}
				}
				return new ResultatRecurrence(cheminMinimal, false);
			}
		}
		if(courantX==baseX && courantY==baseY){
			//System.out.println("Ajout de ("+courantX+","+courantY+") dans dernier");
			cheminMinimal.add(positionParcouru.get(getPositionParcouru(baseX, baseY)));
			return new ResultatRecurrence(cheminMinimal,true);
		}
		else{
			return new ResultatRecurrence(cheminMinimal, false);
		}
		
	}
	*/
	
	
	
	private int getPositionParcouru(int x, int y){
		for(int i=0;i<positionParcouru.size();i++){
			if(positionParcouru.get(i).getX()==x && positionParcouru.get(i).getY()==y){
				return i;
			}
		}
		return -1;
	}
	private void findNewDirection() {
		int courante=getPositionParcouru(positionCourante.getX(),positionCourante.getY());
		if(courante<0){
			PositionParcouru toAdd=new PositionParcouru(positionCourante.getX(),positionCourante.getY());
			positionParcouru.add(toAdd);
			courante=getPositionParcouru(positionCourante.getX(),positionCourante.getY());
		}
		for(int i=0;i<4;i++){
			switch(i){
				case 0:
					if(haut.isAccessible(positionCourante) && !positionParcouru.get(courante).getSensFait().contains(i)){
						setDirectionCourante(i);
						positionParcouru.get(courante).ajouterSens(i);
						return;
					}
					break;
				case 1:
					if(droit.isAccessible(positionCourante) && !positionParcouru.get(courante).getSensFait().contains(i)){
						setDirectionCourante(i);
						positionParcouru.get(courante).ajouterSens(i);
						return;
					}
					break;
				case 2:
					if(gauche.isAccessible(positionCourante) && !positionParcouru.get(courante).getSensFait().contains(i)){
						setDirectionCourante(i);
						positionParcouru.get(courante).ajouterSens(i);
						return;
					}
					break;
				case 3:
					if(bas.isAccessible(positionCourante) && !positionParcouru.get(courante).getSensFait().contains(i)){
						setDirectionCourante(i);
						positionParcouru.get(courante).ajouterSens(i);
						return;
					}
					break;
			}
		}
		/*
		 * Code atteint uniquement lorsque toutes les directions
		 * possibles ont été explorés. Le robot repart alors en
		 * sens inverse.
		 */
		switch(directionCourante){
			case 0:
				if(!estBloque){
					setDirectionCourante(3);
				}
				else{
					if(gauche.isAccessible(positionCourante)){setDirectionCourante(2);}
					else if(droit.isAccessible(positionCourante)){setDirectionCourante(1);}
					else if(haut.isAccessible(positionCourante)){setDirectionCourante(0);}
					else{setDirectionCourante(3);}
				}
				break;
			case 1:
				if(!estBloque){
					setDirectionCourante(2);
				}
				else{
					if(bas.isAccessible(positionCourante)){setDirectionCourante(3);}
					else if(haut.isAccessible(positionCourante)){setDirectionCourante(0);}
					else if(droit.isAccessible(positionCourante)){setDirectionCourante(1);}
					else{setDirectionCourante(2);}
				}
				break;
			case 2:
				if(!estBloque){
					setDirectionCourante(1);
				}
				else{
					if(bas.isAccessible(positionCourante)){setDirectionCourante(3);}
					else if(haut.isAccessible(positionCourante)){setDirectionCourante(0);}
					else if(gauche.isAccessible(positionCourante)){setDirectionCourante(2);}
					else{setDirectionCourante(1);}
				}
				break;
			case 3:
				if(!estBloque){
					setDirectionCourante(0);
				}
				else{
					if(gauche.isAccessible(positionCourante)){setDirectionCourante(2);}
					else if(droit.isAccessible(positionCourante)){setDirectionCourante(1);}
					else if(bas.isAccessible(positionCourante)){setDirectionCourante(3);}
					else{setDirectionCourante(0);}
				}
				break;
		}
		estBloque=true;
	}

	public void aspirer(){
		int poussiereAspire=0;
		while(positionCourante.getQtePoussiere()>0 && poussiereAspire<puissance && etatReserve<reservePoussiere){
			Piece.positions[Piece.getIndexOfPosition(positionCourante.getX(), positionCourante.getY())].setQtePoussiere(positionCourante.getQtePoussiere()-1);;
			etatReserve++;
			poussiereAspire++;
		}
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
	
		if(positionCourante.getType().charAt(0)=='T'){
			if(this.capaciteBatterie-consommationBase*1.5>0){
				this.capaciteBatterie=(float) (this.capaciteBatterie-consommationBase*1.5);
			}
			else{
				throw new outOfEnergy();
			}
		}
		else{
			this.capaciteBatterie--;
		}	
		if(aspire){
			if(capaciteBatterie-1>0){
				capaciteBatterie--;
			}
			else{
				throw new outOfEnergy();
			}
		}
		Robot.positionCourante = positionCourante;
		positionCourante.afficher();
		System.out.println("Etat de la batterie : "+capaciteBatterie+" Reservoir : "+etatReserve+"/"+reservePoussiere);
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
	public int getDirectionCourante() {
		return directionCourante;
	}
	public void setDirectionCourante(int directionCourante) {
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

	public int getPuissance() {
		return puissance;
	}
	public void setPuissance(int puissance) {
		this.puissance = puissance;
	}

	public Position getPositionBase() {
		return positionBase;
	}

	public void setPositionBase(Position positionBase) {
		this.positionBase = positionBase;
	}
	public float getCapaciteTotalBatterie() {
		return capaciteTotalBatterie;
	}

	public void setCapaciteTotalBatterie(float capaciteTotalBatterie) {
		this.capaciteTotalBatterie = capaciteTotalBatterie;
	}
}
