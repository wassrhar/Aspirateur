/**
 * 
 * @author http://www.enseignement.polytechnique.fr/informatique/profs/Patrick.Gros/java/chrono.html
 *
 */
public class Chrono {
	// Fonctions pour le chronometre
	static long chrono = 0 ;

	// Lancement du chrono
	static void Go_Chrono() {
	chrono = java.lang.System.currentTimeMillis() ;
	}

	// Arret du chrono
	static long Stop_Chrono() {
	long chrono2 = java.lang.System.currentTimeMillis() ;
	long temps = chrono2 - chrono ;
	return temps;
	} 
}
