
public class Source {
	public static void main (String [] args){
		Base b1=new Base(0,0,false);
		Robot r1=new Robot(25,40,3,0,b1);
		r1.start();
	}
}
