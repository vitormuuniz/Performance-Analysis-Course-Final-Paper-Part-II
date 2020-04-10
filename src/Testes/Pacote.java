/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testes;

/**
 *
 * @author glauc
 */
public class Pacote implements Comparable<Pacote> {

    double tamanho;
    double tempo_atual;
    double intervalo;
    double tempo_fim;

    public Pacote(double tamanho, double tempo_atual, double intervalo, double tempo_fim) {
        this.tamanho = tamanho;
        this.tempo_atual = tempo_atual;
        this.intervalo = intervalo;
        this.tempo_fim = tempo_fim;
    }

    @Override
    public int compareTo(Pacote comparado) {
        if(this.tempo_atual > comparado.tempo_atual){
            return 1;
        }else if(this.tempo_atual < comparado.tempo_atual){
            return -1;
        }else{
            return 0;
        }
    }

}
