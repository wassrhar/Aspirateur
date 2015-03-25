/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotaspirateur;
import java.io.*;
import java.util.*;

/**
 * Classe Piece à nettoyer, c'est aussi un thread
 * @author Sébastien
 */
public class Piece extends Thread
{
    protected int taille;
    protected int longueur;
    protected int largeur;
    protected String fichier;
    protected String description;
    protected Position[] positions;

    public Piece(String fichier) 
    {
        this.fichier = fichier;
        this.taille = 0;
        this.longueur = 0;
        this.largeur = 0;
        this.description = "";
    }

    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }

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
        this.positions = positions;
    }

    public int getLongueur() {
        return longueur;
    }

    public void setLongueur(int longueur) {
        this.longueur = longueur;
    }

    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
    }
    
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
            if (ficTexte == null) throw new FileNotFoundException("Fichier non trouvé: " + fichier);
            
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
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println(e.getMessage());
        }
        catch (IOException e) 
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
}
