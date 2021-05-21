package application;
import java.util.ArrayList;
import java.util.Collections;

public class Lagrange {
    private ArrayList<Point> liste_points;
    private final boolean affichage = false;

    Lagrange(ArrayList<Point> lp){
        this.liste_points = lp;
    }

    public Lagrange(){
        this.liste_points = new ArrayList<Point>();
    }

    public void addPoint(Point p){
        this.liste_points.add(p);
    }

    // Fonction pour realiser une multiplication modulaire
    public static int multiplication_mod(int x, int y, int mod) {
    	int res = 0;
    	for (int i=0; i<y; i++) {
    		res += x;
    		res %= mod;
    	}
    	return res;
    }

    // Fonction pour calculer l'inverse modulaire
    // Attention : veiller a travailler dans un corps (pour avoir tous les elements inversibles)
    // Avec les pixels, on travaille dans GF(251) 
    // (car GF(256) N'EST PAS un corps)
    public static int inverse_mod(int x, int mod) {
    	int inv = 1;
    	while (multiplication_mod(x, inv, mod) != 1) {
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

    public PolynomeInt interpolation_int_mod(int mod, int[][] inverse_modulaires){
        int degre = this.liste_points.size()-1;
        //Dans cette arraylist on met les sous polynomes a additionner pour retrouver le polynome final
        ArrayList<PolynomeInt> L = new ArrayList<PolynomeInt>();

        //On traite chaque point
        for(int i=0; i<this.liste_points.size(); i++){
            //Ici on traite chaque "sous polynome" de la formule de Lagrange yi(M(X - xi)/(xj - xi))
        	// (X - xi)/(xj - xi) = (X - xi) * inverse_mod(xj - xi, mod)
        	// Attention, (xj - xi) doit etre une valeur positive donc on utilise positive_mod
        	
            if(affichage){
                System.out.println("--Traitement du point (" + this.liste_points.get(i).getX()+","+this.liste_points.get(i).getY()+")");
            }
            
            
            // On cr�e la liste de sous-polynomes
            ArrayList<PolynomeInt> poly = new ArrayList<PolynomeInt>();
            int affichage_sous_polynome_num = 0;
            int coeff_inv;
            
            // On calcule chaque sous-polynome
            for(int j=0; j<this.liste_points.size(); j++){
                if(this.liste_points.get(i)!=this.liste_points.get(j)){
                    affichage_sous_polynome_num++;

                    coeff_inv = inverse_modulaires[this.liste_points.get(i).getX()][this.liste_points.get(j).getX()];

                	poly.add(new PolynomeInt(new int[]{(multiplication_mod(coeff_inv, positive_mod(-this.liste_points.get(j).getX(), mod), mod)), coeff_inv}));

                    if(affichage){
                        System.out.println("-----Sous-Traitement du point (" + this.liste_points.get(j).getX()+","+this.liste_points.get(j).getY()+")");
                        System.out.println("Polynome tmp : "+poly.get(affichage_sous_polynome_num-1).toString());
                    }

                }
            }
            
            PolynomeInt tmp = poly.get(0);
            //On multiplie les sous-polynomes
            for(int j=1; j<poly.size();j++){
                tmp = tmp.multiplication_polynomiale_mod(poly.get(j),mod);
            }

            //On multiplie le polynome obtenu par yi
            tmp = tmp.multiplication_poly_mod(this.liste_points.get(i).getY(),mod);
            if(affichage){
                System.out.println("tmp multiplication yi = "+tmp.toString());
            }

            L.add(tmp);
            if(affichage){
                System.out.println();
            }
            
        }

        //On additionne/combine les polynomes du for pr�c�dent, S(yi(M(X - xi)/(xj - xi)))
        PolynomeInt res = L.get(0);
        if(affichage){
            System.out.println("tmp combinaison 0 = "+res.toString());
        }
        for(int i=1; i<this.liste_points.size();i++){
            res = res.combinaison_mod(L.get(i),mod);
            if(affichage){
                System.out.println("tmp combinaison "+i+" = "+res.toString());
            } 
        }

        return res;
    }
}