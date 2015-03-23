/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotaspirateur;
import java.io.*;

/**
 * Classe Piece à nettoyer, c'est aussi un thread
 * @author Sébastien
 */
public class Piece extends Thread
{
    protected int taille;
    protected String fichier;
    protected String description;

    public Piece(String fichier) 
    {
        this.fichier = fichier;
        this.taille = 0;
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
                description = description + ligne + "\n";
            }
            
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
    
    @Override
    public void run()
    {
        this.chargerPiece();
    }
}
