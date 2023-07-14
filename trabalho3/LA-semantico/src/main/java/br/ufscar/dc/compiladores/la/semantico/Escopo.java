package br.ufscar.dc.compiladores.la.semantico;

import java.util.LinkedList;
import java.util.List;

//Construtor da classe Escopo.
//Inicializa a pilha de tabelas de símbolos e cria um novo escopo.
public class Escopo {

    private LinkedList<TabelaDeSimbolos> pilhaDeTabelas;

    public Escopo() {
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo();
    }

    //Cria um novo escopo, adicionando uma nova tabela de símbolos à pilha de tabelas.
    public void criarNovoEscopo() {
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }

    //Retorna o escopo atual
    public TabelaDeSimbolos obterEscopoAtual() {
        return pilhaDeTabelas.peek();
    }
    //Retorna uma lista contendo todas as tabelas de símbolos presentes na pilha de tabelas, percorrendo os escopos aninhados.
    public List<TabelaDeSimbolos> percorrerEscopoAninhados() {
        return pilhaDeTabelas;
    }
    //Abandona o escopo atual, removendo a tabela de símbolos do topo da pilha.
    public void abandonarEscopo() {
        pilhaDeTabelas.pop();
    }
}
