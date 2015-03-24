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
                description = description + ligne + "\n";
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
        
        for(int i=0; i<taille; i++)
        {
            positions[i] = new Position(
        }
        
    }
    
    @Override
    public void run()
    {
        this.chargerPiece();
        this.listerPositions();
    }
}
