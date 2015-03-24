/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotaspirateur;

/**
 *
 * @author Sébastien
 */
public class Position 
{
    private int x;
    private int y;
    private String type;
    private int qtePoussiere;

    public Position(int x, int y, String type, int qtePoussiere) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.qtePoussiere = qtePoussiere;
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
    public String getType() {
            return type;
    }
    public void setType(String type) {
            this.type = type;
    }
}
