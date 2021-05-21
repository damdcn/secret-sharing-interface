package code;

public class PolynomeInt {
    private int degre;
    private int[] coefficients;

    public PolynomeInt(int d, int[] cs){
        this.degre = d;
        this.coefficients = cs;
    }

    public PolynomeInt(int[] cs){
        this.degre = cs.length -1;
        this.coefficients = cs;
    }

    public PolynomeInt(){
        this.degre = 0;
        this.coefficients = new int[this.degre + 1];
    }

    public void setDegre(int d){
        this.degre = d;
        this.coefficients = new int[d+1];
    }

    public int calcul(int x){
        int resultat = 0;

        for(int i=0; i<degre+1; i++){
            if(i==0){
                resultat+=this.coefficients[0];
            }
            else{
                resultat+=this.coefficients[i]*PolynomeInt.puissance(x,i);
            }
        }
        return resultat;
    }

    public int calcul_mod(int x, int mod){
        int resultat = this.coefficients[0];

        for(int i=1; i<degre+1; i++){
            resultat+=multiplication_mod(this.coefficients[i],PolynomeInt.puissance_mod(x,i,mod),mod);
            resultat%=mod;
        }
        return resultat;
    }

    public static int multiplication_mod(int x, int y, int mod){
        int resultat = 0;

        for(int i=0; i < y; i++){
            resultat+=x;
            resultat%=mod;
        }
        return resultat;
    }

    public static int puissance_mod(int a, int b, int mod){
        int resultat = a;
        for(int i=0; i<b-1; i++){
            resultat*=a;
            resultat %= mod;
        }
        return resultat;
    }

    public int getDegre(){
        return this.degre;
    }

    public int getCoefficient(int i){
        return this.coefficients[i];
    }

    public PolynomeInt combinaison(PolynomeInt p){
        int degre = (this.degre>p.getDegre()? this.degre : p.getDegre());
        int[] coeffres = new int[degre+1];

        for(int i=0; i<(degre+1); i++){
            
            int a=0,b=0;
            if(this.degre >= i){
                a=this.coefficients[i];
            }
            if(p.getDegre() >= i){
                b=p.getCoefficient(i);
            }
            coeffres[i] = a+b;
        }

        PolynomeInt polyres = new PolynomeInt(degre,coeffres);
        return polyres;
    }

    public PolynomeInt combinaison_mod(PolynomeInt p, int mod){
        int degre = (this.degre>p.getDegre()? this.degre : p.getDegre());
        int[] coeffres = new int[degre+1];

        for(int i=0; i<(degre+1); i++){
            
            int a=0,b=0;
            if(this.degre >= i){
                a=this.coefficients[i];
            }
            if(p.getDegre() >= i){
                b=p.getCoefficient(i);
            }
            coeffres[i] = (a+b)%mod;
        }

        PolynomeInt polyres = new PolynomeInt(degre,coeffres);
        return polyres;
    }

    public PolynomeInt multiplication_poly(int x){
        int[] coeffres = new int[this.degre+1];

        for(int i=0; i<this.degre+1;i++){
            coeffres[i]=this.coefficients[i]*x;
        }

        return new PolynomeInt(coeffres);
    }

    public PolynomeInt multiplication_poly_mod(int x, int mod){
        int[] coeffres = new int[this.degre+1];

        for(int i=0; i<this.degre+1;i++){
            coeffres[i]=(this.coefficients[i]*x)%mod;
        }

        return new PolynomeInt(coeffres);
    }

    public PolynomeInt multiplication_polynomiale(PolynomeInt p){
        int degre = this.degre + p.getDegre();
        int[] coeffres = new int[degre+1];
        for(int i=0; i<degre+1;i++){
            coeffres[i]=0;
        }

        for(int i=0; i<(this.degre+1); i++){
            for(int j=0; j<(p.getDegre()+1);j++){
                coeffres[i+j] += this.coefficients[i] * p.getCoefficient(j);
            }
        }
        PolynomeInt polyres = new PolynomeInt(degre,coeffres);
        return polyres;
    }

    public PolynomeInt multiplication_polynomiale_mod(PolynomeInt p,int mod){
        int degre = this.degre + p.getDegre();
        int[] coeffes = new int[degre+1];
        for(int i=0; i<degre+1;i++){
            coeffes[i]=0;
        }

        for(int i=0; i<(this.degre+1); i++){
            for(int j=0; j<(p.getDegre()+1);j++){
                int coeff = this.coefficients[i] * p.getCoefficient(j);
                coeffes[i+j] = (coeffes[i+j] + (coeff%mod))%mod;
            }
        }
        PolynomeInt polyres = new PolynomeInt(degre,coeffes);
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
                    res+= " + ";
                }
            }
        }
        return res;
    }

    public static long puissance(int a, int b){
        long resultat = a;
        for(int i=0; i<b-1; i++){
            resultat*=a;
        }
        return resultat;
    }
}
