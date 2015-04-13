/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.*;

/**
 * Classe Piece à nettoyer, c'est aussi un thread
 * @author Sébastien
 */
public class Piece extends Thread
{
	private int taille;
    private static int longueur=0;
    private static int largeur=0;
    private String fichier;
    private String description;
    public static Position[] positions;
    public static boolean estPropre=false;
    public Piece(String fichier) 
    {
        this.fichier = fichier;
        this.taille = 0;
        this.description = "";
       
    }

    public Position getBase(){
		for(int i=0;i<positions.length;i++){
			if(positions[i].getType().equals("BB")){
				return positions[i];
			}
		}
		return null;
    }
    public int getTaille() {
        return taille;
    }
    public static int[] getDimensions(){
    	int[] toReturn=new int[2];
    	toReturn[0]=largeur;
    	toReturn[1]=longueur;
		return toReturn;    	
    }
    public void setTaille(int taille) {
        this.taille = taille;
    }
    public static boolean estAccessiblePosition(int x, int y){
    	if(positions.length>0){
    		Position p1=null;
    		for(int i=0;i<positions.length;i++){
    			if(positions[i].getX()==x && positions[i].getY()==y){
    				p1=positions[i];
    			}
    		}
    		if(p1!=null){
    			return (!p1.getType().equals("OO") && !p1.getType().equals("VV"));
    		}
    		return false;
    	}
    	return false;
    }
    public static int getIndexOfPosition(int x, int y){
		for(int i=0;i<positions.length;i++){
			if(positions[i].getX()==x && positions[i].getY()==y){
				return i;
			}
		}
		return -2;
    }
    public static Position getPosition(int x,int y){
    	Position p1=null;
		for(int i=0;i<positions.length;i++){
			if(positions[i].getX()==x && positions[i].getY()==y){
				p1=positions[i];
			}
		}
		return p1;
    }
    /*public int getIdOfPosition(int x,int y){
    	for(int i=0;i<positions.length;i++){
    		if(positions[i].getX()==x && positions[i].getY()==y){
    			return i;
    		}
    	}
    	return 0;
    }*/
    public String getFichier() {
        return fichier;
    }

    public void setFichier(String fichier) {
        this.fichier = fichier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Position[] getPositions() {
        return positions;
    }

    public void setPositions(Position[] positions) {
        Piece.positions = positions;
    }

    public int getLongueur() {
        return longueur;
    }

    public void setLongueur(int longueur) {
        Piece.longueur = longueur;
    }

    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        Piece.largeur = largeur;
    }
    
    /**
    * Charge la description de la piece contenu dans le fichier
    */
    /**
     * Charge la description de la piece contenu dans le fichier
     */
     public void chargerPiece()
     {
         BufferedReader ficTexte;
         String ligne;
         
         try 
         {
             ficTexte = new BufferedReader(new FileReader(new File(fichier)));
             while((ligne = ficTexte.readLine())!= null)
             {
                 if(longueur == 0)
                 {
                     largeur = ligne.length()/2;
                 }
                 description = description + ligne;
                 longueur++;
             }
             
             taille = longueur*largeur;
             
             ficTexte.close();
             
             if(taille != 408 || longueur != 17 || longueur != 24) 
                 throw new DimensionsException();
         } 
         catch (FileNotFoundException e) 
         {
             System.out.println(e.getMessage());
         }
         catch (IOException e) 
         {
             System.out.println(e.getMessage());
         }
         catch (DimensionsException e)
         {
             System.out.println(e.getMessage());
         }
     }
    
    /**
    * Rempli un vector de toutes les positions possibles dans la pièce
    */
    public void listerPositions()
    {
        positions = new Position[taille];
        String type;
        int qtepoussiere;
        int x,y;
        
        for(int i=0; i<taille; i++)
        {
            type = description.substring(i*2,i*2+2);
            switch(type.charAt(1))
            {
                case '9':
                    qtepoussiere = 9;
                    break;
                case '8':
                    qtepoussiere = 8;
                    break;
                case '7':
                    qtepoussiere = 7;
                    break;
                case '6':
                    qtepoussiere = 6;
                    break;
                case '5':
                    qtepoussiere = 5;
                    break;
                case '4':
                    qtepoussiere = 4;
                    break;
                case '3':
                    qtepoussiere = 3;
                    break;
                case '2':
                    qtepoussiere = 2;
                    break;
                case '1':
                    qtepoussiere = 1;
                    break;
                case '0':
                    qtepoussiere = 0;
                    break;
                default : 
                    qtepoussiere = 0;
                    break;
            }
           
            y = (int)(i/largeur);
            x = i-(largeur*y);
            positions[i] = new Position(x,y,type,qtepoussiere);
        }
        
    }
    
    @Override
    public void run()
    {
    	 this.chargerPiece();
         this.listerPositions();
    }

	@Override
	public String toString() {
		return "Piece [taille=" + taille + ", longueur=" + longueur
				+ ", largeur=" + largeur + ", fichier=" + fichier
				+ ", description=" + description + ", positions="
				+ Arrays.toString(positions) + "]";
	}
}
