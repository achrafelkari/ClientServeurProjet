package el.jeu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import el.entities.Joueur;
import el.entities.Position;
import el.variable.VariableDuJeu;

public class Jeu implements Runnable , VariableDuJeu{

	
	public Long time= System.currentTimeMillis();// initialiser la variable time qui sera utilisé pour calculer le temps d'affichage de la balle 
	private JFrame frame;
	private Thread thread;
	Painter painter; // la classe painter extends d'un jPanel 
	private Socket socket; 
	
    public DataOutputStream dos; // pour écrire dans le socket
	public DataInputStream dis; // pour lire les données du réseau

	private ServerSocket serverSocket; 

   private BufferedImage j1; // l'image du joueur1
   private BufferedImage j2; // l'image du joueur2
   private BufferedImage balle;// limage de la table 
   private BufferedImage table;
        
   /** Déclaration des joueurs **/
   public Joueur joueur1;
   public Joueur joueur2;  
   
   /* Déclaration des boolean  */
   
	public boolean isClient = true; // l'instance créer est un client ou un serveur
	public boolean accepted = false; // la connexion est accepté ou pas ! 
	private boolean connexionPerdu = false; // si la connexion est perdu ou pas 
	boolean won = false; // si le joueur à gagner 
	boolean enemyWon = false; // si le joueur a perdu 
	public int errors = 0; // le nombre d'errreurs de connexion
	
	   /* Déclaration des fonts   */
	private Font font = new Font("Verdana", Font.BOLD, 32);
	private Font smallerFont = new Font("Verdana", Font.BOLD, 20);
	private Font largerFont = new Font("Verdana", Font.BOLD, 50);
	private Font petitFont = new Font("Verdana", Font.BOLD, 12);

	
	 /* Déclaration e la position de la balle, la position premiére est al"atoire !   */
	Random r = new Random();
    public  int xballe =  340; 
    public int yballe = 240;
        
    /* Constructeru du jeu    */
	public Jeu() {
		
		loadImages(); 
        painter = new Painter(this);
		painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		 /* On test s'il y a une connexion, s'il existe alors qu'il y a un serveur exécuté (ServerSocket) sinon on initialise le server   */
        if (!connect()) initializeServer();
		frame = new JFrame();
		frame.setTitle("Learn Me ! - ELKARI VS FENG ");
		frame.setContentPane(painter);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		thread = new Thread(this, "javaGame");
		thread.start();
	}

	
	public void run() {
		while (true) {
			lirePacket(); // je lis les packets de data.. input 
        if (!isClient && !accepted) {
				listenForServerRequest(); // j'attends une connexion d'un client 
			}
                        painter.repaint(); // toujour je redéssine l'affichage 
                    	
		}
	}

	void render(Graphics g) { // c'est la fonction de l'affichage 
		
		/* toujours je desine la table  */
		g.drawImage(table, 0, 0, null);
		/* le joueur1 et son nom en dessous ! */
        g.drawImage(j1, joueur1.getPosition().getX(), joueur1.getPosition().getY(), null);
        g.setFont(petitFont);
        g.drawString(joueur1.getNom(), joueur1.getPosition().getX(), joueur1.getPosition().getY()+80);
        Graphics2D g2 = (Graphics2D) g;
        
        /*si la connexion est déja été établi et qu'on la peru alors :  */
		if (accepted && connexionPerdu) {
			g.setColor(Color.RED);
			g.setFont(smallerFont);
		    g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(connexionPerduString);
			g.drawString(connexionPerduString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
			return; // je sors 
		}
		/* Si la connexion est accépter : cad deux joueur connectés ! */
		if (accepted) {
			/* Je dessine le deuxiéme joueur et son nom ! */
              g.drawImage(j2, joueur2.getPosition().getX(), joueur2.getPosition().getY(), null);
              g.drawString(joueur2.getNom(), joueur2.getPosition().getX(), joueur2.getPosition().getY()+70);

              /*Je dessine la balle  */
              g.drawImage(balle, xballe, yballe, null); 
              /* Je teste si l'un a gagné */
                if (won || enemyWon) {
				//g.setColor(Color.RED);
				g.setFont(largerFont);
				
               if (!enemyWon) {
            	   g.setColor(Color.GREEN);
					int stringWidth = g2.getFontMetrics().stringWidth(wonString);
					g.drawString(wonString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
				} else{
					g.setColor(Color.RED);
					int stringWidth = g2.getFontMetrics().stringWidth(enemyWonString);
					g.drawString(enemyWonString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
                                } 
               
			}
                
                /* sinon la connexion est non établie, alors on est dans la phase de recherche d'un joueur*/
		} else {
			g.setColor(Color.RED);
			g.setFont(font);
			 g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(waitingString);
			g.drawString(waitingString , WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
		}

	}
void sleep() throws InterruptedException{
       Thread.sleep(3000);
       

}
	/*la fonction lire Packet nous permet de lire les données passée dans la variable  "dis" */
	private void lirePacket() {
		if (errors >= 10) connexionPerdu = true; // si on a eu 10 exeption alors c'est que la connexion est perdu 

		/*Si la connexion est accepté est elle n'est pas perdu alors !  */
		if ( accepted && !connexionPerdu) {
			try {
				/* Je li les le string envoyer depuis l'autre intance */
                               String utf = dis.readUTF();
                               StringTokenizer token = new StringTokenizer(utf," ");
                               /*Je découpe le string et je modifie mes variables (mes positions) ! */
                               int j1xbis= Integer.parseInt(token.nextToken());
                               int  j1ybis= Integer.parseInt(token.nextToken());
                               int j2xbis = Integer.parseInt(token.nextToken());
                               int  j2ybis = Integer.parseInt(token.nextToken());
                               int xb = Integer.parseInt(token.nextToken());
                               int yb= Integer.parseInt(token.nextToken());
                               /* Je remodifie les positions de mon affichage*/
                               joueur1.setPosition(new Position(j1xbis, j1ybis));
                               joueur2.setPosition(new Position(j2xbis, j2ybis));
                               xballe= xb;
                               yballe= yb;
                               /* Je teste si l'un a gagner */
				            checkForEnemyCollision();
							checkForCollision();
				
			} catch (IOException e) {
				/*Si on a eu eu exception alors on incrémente erreurs  */
				e.printStackTrace();
				errors++;
			}
		}
	}
	
	/* Je cherche s'il y a des collisions de mon joueur*/
	public void checkForCollision() {
		if(isClient){
		       if(joueur1.getPosition().getX()>=xballe-20 && joueur1.getPosition().getX()<=xballe+20 
		               && joueur1.getPosition().getY()>=yballe-20 && joueur1.getPosition().getY()<=yballe+20)
		           won = true;
                }else{
                if(joueur2.getPosition().getX()>=xballe-20 && joueur2.getPosition().getX()<=xballe+20 &&
                     joueur2.getPosition().getY()>=yballe-20 && joueur2.getPosition().getY()<=yballe+20)
                   won = true;
                }
                
                System.out.println("win : "+ won);
             
		}

	/*Je cherche si mon enemmi a toucher la balle  */
	public void checkForEnemyCollision() {
	
		if(isClient){
          
                if(joueur2.getPosition().getX()>=xballe-20 && joueur2.getPosition().getX()<=xballe+20 &&
                joueur2.getPosition().getY()>=yballe-20 && joueur2.getPosition().getY()<=yballe+20)
                    enemyWon = true;
                }else{
                  if(joueur1.getPosition().getX()>=xballe-20 && joueur1.getPosition().getX()<=xballe+20 
               && joueur1.getPosition().getY()>=yballe-20 && joueur1.getPosition().getY()<=yballe+20)
                    enemyWon = true;
                }
                System.out.println("enemyWin : "+ enemyWon);
	}

	

	/* la fonction d'écoute du le serveur lancé */
	private void listenForServerRequest() {
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			accepted = true;
			System.out.println("CLIENT HAS REQUESTED TO JOIN, AND WE HAVE ACCEPTED");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* La connexion qui retourn true si il y a eu une connexion au serveur et false sinon ! */
	private boolean connect() {
		try {
			socket = new Socket(ADDRESS, PORT);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			accepted = true;
			time = System.currentTimeMillis(); // je sauvegarde le temps de connexion 
		} catch (IOException e) {
			System.out.println("Unable to connect to the address: " + ADDRESS + ":" + PORT + " | Starting a server");
			return false;
		}
		System.out.println("Successfully connected to the server.");
	
		joueur2 = new Joueur(false);
		joueur1 = new Joueur(true);		
		joueur1.setInitialPosition(true);
		joueur2.setInitialPosition(false);
		return true;
	}
	/* La fonction qui iniite le serveur */
	private void initializeServer() {
		try {
			serverSocket = new ServerSocket(PORT, 8, InetAddress.getByName(ADDRESS));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		isClient = false;
		joueur1 = new Joueur(false);
		joueur2 = new Joueur(true);
		joueur1.setInitialPosition(true);
		joueur2.setInitialPosition(false);
	}
	/* Je load les images */
	private void loadImages() {
		try {
		       	table = ImageIO.read(new File("table.png"));
		        j1 = ImageIO.read(new File("j1.png"));
                j2= ImageIO.read(new File("j2.png"));
                balle = ImageIO.read(new File("java.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Fonction main*/
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Jeu ticTacToe = new Jeu();
	}



}
