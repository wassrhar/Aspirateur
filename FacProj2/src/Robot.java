import java.util.ArrayList;

/**
 * @author Wassim
 *
 */
public class Robot extends Thread{
	private float capaciteTotalBatterie;
	private float capaciteBatterie;
	public static int reservePoussiere;
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
	private int nbRetour;
	private int metrage;
	private Position positionBase;
	public static boolean  seCharge=false;
	public static Position positionCourante;
	private int puissance;
	private boolean aspire;
	private float consommationBase=1;
	private boolean estBloque=false;
	private ArrayList<PositionParcouru> positionParcouru;
	 
	public ArrayList<PositionParcouru> getLesPositionsParcouru(){
		return positionParcouru;
	}
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
	
	/**
	 * Permet de convertir les positions parcourus par le robot, de type PositionParcouru
	 * en type Position (utile pour l'affichage).
	 * @return ArrayList<Position>
	 */
	public Position[] vectorPourAffichage(){
		Position[] toReturn = new Position[Piece.getDimensions()[0]*Piece.getDimensions()[1]];
		for(int i=0; i<Piece.getDimensions()[0]; i++)
		{
			for(int j=0; j<Piece.getDimensions()[1]; j++)
			{
				toReturn[i*Piece.getDimensions()[1]+j] = new Position(i,j,"VV",0);
			}
			
		}
		
		for(int i=0;i<positionParcouru.size();i++){
			toReturn[positionParcouru.get(i).getX()*Piece.getDimensions()[1]+positionParcouru.get(i).getY()]=Piece.getPosition(positionParcouru.get(i).getX(), positionParcouru.get(i).getY());
		}
		
		return toReturn;
	}
	/**
	 * Methode run de la Thread ROBOT. Actions effectués :
	 * 		- Initialise le chrono
	 * 		- Affichage dans la console de la position de la base
	 * 		- Lance la fonction déplacer
	 */
	@Override
	public void run() {
		System.out.println("ROBOT : Ok");
		Chrono.Go_Chrono();
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
		System.out.println("ROBOT : Fin");
	}
	
	public void affichageUI(){
		Interface.CarteRobot.positionsToCarte(vectorPourAffichage(), Piece.getDimensions()[0]*Piece.getDimensions()[1], Piece.getDimensions()[1], Piece.getDimensions()[0]);
		Interface.CarteRobot.affichageAspirateur(positionCourante.getX(), positionCourante.getY());
		Interface.CarteRobot.repaint();
		String toDisplay=Float.toString((capaciteBatterie/capaciteTotalBatterie)*100);
		if(toDisplay.length()>4){
			toDisplay=toDisplay.substring(0, 4);
		}
		Interface.LabelValeurBatterie.setText(toDisplay);
		Interface.LabelValeurReservoir.setText(Integer.toString(etatReserve));
		Interface.LabelValeurTemps.setText(Long.toString(Chrono.Stop_Chrono()/1000));
		Interface.LabelValeurMetrage.setText(Integer.toString(metrage));
		Interface.LabelValeurRetours.setText(Integer.toString(nbRetour));
	}
	
	/**
	 * Déplace le robot dans la pièce. La fonction assure le retour à la base du robot
	 * lorsque sa réserve est pleine ou lorsqu'il dispose du minimum de batterie nécessaire au retour.
	 * 
	 * @throws InterruptedException
	 * @throws outOfEnergy
	 */
	public void deplacer() throws InterruptedException, outOfEnergy{		
		setAspire(true);
		while(!Piece.estPropre){
			setAspire(true);
			ResultatRecurrence res=determinerPlusCourtCheminBase(positionCourante.getX(),positionCourante.getY(),new ArrayList<PositionParcouru>(),positionCourante.getX(),positionCourante.getY());

			bouger();
			
			if(capaciteBatterie<consommationBase*4+calculerCout(res.getChemin())){
				setAspire(false);
				emprunterChemin(res.getChemin());
				nbRetour++;
				System.out.println("----EN ATTENTE DE LA BASE----");
				while(!seCharge){System.out.print("");}
				System.out.println("---ROBOT EN CHARGE---");
				Robot.sleep(5000);
				recharger();			
			}
			if(capaciteBatterie<calculerCout(res.getChemin())){
				throw new outOfEnergy();
			}
			if(aspire){
				aspirer();
				if(capaciteBatterie<calculerCout(res.getChemin())){
					throw new outOfEnergy();
				}
				if(capaciteBatterie<consommationBase*3+calculerCout(res.getChemin()) || etatReserve==reservePoussiere){
					setAspire(false);
					emprunterChemin(res.getChemin());
					nbRetour++;
					System.out.println("----EN ATTENTE DE LA BASE----");
					while(!seCharge){System.out.print("");}
					System.out.println("---ROBOT EN CHARGE---");
					Robot.sleep(5000);
					recharger();
				}
			}
		}
	}

	/**
	 * Fait suivre le chemin passer en paramètre au robot.
	 * @param chemin
	 * @throws InterruptedException
	 * @throws outOfEnergy
	 */
	public void emprunterChemin(ArrayList<PositionParcouru> chemin) throws InterruptedException, outOfEnergy{
		for(int i=0;i<chemin.size();i++){
			if(Piece.estAccessiblePosition(chemin.get(i).getX(), chemin.get(i).getY())){
				setPositionCourante(Piece.getPosition(chemin.get(i).getX(), chemin.get(i).getY()));
			}
		}
	}
	
	/**
	 * Réinitialise les variables du robot (simulation d'une charge) 
	 */
	private void recharger() {
		// TODO Auto-generated method stub
		etatReserve=0;
		capaciteBatterie=capaciteTotalBatterie;
	}
	
	/**
	 * Calcule le coût en batterie d'un chemin passer en paramètre.
	 * @param chemin
	 * @return la valeur calculée en float
	 */
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
	
	private int qtePoussiereNonTraite(){
		int qtePoussiere=0;
		for(int i=0;i<positionParcouru.size();i++){
			qtePoussiere=qtePoussiere+Piece.getPosition(positionParcouru.get(i).getX(), positionParcouru.get(i).getY()).getQtePoussiere();
		}
		
		return qtePoussiere;
	}
	/**Permet au robot de bouger d'une case dans son sens de direction.
	 * Si la case n'est pas accessible, change le sens de direction du robot avant de le déplacer.
	 * @throws outOfEnergy 
	 * @throws InterruptedException 
	 */
	public void bouger() throws InterruptedException, outOfEnergy{
		int currentX=positionCourante.getX();
		int currentY=positionCourante.getY();
		if(positionParcouru.size()>1){ 
			if(qtePoussiereNonTraite()>0){
				int index=-2;
				int distance=Piece.getDimensions()[0]*Piece.getDimensions()[1];
				/*
				**On cherche dans positionParcouru une position sur laquelle il reste de la saleté, la plus proche et différente de la position courante
				*/
				for(int j=0;j<positionParcouru.size();j++){
					if(!Piece.getPosition(positionParcouru.get(j).getX(), positionParcouru.get(j).getY()).equals(positionCourante)
						&&Piece.getPosition(positionParcouru.get(j).getX(),positionParcouru.get(j).getY()).getQtePoussiere()>0){
						if(distance>shortestDistanceBetweenPoints(positionCourante.getX(), positionCourante.getY(), positionParcouru.get(j).getX(), positionParcouru.get(j).getY())){
							index=j;
						}
					}
				}
				
				if(index>0){//Si on a trouvé une case qui n'est pas encore complétement nettoyé et qui a été nettoyé
					PositionParcouru aim=positionParcouru.get(index);
					boolean aChange=false;
					if(aim.getX()>positionCourante.getX()){
						if(droit.isAccessible(positionCourante)){
							currentX++;
							aChange=true;
						}
					}
					else if(aim.getY()>positionCourante.getY()){
						if(haut.isAccessible(positionCourante)){
							currentY++;
							aChange=true;
						}
					}
					if(!aChange){
						if(aim.getX()<positionCourante.getX()){
							if(gauche.isAccessible(positionCourante)){
								currentX--;
							}
						}
						else{
							if(bas.isAccessible(positionCourante)){
								currentY--;
							}
						}
					}
					Position courante=Piece.getPosition(currentX, currentY);
					PositionParcouru toAdd=new PositionParcouru(courante.getX(),courante.getY());
					setPositionCourante(courante);
					if(!positionParcouru.contains(courante)){
						positionParcouru.add(toAdd);
					}
				}else{//On explore à nouveau la pièce
					boolean access=false;
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
					if(access){		
						Position courante=Piece.getPosition(currentX, currentY);
						PositionParcouru toAdd=new PositionParcouru(courante.getX(),courante.getY());		
						if(!positionParcouru.contains(courante)){
							positionParcouru.add(toAdd);
						}
						setPositionCourante(courante);
						
					}
					else{
						findNewDirection();
						bouger();
					}
				}
				
			}
			else{
				boolean access=false;
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
				if(access){
					Position courante=Piece.getPosition(currentX, currentY);
					PositionParcouru toAdd=new PositionParcouru(courante.getX(),courante.getY());
					if(!positionParcouru.contains(courante)){
						positionParcouru.add(toAdd);
						setPositionCourante(courante);
					}else{
						if(droit.isAccessible(positionCourante) && !positionParcouru.contains(new PositionParcouru(positionCourante.getX()+1, positionCourante.getY()))){
							positionParcouru.add(new PositionParcouru(positionCourante.getX()+1, positionCourante.getY()));
							courante=Piece.getPosition(positionCourante.getX()+1, positionCourante.getY());
							setPositionCourante(courante);
							return;
						}
						if(gauche.isAccessible(positionCourante) && !positionParcouru.contains(new PositionParcouru(positionCourante.getX()-1, positionCourante.getY()))){
							positionParcouru.add(new PositionParcouru(positionCourante.getX()-1, positionCourante.getY()));
							courante=Piece.getPosition(positionCourante.getX()-1, positionCourante.getY());
							setPositionCourante(courante);
							return;
						}
						if(haut.isAccessible(positionCourante) && !positionParcouru.contains(new PositionParcouru(positionCourante.getX(), positionCourante.getY()+1))){
							positionParcouru.add(new PositionParcouru(positionCourante.getX(), positionCourante.getY()+1));
							courante=Piece.getPosition(positionCourante.getX(), positionCourante.getY()+1);
							setPositionCourante(courante);
							return;
						}
						if(bas.isAccessible(positionCourante) && !positionParcouru.contains(new PositionParcouru(positionCourante.getX(), positionCourante.getY()-1))){
							positionParcouru.add(new PositionParcouru(positionCourante.getX(), positionCourante.getY()-1));
							courante=Piece.getPosition(positionCourante.getX(), positionCourante.getY()-1);
							setPositionCourante(courante);
							return;
						}
					}
					setPositionCourante(courante);
				}
				else{
					findNewDirection();
					bouger();
				}
			}
		}
		else{ //Démarrage du robot
			
			boolean access=false;
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
				return;
			}
			else{
				Position courante=Piece.getPosition(currentX, currentY);
				PositionParcouru toAdd=new PositionParcouru(courante.getX(),courante.getY());
				setPositionCourante(courante);
				if(!positionParcouru.contains(courante)){
					positionParcouru.add(toAdd);
				}
			}
		}
	}
	/*public void bouger() throws InterruptedException, outOfEnergy{
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
			if(estBloque){
				findNewDirection();//ICI
				estBloque=false;
			}
		}
	}*/
	/**
	 * Retourne l'ensemble des positions accessibles depuis (x,y) 
	 * 
	 * @param x
	 * @param y
	 * @return Un tableau contenant l'indice des positions accessibles dans positionParcouru
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
	
	/**
	 * Calcule une distance entre 2 points
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return La valeur calculée
	 */
	private double shortestDistanceBetweenPoints(int x1, int y1, int x2, int y2){
		return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
	}
	
	/**
	 * Vérifie qu'il n'existe pas un chemin direct entre le point (x,y) et l'ensemble des points de aCheck.(càd qu'il n'y a pas de "carré")
	 * @param x
	 * @param y
	 * @param aCheck
	 * @param previousX
	 * @param previousY
	 * @return Un booléen, vrai s'il en existe, faux sinon
	 */
	private boolean existDirectPath(int x, int y, ArrayList<PositionParcouru> aCheck, int previousX, int previousY){
		boolean side=false;
		boolean bottom=false;
		for(int i=0;i<aCheck.size();i++){
			if(	(aCheck.get(i).getX()+1==x && aCheck.get(i).getY()==y) ||
					(aCheck.get(i).getX()-1==x && aCheck.get(i).getY()==y)&&
					(aCheck.get(i).getX()!=previousX || aCheck.get(i).getY()!=previousY)){
				side=true;
			}
			if((aCheck.get(i).getX()==x && aCheck.get(i).getY()+1==y) ||
					(aCheck.get(i).getX()==x && aCheck.get(i).getY()-1==y)&&
					(aCheck.get(i).getX()!=previousX || aCheck.get(i).getY()!=previousY)
				){
				bottom=true;
			}
		}
		return side&&bottom;
	}
	
	/**
	 * Détermine le plus court chemin à la base en partant du point (courantX,courantY) sachant qu'on a déja cheminMinimal.
	 * @param courantX
	 * @param courantY
	 * @param cheminMinimal
	 * @return Le chemin et un booleen indiquant si le chemin mène bien à la base. Sinon, le chemin n'est pas utilisable(Impasse).
	 * @throws InterruptedException
	 */
	private ResultatRecurrence determinerPlusCourtCheminBase(int courantX, int courantY,ArrayList<PositionParcouru> cheminMinimal,int departX, int departY) throws InterruptedException{
		int baseX=getPositionBase().getX();
		int baseY=getPositionBase().getY();
		ArrayList<Integer> nextOnes=estAccessible(courantX, courantY);
	/*	if(positionCourante.getX()==4 && positionCourante.getY()==11){
			System.out.println("Taille chem min ="+cheminMinimal.size());
			System.out.println("Position courante : ("+courantX+","+courantY+")");
			System.out.println("Next ones size = "+nextOnes.size());
			for(int i=0;i<nextOnes.size();i++){
				System.out.println("("+positionParcouru.get(nextOnes.get(i)).getX()+","+positionParcouru.get(nextOnes.get(i)).getY()+")");
			}
			System.out.print("Le chemin minimal c'est : ");
			for(int j=0;j<cheminMinimal.size();j++){
				System.out.print("("+cheminMinimal.get(j).getX()+","+cheminMinimal.get(j).getY()+")");
			}
			System.out.println("");
			System.out.println("");
			Robot.sleep(1000);
		}*/
		//System.out.println("Taille de nextOnes : "+nextOnes.size()+" pour ("+courantX+","+courantY+"), taille chemin : "+cheminMinimal.size());
		if(nextOnes.size()>0 && (courantX!=baseX || courantY!=baseY)){
			if(nextOnes.size()==1){
				if(positionParcouru.get(nextOnes.get(0)).getX()==baseX && positionParcouru.get(nextOnes.get(0)).getY()==baseY || cheminMinimal.size()==0){
					//System.out.println("Ajout de ("+courantX+","+courantY+") dans nextones==1");
					cheminMinimal.add(positionParcouru.get(getPositionParcouru(courantX, courantY)));
					PositionParcouru tempo=positionParcouru.get(nextOnes.get(0));
					return determinerPlusCourtCheminBase(tempo.getX(),tempo.getY(),cheminMinimal,departX,departY);
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
					//System.out.println("Valeur selectionné : ("+tempo.getX()+","+tempo.getY()+")");
					if(!cheminMinimal.contains(tempo)){
						if(!existDirectPath(tempo.getX(), tempo.getY(), cheminMinimal, courantX,courantY)){	
							//System.out.println("Ajout de ("+positionParcouru.get(getPositionParcouru(courantX, courantY)).getX()+","+positionParcouru.get(getPositionParcouru(courantX, courantY)).getY()+") dans while !existDirectPath avec tempo("+tempo.getX()+","+tempo.getY()+")");
						//	System.out.println("ENTREE");
							cheminMinimal.add(positionParcouru.get(getPositionParcouru(courantX, courantY)));
							ResultatRecurrence res=determinerPlusCourtCheminBase(tempo.getX(),tempo.getY(),cheminMinimal,departX,departY);
							if(res.isEstTermine()){
								return res;
							}
							else{
							//	System.out.println("ICI");
								//System.out.println("Suppresion de ("+cheminMinimal.get(cheminMinimal.size()-1).getX()+","+cheminMinimal.get(cheminMinimal.size()-1).getY()+") avec tempo("+tempo.getX()+","+tempo.getY()+")");
								cheminMinimal.remove(cheminMinimal.size()-1);
							}
						}
						else{
							//System.out.println("Suppression de ("+cheminMinimal.get(cheminMinimal.size()-1).getX()+","+cheminMinimal.get(cheminMinimal.size()-1).getY()+")");
							cheminMinimal.remove(cheminMinimal.size()-1);
							ResultatRecurrence res=determinerPlusCourtCheminBase(tempo.getX(),tempo.getY(),cheminMinimal,departX,departY);
							if(res.isEstTermine()){
								return res;
							}
							else{
							//	System.out.println("Suppression de ("+cheminMinimal.get(cheminMinimal.size()-1).getX()+","+cheminMinimal.get(cheminMinimal.size()-1).getY()+") avec tempo("+tempo.getX()+","+tempo.getY()+")");
								cheminMinimal.remove(cheminMinimal.size()-1);
							}
						}
					}
				}
				//System.out.println("("+courantX+","+courantY+") retourne faux");
				return new ResultatRecurrence(cheminMinimal, false);
			}
		}
		if(courantX==baseX && courantY==baseY){
			//System.out.println("Ajout de ("+courantX+","+courantY+") dans dernier");
			cheminMinimal.add(positionParcouru.get(getPositionParcouru(baseX, baseY)));
			return new ResultatRecurrence(cheminMinimal,true);
		}
		else{
			//System.out.println("("+courantX+","+courantY+") retourne faux");
			return new ResultatRecurrence(cheminMinimal, false);
		}
		
	}	
	
	/**
	 * Retourne l'indice de la position(x,y) dans positionParcouru
	 * @param x
	 * @param y
	 * @return l'indice
	 */
	private int getPositionParcouru(int x, int y){
		ArrayList<PositionParcouru> tab=positionParcouru;
		for(int i=0;i<tab.size();i++){
			if(tab.get(i).getX()==x && tab.get(i).getY()==y){
				return i;
			}
		}
		return -1;
	}
	/**
	 * Cherche une nouvelle direction praticable à partir de la positionCourante, l'ordre de priorité étant :
	 * 		- Haut
	 * 		- Droit
	 * 		- Gauche
	 *		- Bas
	 * La fonction ne retourne rien car elle se charge d'affecter la nouvelle direction au robot.
	 */
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

	/**
	 * Fonction simulant une aspiration du robot.
	 */
	public void aspirer(){
		int poussiereAspire=0;
		while(positionCourante.getQtePoussiere()>0 && poussiereAspire<puissance && etatReserve<reservePoussiere){
			Piece.positions[Piece.getIndexOfPosition(positionCourante.getX(), positionCourante.getY())].setQtePoussiere(positionCourante.getQtePoussiere()-1);;
			etatReserve++;
			poussiereAspire++;
		}
		System.out.println("ROBOT : Aspirer "+poussiereAspire+" pour ("+positionCourante.getX()+","+positionCourante.getY()+")" );
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
	/**
	 * Mutateur de positionCourante. Se charge d'effectuer les modifications sur la batterie en fonction du
	 * type de position sur laquelle le robot va.
	 * Si le robot dispose d'assez de batterie, la position est affiché dans la console.
	 * @param positionCourante
	 * @throws InterruptedException
	 * @throws outOfEnergy
	 */
	public void setPositionCourante(Position positionCourante) throws InterruptedException, outOfEnergy {
		Robot.sleep(100);
	
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
		metrage++;
		affichageUI();
		if(aspire){
			System.out.println("Etat de la batterie : "+capaciteBatterie+" Reservoir : "+etatReserve+"/"+reservePoussiere);
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
}
