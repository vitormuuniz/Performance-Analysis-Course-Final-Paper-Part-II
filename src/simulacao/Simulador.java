package simulacao;

import static java.lang.Math.log;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

import Simulacao.Little;

public class Simulador {

    Random gerador = new Random(1556915527);

    /*
    @return numero aleatorio entre (0,1] 
     */
    public double aleatorio() {
        double u;
        u = this.gerador.nextDouble();
        u = 1.0 - u;
        return (u);
    }

    /**
     * @param parametro_l parametro da exponencial
     * @return intervalo de tempo, com media tendendo ao intervalo informado
     * pelo usuário
     */
    public double calcula_chegada(double l) {
        return (-1.0 / l) * log(aleatorio());
    }

    public double calculaIntervalo() {
        return (double) (gerador.nextInt((20 - 10) + 1) + 10) / 1000;
    }

    /**
     * @return tamanho do pacote que acabou de chegar, seguindo a proporcao
     * aproximada de 50% = 500Bytes 40% = 40Bytes e 10% = 1500Bytes
     */
    double gera_tam_pct() {
        double a = aleatorio();
        //tamanhos convertidos para Mbits
        if (a <= 0.5) {
            return ((550.0 * 8.0) / (1000000.0)); //o 1KK é convertendo para Megabits
        } else if (a <= 0.9) {
            return ((40.0 * 8.0) / (1000000.0));
        }
        return ((1500.0 * 8.0) / (1000000.0));
    }

    public double minimo(double a, double b) {
        if (a <= b) {
            return a;
        }
        return b;
    }

    public void simulacao() {

        PriorityQueue<Pacote> heap_min = new PriorityQueue<>();  //arvore para tratamento dos eventos futuros de CBR      

        LinkedList<Pacote> fila_atendimento_web = new LinkedList<>(); //fila para os pacotes web

        LinkedList<Pacote> fila_atendimento_cbr = new LinkedList<>(); //fila para os pactes cbr

        boolean isWEB = true;//inicializada desnecessariamente

        //variavel para en
        Little en_total = new Little();
        Little en_web = new Little();
        Little en_cbr = new Little();

        //variavel para ew_chegada
        Little ew_chegada_total = new Little();
        Little ew_chegada_web = new Little();
        Little ew_chegada_cbr = new Little();

        //variavel para ew_saida
        Little ew_saida_total = new Little();
        Little ew_saida_web = new Little();
        Little ew_saida_cbr = new Little();

//        int seed = 1556915527;
//        srand(seed);
        double tempo = 0.0; //tempo atual na simulação
        double tempo_total; //tempo que total simulado

        Scanner ler = new Scanner(System.in);

        System.out.println("Informe o tempo total de simulacao: ");
        tempo_total = ler.nextDouble();

        double intervalo; //parametro ajustavel do intervalo de chegadas de pacotes no roteador
        System.out.println("Informe o intervalo médio(s) de tempo entre pacotes: ");
        intervalo = ler.nextDouble(); //vai colocar o valor do intervalo no endereco que for informado. Logo se fosse um ponteiro, era somemte o nome da variavel

        double intervalo_entre_ligacoes; //parametro ajustavel do intervalo de chegadas de pacotes no roteador
        System.out.println("Informe o intervalo médio(s) de tempo entre ligações: ");
        intervalo_entre_ligacoes = ler.nextDouble(); //vai colocar o valor do intervalo no endereco que for informado. Logo se fosse um ponteiro, era somemte o nome da variavel

        double tempo_medio_ligacoes; //parametro ajustavel do intervalo de chegadas de pacotes no roteador
        System.out.println("Informe o tempo médio das ligações: ");
        tempo_medio_ligacoes = ler.nextDouble(); //vai colocar o valor do intervalo no endereco que for informado. Logo se fosse um ponteiro, era somemte o nome da variavel

        //ajustando paramentro para a exponencial - intervalo é o 'l'
        intervalo = 1.0 / intervalo;
        //ajustando paramentro para a exponencial - intervalo é o 'l'
        intervalo_entre_ligacoes = 1.0 / intervalo_entre_ligacoes;

        //ajustando paramentro para a exponencial - intervalo é o 'l'
        tempo_medio_ligacoes = 1 / tempo_medio_ligacoes;

        //tamanho do pacote gerado
        double tam_pct;

        //tamanho do link de saida do roteador
        double link;
        System.out.println("Informe o tamanho do link (Mbps): ");
        link = ler.nextDouble();

        //fila, onde fila == 0 indica
        //roteador vazio, fila == 1
        //indica 1 pacote, ja em transmissao;
        //fila > 1 indica 1 pacote em transmissão e os demais em espera
        //double fila = 0.0;
        //tempo de chegada do proximo pacote ao sistema
        double chegada_proximo_pct = calcula_chegada(intervalo); //sempre momento futuro

        double chegada_proxima_ligacao = calcula_chegada(intervalo_entre_ligacoes);

        //tempo de chegada do proximo pacote
        //cbr ao sistema, com intervalor
        //de 20 ms entre pacotes
        double chegada_proximo_pct_cbr = Double.POSITIVE_INFINITY;
        /*ATENCAO AQUIII*/

        //tempo de saida do pacote que esta sendo atendido atualmente
        double saida_pct_atendimento = Double.POSITIVE_INFINITY;

        //tempo que o roteador fica ocupado
        double ocupacao = 0.0;

        while (tempo <= tempo_total) {
            //roteador vazio, logo, 
            //avanco no tempo para a chegada do
            //proximo pacote
            if (fila_atendimento_web.isEmpty() && fila_atendimento_cbr.isEmpty()) {
                tempo = minimo(chegada_proxima_ligacao, chegada_proximo_pct);
            } else {
                //há fila!
                tempo = minimo(minimo(minimo(chegada_proximo_pct_cbr, chegada_proximo_pct), chegada_proxima_ligacao), saida_pct_atendimento); //para saber quem acontece antes e tratarmos
            }

            //chegada de pacote
            if (tempo == chegada_proximo_pct) {
                //roteador livre
                //gero o tamanho do pacote
                //que acaba de chegar
                tam_pct = gera_tam_pct();

                if (fila_atendimento_web.isEmpty() && fila_atendimento_cbr.isEmpty()) {
                    //gerando o tempo em que o pacote
                    //atual sairá do sistema
                    saida_pct_atendimento = tempo + tam_pct / link; //tamanho por link eh o tempo que ele será processado no roteador
                    //tempo que o roteador ficou ocupado atendendo o pacote
                    ocupacao += saida_pct_atendimento - tempo;
                    isWEB = true;

                }

                //pacote colocado na fila do web
                fila_atendimento_web.add(new Pacote(tam_pct, -1, -1, -1));
                //gerar o tempo de chegada do proximo
                chegada_proximo_pct = tempo + calcula_chegada(intervalo);

                //calculo little -- E[N] TOTAL
                en_total.soma_areas += en_total.qtd_pacotes * (tempo - en_total.tempo_anterior);
                en_total.qtd_pacotes++;
                en_total.tempo_anterior = tempo;

                //calculo little -- E[W] TOTAL
                ew_chegada_total.soma_areas += ew_chegada_total.qtd_pacotes * (tempo - ew_chegada_total.tempo_anterior);
                ew_chegada_total.qtd_pacotes++;
                ew_chegada_total.tempo_anterior = tempo;

                //calculo little -- E[N] WEB 
                en_web.soma_areas += en_web.qtd_pacotes * (tempo - en_web.tempo_anterior);
                en_web.qtd_pacotes++;
                en_web.tempo_anterior = tempo;

                //calculo little -- E[W] WEB
                ew_chegada_web.soma_areas += ew_chegada_web.qtd_pacotes * (tempo - ew_chegada_web.tempo_anterior);
                ew_chegada_web.qtd_pacotes++;
                ew_chegada_web.tempo_anterior = tempo;

            } else if (tempo == chegada_proxima_ligacao) {

                //chegou pacote cbr de ligação
                tam_pct = ((1200.0 * 8.0) / 1000000.0);

                if (fila_atendimento_web.isEmpty() && fila_atendimento_cbr.isEmpty()) {
                    //gerando o tempo em que o pacote
                    //atual sairá do sistema
                    saida_pct_atendimento = tempo + tam_pct / link; //tamanho por link eh o tempo que ele será processado no roteador
                    //tempo que o roteador ficou ocupado atendendo o pacote
                    ocupacao += saida_pct_atendimento - tempo;
                    isWEB = false;
                }

                double tempo_fim_ligacao = tempo + calcula_chegada(tempo_medio_ligacoes);

                double valor_intervalo = calculaIntervalo();

                //pacote colocado na fila real de ATENDIMENTO CBR DO ROTEADOR
                fila_atendimento_cbr.add(new Pacote(tam_pct, valor_intervalo, tempo, tempo_fim_ligacao));

                //adiciono na heap os proximo pacote da ligacao
                if (tempo + valor_intervalo <= tempo_fim_ligacao) {
                    heap_min.add(new Pacote(tam_pct, valor_intervalo, tempo + valor_intervalo, tempo_fim_ligacao));
                }

                chegada_proxima_ligacao += calcula_chegada(intervalo_entre_ligacoes);

                //calculo little -- E[N] TOTAL
                en_total.soma_areas += en_total.qtd_pacotes * (tempo - en_total.tempo_anterior);
                en_total.qtd_pacotes++;
                en_total.tempo_anterior = tempo;

                //calculo little -- E[W] TOTAL
                ew_chegada_total.soma_areas += ew_chegada_total.qtd_pacotes * (tempo - ew_chegada_total.tempo_anterior);
                ew_chegada_total.qtd_pacotes++;
                ew_chegada_total.tempo_anterior = tempo;

                //calculo little -- E[N] CBR
                en_cbr.soma_areas += en_cbr.qtd_pacotes * (tempo - en_cbr.tempo_anterior);
                en_cbr.qtd_pacotes++;
                en_cbr.tempo_anterior = tempo;

                //calculo little -- E[W] CBR
                ew_chegada_cbr.soma_areas += ew_chegada_cbr.qtd_pacotes * (tempo - ew_chegada_cbr.tempo_anterior);
                ew_chegada_cbr.qtd_pacotes++;
                ew_chegada_cbr.tempo_anterior = tempo;

            } else if (tempo == chegada_proximo_pct_cbr) {
                //printf("CBR CHEGOU!\n");

                //chegou pacote cbr
                tam_pct = ((1200.0 * 8.0) / 1000000.0);

                if (fila_atendimento_web.isEmpty() && fila_atendimento_cbr.isEmpty()) {
                    //gerando o tempo em que o pacote
                    //atual sairá do sistema
                    saida_pct_atendimento = tempo + tam_pct / link; //tamanho por link eh o tempo que ele será processado no roteador
                    //tempo que o roteador ficou ocupado atendendo o pacote
                    ocupacao += saida_pct_atendimento - tempo;
                    isWEB = false;
                }

                Pacote cbr_retirado = heap_min.remove();//removeu o pacote a ser passado pro atendimento

                //insiro o pacote da heap de previsao de eventos futuros na fila de atendimento
                fila_atendimento_cbr.add(cbr_retirado);

                //AQUI ESTAVA ERRADO:
                //                               tempo + tempo_medio_ligacoes????????
                //ANTES :  if (tempo + 0.02 <= tempo + tempo_medio_ligacoes) {
                //AGORA ESTA CERTO ACHO :
                if (tempo + cbr_retirado.intervalo <= cbr_retirado.tempo_fim) {
                    heap_min.add(new Pacote(tam_pct, cbr_retirado.intervalo, tempo + cbr_retirado.intervalo, cbr_retirado.tempo_fim));
                }

                //calculo little -- E[N]
                en_total.soma_areas += en_total.qtd_pacotes * (tempo - en_total.tempo_anterior);
                en_total.qtd_pacotes++;
                en_total.tempo_anterior = tempo;

                //calculo little -- E[W]
                ew_chegada_total.soma_areas += ew_chegada_total.qtd_pacotes * (tempo - ew_chegada_total.tempo_anterior);
                ew_chegada_total.qtd_pacotes++;
                ew_chegada_total.tempo_anterior = tempo;

                //calculo little -- E[N] CBR
                en_cbr.soma_areas += en_cbr.qtd_pacotes * (tempo - en_cbr.tempo_anterior);
                en_cbr.qtd_pacotes++;
                en_cbr.tempo_anterior = tempo;

                //calculo little -- E[W] CBR
                ew_chegada_cbr.soma_areas += ew_chegada_cbr.qtd_pacotes * (tempo - ew_chegada_cbr.tempo_anterior);
                ew_chegada_cbr.qtd_pacotes++;
                ew_chegada_cbr.tempo_anterior = tempo;

            } else {//saida de pacote
                boolean tem_pacote_cbr = false;
                boolean tem_pacote_web = false;

//                fila_atendimento.remove(); //retiro o pacote em atendimento
                if (isWEB) {
                    fila_atendimento_web.remove();
                    //calculo little -- E[N]
                    en_web.soma_areas += en_web.qtd_pacotes * (tempo - en_web.tempo_anterior);
                    en_web.qtd_pacotes--;
                    en_web.tempo_anterior = tempo;

                    //calculo little -- E[W] saida
                    ew_saida_web.soma_areas += ew_saida_web.qtd_pacotes * (tempo - ew_saida_web.tempo_anterior);
                    ew_saida_web.qtd_pacotes++;
                    ew_saida_web.tempo_anterior = tempo;
                } else {
                    fila_atendimento_cbr.remove();
                    //calculo little -- E[N]
                    en_cbr.soma_areas += en_cbr.qtd_pacotes * (tempo - en_cbr.tempo_anterior);
                    en_cbr.qtd_pacotes--;
                    en_cbr.tempo_anterior = tempo;

                    //calculo little -- E[W] saida
                    ew_saida_cbr.soma_areas += ew_saida_cbr.qtd_pacotes * (tempo - ew_saida_cbr.tempo_anterior);
                    ew_saida_cbr.qtd_pacotes++;
                    ew_saida_cbr.tempo_anterior = tempo;
                }

                if (!fila_atendimento_web.isEmpty() && !fila_atendimento_cbr.isEmpty()) {
                    double tempo_cabeca_web = fila_atendimento_web.peek().tempo_atual;
                    double tempo_cabeca_cbr = fila_atendimento_cbr.peek().tempo_atual;

                    if (tempo - tempo_cabeca_cbr >= (tempo - tempo_cabeca_web) / 2) {
                        tem_pacote_cbr = true;
                    } else {
                        tem_pacote_web = true;
                    }
                } else if (!fila_atendimento_web.isEmpty()) {
                    tem_pacote_web = true;
                } else if (!fila_atendimento_cbr.isEmpty()) {
                    tem_pacote_cbr = true;
                }

                if (tem_pacote_web) {
                    //descobrir o tamanho do proximo pacote
                    tam_pct = fila_atendimento_web.peek().tamanho;
                    //gerando o tempo em que o pacote
                    //atual sairá do sistema
                    saida_pct_atendimento = tempo + tam_pct / link; //tamanho por link eh o tempo que ele será processado no roteador
                    //tempo que o roteador ficou ocupado atendendo o pacote
                    ocupacao += saida_pct_atendimento - tempo;
                    isWEB = true;
                } else if (tem_pacote_cbr) {
                    //descobrir o tamanho do proximo pacote
                    tam_pct = fila_atendimento_cbr.peek().tamanho;
                    //gerando o tempo em que o pacote
                    //atual sairá do sistema
                    saida_pct_atendimento = tempo + tam_pct / link; //tamanho por link eh o tempo que ele será processado no roteador
                    //tempo que o roteador ficou ocupado atendendo o pacote
                    ocupacao += saida_pct_atendimento - tempo;
                    isWEB = false;
                }

                //calculo little -- E[N]
                en_total.soma_areas += en_total.qtd_pacotes * (tempo - en_total.tempo_anterior);
                en_total.qtd_pacotes--;
                en_total.tempo_anterior = tempo;

                //calculo little -- E[W] saida
                ew_saida_total.soma_areas += ew_saida_total.qtd_pacotes * (tempo - ew_saida_total.tempo_anterior);
                ew_saida_total.qtd_pacotes++;
                ew_saida_total.tempo_anterior = tempo;
            }
            if (!heap_min.isEmpty()) {
                chegada_proximo_pct_cbr = heap_min.peek().tempo_atual;
            } else {
                chegada_proximo_pct_cbr = Double.POSITIVE_INFINITY;
            }

            //cont_pcts++;
        }

        //TOTAL  
        ew_saida_total.soma_areas += ew_saida_total.qtd_pacotes * (tempo - ew_saida_total.tempo_anterior);
        ew_chegada_total.soma_areas += ew_chegada_total.qtd_pacotes * (tempo - ew_chegada_total.tempo_anterior);

        double en_final = en_total.soma_areas / tempo;
        double ew = ew_chegada_total.soma_areas - ew_saida_total.soma_areas;
        ew = ew / ew_chegada_total.qtd_pacotes;
        double lambda = ew_chegada_total.qtd_pacotes / tempo;

        //WEB
        ew_saida_web.soma_areas += ew_saida_web.qtd_pacotes * (tempo - ew_saida_web.tempo_anterior);
        ew_chegada_web.soma_areas += ew_chegada_web.qtd_pacotes * (tempo - ew_chegada_web.tempo_anterior);

        double en_web_final = en_web.soma_areas / tempo;
        double ew_web = ew_chegada_web.soma_areas - ew_saida_web.soma_areas;
        ew_web = ew_web / ew_chegada_web.qtd_pacotes;
        double lambda_web = ew_chegada_web.qtd_pacotes / tempo;

        //CBR 
        ew_saida_cbr.soma_areas += ew_saida_cbr.qtd_pacotes * (tempo - ew_saida_cbr.tempo_anterior);
        ew_chegada_cbr.soma_areas += ew_chegada_cbr.qtd_pacotes * (tempo - ew_chegada_cbr.tempo_anterior);

        double en_cbr_final = en_cbr.soma_areas / tempo;
        double ew_cbr = ew_chegada_cbr.soma_areas - ew_saida_cbr.soma_areas;
        ew_cbr = ew_cbr / ew_chegada_cbr.qtd_pacotes;
        double lambda_cbr = ew_chegada_cbr.qtd_pacotes / tempo;

        //nao tempo total pois tempo total é um pouco além do total da execucao
        System.out.println("============ Little TOTAL =======");
        System.out.println("E[N] TOTAL = " + en_final); //media do tamanho da fila...
        System.out.println("E[W] TOTAL = " + ew);
        System.out.println("Lambda TOTAL: " + lambda);

        System.out.println("==================================");
        System.out.println(String.format("Validação de Little TOTAL: %.20f", (en_final - (lambda * ew))));
        System.out.println();

        //nao tempo total pois tempo total é um pouco além do total da execucao
        System.out.println("============ Little WEB =======");
        System.out.println("E[N] WEB = " + en_web_final); //media do tamanho da fila...
        System.out.println("E[W] WEB = " + ew_web);
        System.out.println("Lambda WEB: " + lambda_web);

        System.out.println("==================================");
        System.out.println(String.format("Validação de Little WEB: %.20f", (en_web_final - (lambda_web * ew_web))));
        System.out.println();

        //nao tempo total pois tempo total é um pouco além do total da execucao
        System.out.println("============ Little CBR =======");
        System.out.println("E[N] CBR = " + en_cbr_final); //media do tamanho da fila...
        System.out.println("E[W] CBR = " + ew_cbr);
        System.out.println("Lambda CBR: " + lambda_cbr);

        System.out.println("==================================");
        System.out.println(String.format("Validação de Little CBR: %.20f", (en_cbr_final - (lambda_cbr * ew_cbr))));
        System.out.println();

        System.out.println("Ocupacao TOTAL: " + ocupacao / tempo);
        ler.close();
    }
}
