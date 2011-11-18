package worker;

import java.math.BigInteger;
import java.util.Random;

public class MillerRabin{

    int w; // Zeugen

    public void init() {
        // Eingabe: eine natürliche ungerade Zahl größer 2
        BigInteger N = new BigInteger("4503599627370449");
        w = 10;
        miller_rabin(N,w);
    }

    // Häufig benötigte Werte
    BigInteger Big0 = BigInteger.valueOf(0);
    BigInteger Big1 = BigInteger.valueOf(1);
    BigInteger Big2 = BigInteger.valueOf(2);

    public boolean miller_rabin(BigInteger N, int s) {

        BigInteger R, N_2 = N.subtract(Big2);
        boolean prime = true;

        for (w=1;w<s+1;w++) {
            R = random(N_2);
            if (witness(R,N)) {
                prime = false;
                break;
            }
        }

        if (prime) return true;
        else return false;
    }


    Random seed = new Random();

    public BigInteger random(BigInteger N_2) {

        BigInteger R;

        // Zufallszahl aus [1,N-1]
        do {R = new BigInteger(N_2.bitLength(),seed).add(Big1);}
        while (R.compareTo(N_2)>0);

        return R;
    }


    String bin;

    public String bigIntToBinaryString(BigInteger B) {

         // Eingabe in binäre Form umwandeln
        for (BigInteger I=Big2.pow(B.bitLength()-1);I.compareTo(Big0)!=0;I=I.divide(Big2)) {
            if (B.compareTo(I)>=0) {
                bin += "1";
                B = B.subtract(I);
            }
            else bin += "0";
        }

        return bin;
    }


    public BigInteger modular_exponentiation(BigInteger A, BigInteger U, BigInteger N) {

        BigInteger C = Big0;
        BigInteger D = Big1;

        // Sei {b[0],b[1],...,b[k-1],b[k]} die binäre Darstellung von b
        if (w==1) bin = bigIntToBinaryString(U);
        // Da für alle Zeugen gleich, nur einmal berechnen

        for (int i=0;i<bin.length();i++) {

            C = C.multiply(Big2);
            D = D.pow(2).mod(N);

            if (bin.charAt(i)=='1') {
                C = C.add(Big1);
                D = D.multiply(A).mod(N);
            }
        }

        return D;
    }


    int t;

    public boolean witness(BigInteger A, BigInteger N) {

        BigInteger N_1 = N.subtract(Big1);

        // Sei n-1 = 2^t*u mit t >= 1 und u ungerade
        if (w==1) while(N_1.divide(Big2.pow(t)).mod(Big2).compareTo(Big0)==0) t++;
        // Da für alle Zeugen gleich, nur einmal berechnen

        BigInteger U = N_1.divide(Big2.pow(t));

        BigInteger x0;
        BigInteger x1 = modular_exponentiation(A,U,N); // A^U mod N

        for (int i=1;i<t+1;i++) {
            x0 = x1;
            x1 = x0.pow(2).mod(N);
            if (x1.compareTo(Big1)==0 && x0.compareTo(Big1)!=0 && x0.compareTo(N_1)!=0)
                return true;
        }
        if (x1.compareTo(Big1)!=0) return true;
        else return false;
    }
}