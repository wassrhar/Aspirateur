/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author S�bastien
 */
class DimensionsException extends Exception
{
    public DimensionsException()
    {
        super("Les dimensions de la pi�ce ne correspondent pas aux normes ! (max: 25 x 25)");
    }  
}
