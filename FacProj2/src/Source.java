
public class Source{
	public static void main (String [] args) throws InterruptedException{
		
		Piece p1=new Piece("piece.txt");			
		p1.start();
		p1.join();
		Position base=p1.getBase();
		Robot r1=new Robot(250,40,3,0,base);
		r1.start();
	}
}
