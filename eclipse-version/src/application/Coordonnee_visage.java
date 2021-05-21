package application;

public class Coordonnee_visage {
    private Coord point_haut_gauche;
    private Coord point_bas_droit;

    public Coordonnee_visage(int a, int b, int c, int d){
        point_haut_gauche = new Coord(a,b);
        point_bas_droit = new Coord(c,d);
    }

    public int getHG_X(){
        return this.point_haut_gauche.getX();
    }

    public int getHG_Y(){
        return this.point_haut_gauche.getY();
    }

    public int getBD_X(){
        return this.point_bas_droit.getX();
    }

    public int getBD_Y(){
        return this.point_bas_droit.getY();
    }
}
