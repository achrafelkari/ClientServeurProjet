package el.entities;
import javax.swing.JOptionPane;

import el.variable.VariableDuJeu;


public class Joueur implements VariableDuJeu {

	private Long id; 
	private String nom = "";
	private Position position;

	
	
	public Joueur(boolean isClient) {
		super();
		id = System.currentTimeMillis(); // avoir un id unique
		if(isClient)
		nom = JOptionPane.showInputDialog(  // getName depuis une fennêtre
                "Entrer votre nom : "); 

	
	}
	
	
	public void setInitialPosition(boolean isClient){
		if(isClient)
			position = new Position(POSXJ1, POSYJ1);
		else
			position = new Position(POSXJ2,POSYJ2);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	
	public void deplacerHaut(){
		position.deplacerHaut();
	}
	public void deplacerBas(){
			position.deplacerBas();
		}
	
	public void deplacerGauche(){
		position.deplacerGauche();
	}
	
	public void deplacerDroite(){
		position.deplacerDroite();
	}

}
