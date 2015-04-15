/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Classe DimensionsException est une Exception de dépassement de la taille maximale d'une pièce
 * @author Sébastien
 */
class DimensionsException extends Exception
{
    public DimensionsException()
    {
        super("Les dimensions de la pièce ne correspondent pas aux normes ! (max: 25 x 25)");
    }  
}

