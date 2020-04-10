package simulacao;

import java.util.Random;

public class GeraIntervalosAleatorios {

    public static void main(String[] args) {
        Random a = new Random();
        double soma = 0;

        int x = 150;

        for (int i = 0; i < x; i++) {
            //Retorna um valor pseudo-aleatório uniformemente distribuído intentre 0 (inclusive) e o valor especificado (exclusivo), 
            //            System.out.println(a.nextInt((20 - 10) + 1) + 10);
            //aleatorio.nextInt((max - min) + 1) + min;
            System.out.println((double) (a.nextInt((20 - 10) + 1) + 10) / 1000);
            soma += (double) (a.nextInt((20 - 10) + 1) + 10) / 1000;
        }
        System.out.println("Media:  " + soma / x);
    }
}