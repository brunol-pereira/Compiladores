package br.ufscar.dc.compiladores.la.semantico;

import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {

    public enum TipoLA {
        INTEIRO,
        REAL,
        LITERAL,
        LOGICO,
        INVALIDO,
        PONTEIRO_INTEIRO,
        PONTEIRO_REAL,
        PONTEIRO_LOGICO,
        PONTEIRO_LITERAL,
        REGISTRO,
        ENDERECO,
        NAO_DECLARADO
    }

    public enum EstruturaLA {
        VARIAVEL,
        CONSTANTE,
        TIPO,
        PROCEDIMENTO,
        FUNCAO,
        REGISTRO
    } 

    class EntradaTabelaDeSimbolos {
        public String nome;
        public TipoLA varTipo;
        public EstruturaLA estrutura;
        public TabelaDeSimbolos argsRegFunc;
    }

    private HashMap<String, EntradaTabelaDeSimbolos> tabelaDeSimbolos;
    private TabelaDeSimbolos global;

    public TabelaDeSimbolos() {
        this.tabelaDeSimbolos = new HashMap<>();
        this.global = null;
    }

    // Define a tabela de símbolos global
    void setGlobal(TabelaDeSimbolos global){
        this.global = global;
    }

    // Adiciona uma entrada à tabela de símbolos
    public void adicionar(String nome, EstruturaLA estrutura,TipoLA varTipo ) {

        EntradaTabelaDeSimbolos tabela = new EntradaTabelaDeSimbolos();
        tabela.nome = nome;
        tabela.estrutura = estrutura;
        tabela.varTipo = varTipo;

        tabelaDeSimbolos.put(nome, tabela);
    }

    public void put(String nome, TipoLA estrutura, TipoLA varTipo, TabelaDeSimbolos argsRegFunc){
        EntradaTabelaDeSimbolos tabela = new EntradaTabelaDeSimbolos();
        tabela.nome = nome;
        tabela.estrutura = estrutura;
        tabela.varTipo = varTipo;
        tabela.argsRegFunc = argsRegFunc;
        tabelaDeSimbolos.put(nome, tabela);


    }

    // Verifica se um símbolo existe na tabela de símbolos
    public boolean existe(String nome) {
        if(global == null){
            return tabelaDeSimbolos.containsKey(nome);
        }else {
            return tabelaDeSimbolos.containsKey(nome) || global.existe(nome);
        }
    }
    // Verifica uma entrada na tabela de símbolos
    public EntradaTabelaDeSimbolos verificar(String nome) {
        if(global == null){
            return tabelaDeSimbolos.get(nome);
        }else{
            if(tabelaDeSimbolos.containsKey(nome)){
                return tabelaDeSimbolos.get(nome);
            }else{
                return global.verificar(nome);
            }
        }
    }
}
