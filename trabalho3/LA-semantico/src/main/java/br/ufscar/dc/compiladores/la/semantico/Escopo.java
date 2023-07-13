package br.ufscar.dc.compiladores.la.semantico;

import java.util.LinkedList;
import java.util.List;

public class Escopo {

    private LinkedList<TabelaDeSimbolos> pilhaDeTabelas;

    public Escopo() {
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo();
    }

    public void criarNovoEscopo() {
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }

    
    public TabelaDeSimbolos obterEscopoAtual() {
        return pilhaDeTabelas.peek();
    }

    public List<TabelaDeSimbolos> percorrerEscopoAninhados() {
        return pilhaDeTabelas;
    }

    public void abandonarEscopo() {
        pilhaDeTabelas.pop();
    }
}