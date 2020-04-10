/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testes;

import java.util.PriorityQueue;

/**
 *
 * @author glauc
 */
public class Main {

    public static void main(String[] args) {

        PriorityQueue<Pacote> arvore = new PriorityQueue();
        arvore.add(new Pacote(0, 0.07, 0, 0));
        arvore.add(new Pacote(0, 0.08, 0, 0));
        arvore.add(new Pacote(0, 0.03, 0, 0));
        arvore.add(new Pacote(0, 0.01, 0, 0));
        arvore.add(new Pacote(0, 0.02, 0, 0));
        arvore.add(new Pacote(0, 0.09, 0, 0));
        arvore.add(new Pacote(0, 0.00, 0, 0));
        
        while(!arvore.isEmpty()){
            System.out.println(arvore.remove().tempo_atual);
        }

    }
}
