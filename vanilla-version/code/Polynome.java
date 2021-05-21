package code;

public class Polynome {
    private int degre;
    private double[] coefficients;

    public Polynome(int d, double[] cs){
        this.degre = d;
        this.coefficients = cs;
    }

    public Polynome(double[] cs){
        this.degre = cs.length -1;
        this.coefficients = cs;
    }

    public Polynome(){
        this.degre = 0;
        this.coefficients = new double[this.degre + 1];
    }

    public void setDegre(int d){
        this.degre = d;
        this.coefficients = new double[d+1];
    }

    public double calcul(double x){
        double resultat = 0;

        for(int i=0; i<degre+1; i++){
            if(i==0){
                resultat+=this.coefficients[0];
            }
            else{
                resultat+=this.coefficients[i]*puissance(x,i);
            }
        }
        return resultat;
    }

    public int getDegre(){
        return this.degre;
    }

    public double getCoefficient(int i){
        return this.coefficients[i];
    }

    public Polynome combinaison(Polynome p){
        int degre = (this.degre>p.getDegre()? this.degre : p.getDegre());
        double[] coeffres = new double[degre+1];

        for(int i=0; i<(degre+1); i++){
            
            double a=0,b=0;
            if(this.degre >= i){
                a=this.coefficients[i];
            }
            if(p.getDegre() >= i){
                b=p.getCoefficient(i);
            }
            coeffres[i] = a+b;
        }

        Polynome polyres = new Polynome(degre,coeffres);
        return polyres;
    }

    public Polynome multiplication(Double x){
        double[] coeffres = new double[this.degre+1];

        for(int i=0; i<this.degre+1;i++){
            coeffres[i]=this.coefficients[i]*x;
        }

        return new Polynome(coeffres);
    }

    public Polynome mumtiplication_polynomiale(Polynome p){
        int degre = this.degre + p.getDegre();
        double[] coeffres = new double[degre+1];
        for(int i=0; i<degre+1;i++){
            coeffres[i]=0;
        }

        for(int i=0; i<(this.degre+1); i++){
            for(int j=0; j<(p.getDegre()+1);j++){
                coeffres[i+j] += this.coefficients[i] * p.getCoefficient(j);
            }
        }
        Polynome polyres = new Polynome(degre,coeffres);
        return polyres;
    }

    public String toString(){
        String res ="";
        for(int i=0; i<degre+1; i++){
            if(coefficients[i]!=0){
                if(i==degre){
                    res += coefficients[i]+"x^"+i;
                }
                else{
                    res += coefficients[i];
                    if(i!=0){
                        res += "x^"+i;
                    }
                    res+=" + ";
                }
            }
        }
        return res;
    }

    public static double puissance(double a, int b){
        double resultat = a;
        for(int i=0; i<b-1; i++){
            resultat*=a;
        }
        return resultat;
    }
}
