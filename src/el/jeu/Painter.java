package el.jeu;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;

import javax.swing.JPanel;

import el.variable.VariableDuJeu;


/*Cette classe est le content pane de notre jeu, elle implemente des écouteurs sur le clavier  */
	class Painter extends JPanel implements KeyListener , VariableDuJeu{
		private static final long serialVersionUID = 1L;
		
		Jeu jeu; 
		Long time;
		
		public Painter(Jeu jeu) {
			this.jeu = jeu;
			time= jeu.time;
			setFocusable(true);
			requestFocus();
			setBackground(Color.WHITE);
            addKeyListener(this);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			jeu.render(g);
			
		
		}

        @Override
        public void keyTyped(KeyEvent e) {
        }

        
        /*Surcharge des méthodes keyPressed  !  */
        @Override
        public void keyPressed(KeyEvent e) {
            
                
            if (jeu.accepted) {
            	
            	
            	/*je vérifie les déplacement : 
            	 * 
            	 * Si je suis le serveur alors c'est moi le joueur 1 
            	 * sinon je suis le joueur 2 ! 
            	 * 
            	 * */
                if(e.getKeyCode() == KeyEvent.VK_UP)
                    if(jeu.isClient)
                      jeu.joueur1.deplacerHaut();
                    else
                        jeu.joueur2.deplacerHaut();
                
                 if(e.getKeyCode() == KeyEvent.VK_DOWN)
                    if(jeu.isClient)
                       jeu.joueur1.deplacerBas();
                    else
                        jeu.joueur2.deplacerBas();
                
                 
                  if(e.getKeyCode() == KeyEvent.VK_LEFT)
                    if(jeu.isClient)
                        jeu.joueur1.deplacerGauche();
                    else
                        jeu.joueur2.deplacerGauche();
                  
                   if(e.getKeyCode() == KeyEvent.VK_RIGHT)
                    if(jeu.isClient)
                        jeu.joueur1.deplacerDroite();
                    else
                        jeu.joueur2.deplacerDroite();
                   

                   System.out.println("POS1 : " + jeu.joueur1.getPosition());
                   System.out.println("POS2 : " + jeu.joueur2.getPosition());
                   /* Repaint ! */
                   repaint();
	           Toolkit.getDefaultToolkit().sync();
	          
	           /* réaffichage de la balle */
	           Random r = new Random();
	           int xballe =  r.nextInt(700); 
	           int yballe =  r.nextInt(500);
	            
	           if(( System.currentTimeMillis() -time)>TEMPSDISPARITIONBALLE){
	            	jeu.xballe = xballe;
	            	jeu.yballe= yballe;
	            	time =  System.currentTimeMillis();;
	            }
	            
           try {
        	   
        	   /* c'est le bout du code le plus important!
        	    * J'envoi ma position et la position de mon ennemeie et la position de la balle à mon ennemie 
        	    *  */
                  jeu.dos.writeUTF(jeu.joueur1.getPosition().getX() + " " + jeu.joueur1.getPosition().getY() + " "+ jeu.joueur2.getPosition().getX() + " "+ jeu.joueur2.getPosition().getY()+ " "+ jeu.xballe + " " + jeu.yballe);
                  jeu.dos.flush();
                  
			} catch (IOException e1) {
				
				/*Si je peux pas envoyer les données alors qu'il y a probléme! je compte le nombre de probléme  */
				jeu.errors++;
				
				e1.printStackTrace();
						}

                    }
            
            /*je tst toujours qui a gagné  */
            jeu.checkForEnemyCollision();
            jeu.checkForCollision();
        
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

	}