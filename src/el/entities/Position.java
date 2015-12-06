package el.entities;
import el.variable.VariableDuJeu;


public class Position implements VariableDuJeu{
	/*La classe position  */
	private int x ;
	private  int y; 
	
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

	public Position(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public void deplacerHaut(){
		if(y>0)
     	y-=DEPLACEMENT;
	}
	public void deplacerBas(){
		if(y<HEIGHT-60)
		y+=DEPLACEMENT;
	}
	public void deplacerGauche(){
		if(x>0)
     	x-=DEPLACEMENT;
	}
	public void deplacerDroite(){
		if(x<WIDTH-50)
     	x+=DEPLACEMENT;
	}
}
