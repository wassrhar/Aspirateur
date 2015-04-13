
public class Source{
	public static void main (String [] args) throws InterruptedException{
		
		Piece p1=new Piece("piece.txt");			
		p1.start();
		p1.join();
		Base b1=new Base(p1.getBase().getX(),p1.getBase().getY(),true);
		Position base=p1.getBase();
		Robot r1=new Robot(100,40,3,1,base);
		r1.start();
		b1.start();
	}
}
