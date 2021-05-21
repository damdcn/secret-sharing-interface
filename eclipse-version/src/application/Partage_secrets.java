package application;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.nio.file.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import org.w3c.dom.css.RGBColor;
import java.awt.Color;
import java.util.Collections;

public class Partage_secrets {

    private ArrayList<Coordonnee_visage> coordonnee_visages;
    private int nombre_de_personne;
    private int seuil_de_revelation;

    private ArrayList<Integer> personnes_revelees;

    private boolean images_personnes_crees;

    private final int val_max = 251;
    private final boolean affichage = true;

    public Partage_secrets(ArrayList<Coordonnee_visage> cv, int nb_p, int seuil){
        coordonnee_visages = cv;
        nombre_de_personne = nb_p;
        seuil_de_revelation = seuil;
        personnes_revelees = new ArrayList<Integer>();
        images_personnes_crees = false;
    }

    //Methode de construction du polynome
    public static PolynomeInt cons_poly(int nb_p, int degre_max, int secret, int val_max){
        int[] coefficients = new int[degre_max];

        if(secret>=251){
            coefficients[0] = 250;
        }
        else{
            coefficients[0] = secret;
        }
        

        for(int i=1; i<degre_max; i++){
            coefficients[i] = (int)(Math.random()*(val_max-1));
        }

        return new PolynomeInt(coefficients);
    }

    public int creation_images_secretes(){

        if(images_personnes_crees){
            return 0;
        }

        File fichiers[] = new File[nombre_de_personne+1];
        BufferedImage images[] = new BufferedImage[nombre_de_personne+1];
        //Création des n photos en plus
        for(int i=0; i<nombre_de_personne+1;i++){
            if(i!=0){
                dupliquer("images/0.png", "images/"+i+".png");
            }
            
            try{
                fichiers[i] = new File("images/"+i+".png");
                images[i] = ImageIO.read(fichiers[i]);
            }
            catch(IOException e){
                e.printStackTrace();
                return 0;
            }
        } 
        
        int compteur_visage = 1;


        //Pour chaque visage sur la photo
        for(Coordonnee_visage visage : coordonnee_visages){
            if(affichage){
                System.out.print("-- Cryptage du visage "+compteur_visage+"...");
            }
            

            //Pour chaque pixel du visage
            for(int x = visage.getHG_X(); x<visage.getBD_X(); x++){
                for(int y = visage.getHG_Y(); y<visage.getBD_Y(); y++){

                    //On récupère la couleur du pixel sous forme d'un int, ce sera notre "secret"
                    Color pix_color = new Color(images[0].getRGB(x, y));
                    int R = pix_color.getRed();
                    int G = pix_color.getGreen();
                    int B = pix_color.getBlue();

                    //On crée le polynome correspondant à ce pixel
                    
                    PolynomeInt polynome_R = cons_poly(nombre_de_personne, seuil_de_revelation+1, R, val_max);
                    PolynomeInt polynome_G = cons_poly(nombre_de_personne, seuil_de_revelation+1, G, val_max);
                    PolynomeInt polynome_B = cons_poly(nombre_de_personne, seuil_de_revelation+1, B, val_max);

                    //Attribution des clefs/pixel crypté à chaque personne pour chaque RGB
                    int[] clefs_R = new int[nombre_de_personne+1];
                    int[] clefs_G = new int[nombre_de_personne+1];
                    int[] clefs_B = new int[nombre_de_personne+1];
                    for(int i=0; i<nombre_de_personne+1; i++){
                        clefs_R[i] = polynome_R.calcul_mod((i+1),val_max);
                        clefs_B[i] = polynome_B.calcul_mod((i+1),val_max);
                        clefs_G[i] = polynome_G.calcul_mod((i+1),val_max);
                    }

                    //On modifie pour chaque personne l'image correspondante avec sa clef, sauf si le visage traité est actuellement celui de la personne
                    for(int i=0; i<nombre_de_personne+1;i++){
                        if(i!=compteur_visage){
                            Color new_pixel_color = new Color(clefs_R[i],clefs_G[i],clefs_B[i]);
                            images[i].setRGB(x, y, new_pixel_color.getRGB());                     
                        }
                    }

                }
            }
            compteur_visage++;
            if(affichage){
                System.out.println("OK--");
            }
        }
        for(int i=0; i<nombre_de_personne+1;i++){
            try{
                ImageIO.write(images[i], "png",fichiers[i]);
            }
            catch(IOException e){
                e.printStackTrace();
                return 0;
            }
        }

        this.personnes_revelees.add(0);
        this.images_personnes_crees = true;
        return 1;
    }

    public int reveal(int personne){
        if(personnes_revelees.contains(personne)){
            return 0;
        }
        personnes_revelees.add(personne);
        File fichier_personne_revelee, fichier_base;
        BufferedImage image_personne_revelee, image_base;

        try{
            fichier_personne_revelee = new File("images/"+personne+".png");
            image_personne_revelee = ImageIO.read(fichier_personne_revelee);

            fichier_base = new File("images/0.png");
            image_base = ImageIO.read(fichier_base);
        }
        catch(IOException e){
            e.printStackTrace();
            return 0;
        }

        //On révèle le visage de la personne sur l'image de base
        for(int x=coordonnee_visages.get(personne-1).getHG_X(); x<coordonnee_visages.get(personne-1).getBD_X(); x++){
            for(int y=coordonnee_visages.get(personne-1).getHG_Y(); y<coordonnee_visages.get(personne-1).getBD_Y(); y++){
                int pixel_cache = image_personne_revelee.getRGB(x, y);
                image_base.setRGB(x, y, image_personne_revelee.getRGB(x, y));
            }
        }
        try{
            ImageIO.write(image_base, "png",fichier_base);
        }
        catch(IOException e){
            e.printStackTrace();
            return 0;
        }

        //On regarde si assez de personne ont révélé leur visage
        if(personnes_revelees.size()>=seuil_de_revelation+1){
            for(int v=0; v<coordonnee_visages.size();v++){
                Coordonnee_visage visage = coordonnee_visages.get(v);

                //on regarde si le visage n'a pas déjà été révélé
                if(!personnes_revelees.contains(v+1)){
                    if(affichage){
                        System.out.println("On révèle le visage "+(v+1)+". Coord visage"+coordonnee_visages.get(v).getHG_X()+","+coordonnee_visages.get(v).getHG_Y());
                    }
                    
                    //On calcul les inverses modulaires en avance pour gagner du temps
                    int[][] inverse_modulaires = new int[nombre_de_personne+2][nombre_de_personne+2];
                    for(int i=0; i<=nombre_de_personne+1;i++){
                        for(int j=0; j<=nombre_de_personne+1; j++){
                            if(i!=j){
                                inverse_modulaires[i][j] = inverse_mod(positive_mod(i-j,val_max),val_max);
                            }
                            else{
                                inverse_modulaires[i][j] = 0;
                            }
                        }
                    }

                    File fichiers[] = new File[nombre_de_personne+1];
                    BufferedImage images[] = new BufferedImage[nombre_de_personne+1];

                    for(int personne_revelee : personnes_revelees){
                        try{
                            fichiers[personne_revelee] = new File("images/"+personne_revelee+".png");
                            images[personne_revelee] = ImageIO.read(fichiers[personne_revelee]);
                        }
                        catch(IOException e){
                            e.printStackTrace();
                            return 0;
                        }
                    }

                    //On commence à révéler le visage pixel par pixel
                    int nombre_de_pixel_a_traite = (coordonnee_visages.get(v).getBD_X() - coordonnee_visages.get(v).getHG_X())*(coordonnee_visages.get(v).getBD_Y() - coordonnee_visages.get(v).getHG_Y());
                    int p = 0;
                    for(int x=coordonnee_visages.get(v).getHG_X(); x<coordonnee_visages.get(v).getBD_X(); x++){
                        for(int y=coordonnee_visages.get(v).getHG_Y(); y<coordonnee_visages.get(v).getBD_Y(); y++){
                            //System.out.print("Pixel ("+x+","+y+")...\n");
                            Lagrange l_R = new Lagrange();
                            Lagrange l_G = new Lagrange();
                            Lagrange l_B = new Lagrange();
                            for(int personne_revelee : personnes_revelees){
                                Color pixel_cache_color = new Color(images[personne_revelee].getRGB(x, y));
                                int R = pixel_cache_color.getRed();
                                int G = pixel_cache_color.getGreen();
                                int B = pixel_cache_color.getBlue();
                                l_R.addPoint(new Point(personne_revelee+1, R));
                                l_G.addPoint(new Point(personne_revelee+1, G));
                                l_B.addPoint(new Point(personne_revelee+1, B));
                            }

                            PolynomeInt polynome_pixel_R = l_R.interpolation_int_mod(val_max,inverse_modulaires);
                            PolynomeInt polynome_pixel_G = l_G.interpolation_int_mod(val_max,inverse_modulaires);
                            PolynomeInt polynome_pixel_B = l_B.interpolation_int_mod(val_max,inverse_modulaires);

                            int pixel_trouve_int_R = polynome_pixel_R.calcul_mod(0,val_max);
                            int pixel_trouve_int_G = polynome_pixel_G.calcul_mod(0,val_max);
                            int pixel_trouve_int_B = polynome_pixel_B.calcul_mod(0,val_max);

                            Color pixel = new Color(pixel_trouve_int_R,pixel_trouve_int_G,pixel_trouve_int_B);
                            image_base.setRGB(x, y, pixel.getRGB());
                            p++;
                            if(affichage){
                                System.out.println("Traitement "+p+"/"+nombre_de_pixel_a_traite);
                            }
                        }
                    }
                    
                }

            }

            try{
                ImageIO.write(image_base, "png",fichier_base);
            }
            catch(IOException e){
                e.printStackTrace();
                return 0;
            }
        }

        return 1;
    }

    public static int puissance(int a, int b){
        int resultat = a;
        for(int i=0; i<b-1; i++){
            resultat*=a;
        }
        return resultat;
    }

    public static int renommer(String path_ancien, String path_nouveau){
        File ancien = new File(path_ancien);
		File nouveau = new File(path_nouveau);

		if(ancien.renameTo(nouveau)){
			//System.out.println("Success");
			return 1;
		}
		else{
			//System.out.println("Echec");
			return 0;
		}
	}

	public static int dupliquer(String string_path_ancien, String string_path_nouveau){
		Path path_ancien = Paths.get(string_path_ancien);
		Path path_nouveau = Paths.get(string_path_nouveau);
		try{
			Files.copy(path_ancien,path_nouveau);
			return 1;
		}catch(IOException e){
			e.printStackTrace();
			return 0;
		}
	}

    public static int convert_pixel_to_int(int r, int g, int b){
        String red = Integer.toBinaryString(r);
        String green = Integer.toBinaryString(g);
        String blue = Integer.toBinaryString(b);

        while(blue.length() < 8){
            blue = "0"+blue;
        }
        while(green.length() < 8){
            green = "0"+green;
        }
        while(red.length() < 8){
            red = "0"+red;
        }
        String bin = red+green+blue;
        return Integer.parseInt(bin,2);
    }

    public static int[] get_RGB_from_int(int rgb){
        int RGB[] = new int[3];
        if(rgb > 16777215 || rgb < 0){
            System.err.println("Dépassement de la couleur. pix_int = "+rgb);
            RGB[0] = 0;
            RGB[1] = 0;
            RGB[2] = 0;
            return RGB;
        }
        String rgb_bin = Integer.toBinaryString(rgb);
        while(rgb_bin.length() < 24){
            rgb_bin = "0" + rgb_bin;
        }
        RGB[0] = Integer.parseInt(rgb_bin.substring(0,8),2);
        RGB[1] = Integer.parseInt(rgb_bin.substring(8,16),2);
        RGB[2] = Integer.parseInt(rgb_bin.substring(16,24),2);
        return RGB;
    }

    // Fonction pour calculer l'inverse modulaire
    // Attention : veiller a travailler dans un corps (pour avoir tous les elements inversibles)
    // Avec les pixels, on travaille dans GF(251) 
    // (car GF(256) N'EST PAS un corps)
    public static int inverse_mod(int x, int mod) {
    	int inv = 1;
    	while (Lagrange.multiplication_mod(x, inv, mod) != 1) {
    		inv++;
    	}
    	return inv;
    }
    
    // Fonction pour transformer une valeur negative en positif (apres application du modulo)
    // Par exemple, -1 mod 251 = 250 mod 251 (a faire sinon problemes lors des calculs)
    public static int positive_mod(int x, int mod) {
    	int res = x;
    	while (res < 0) {
    		res += mod;
    	}
    	return res;
    }

}