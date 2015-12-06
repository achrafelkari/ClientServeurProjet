package el.variable;
/*Les variable du jeu */
public interface VariableDuJeu {

	public final String ADDRESS = "localhost";
	public final int PORT = 2222;
	public final int WIDTH = 705;
	public final int HEIGHT = 515;
	public final int DEPLACEMENT = 6; // c'est le déplacement du joueur! 
	public final int POSXJ1 = 0; // la position initial du joueur 1 
	public final int POSYJ1 = 0;
	public final int POSXJ2 = 650; // la positio initiale du joueur 2 
	public final int POSYJ2 = 400;
	
	public final int TEMPSDISPARITIONBALLE = 3000; // en milliseconde

	   /* Déclaration les string d'affichage !   */
	public final String waitingString = "Attente de connexion d'un joueur ";
	public final String connexionPerduString = "Connexion perdue avec l'utilisateur ";
	public final String wonString = "vous avez gagnez !";
	public final String enemyWonString = "Vous avez perdu !";
}