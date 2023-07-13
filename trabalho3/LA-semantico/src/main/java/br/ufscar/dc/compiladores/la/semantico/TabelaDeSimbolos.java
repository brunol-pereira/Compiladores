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
        NAO_DECLARADO
    }

    public enum EstruturaLA {
        VARIAVEL,
        CONSTANTE,
        TIPO,
        PROCEDIMENTO,
        FUNCAO
    } 

    class EntradaTabelaDeSimbolos {
        public String nome;
        public TipoLA varTipo;
        public EstruturaLA estrutura;
    }

    private HashMap<String, EntradaTabelaDeSimbolos> tabelaDeSimbolos;
    private TabelaDeSimbolos global;

    public TabelaDeSimbolos() {
        this.tabelaDeSimbolos = new HashMap<>();
        this.global = null;
    }
    
    void setGlobal(TabelaDeSimbolos global){
        this.global = global;
    }
    
    public void adicionar(String nome, EstruturaLA estrutura,TipoLA varTipo ) {

        EntradaTabelaDeSimbolos tabela = new EntradaTabelaDeSimbolos();
        tabela.nome = nome;
        tabela.estrutura = estrutura;
        tabela.varTipo = varTipo;

        tabelaDeSimbolos.put(nome, tabela);
    }
    
    public boolean existe(String nome) {
        if(global == null){
            return tabelaDeSimbolos.containsKey(nome);
        }else {
            return tabelaDeSimbolos.containsKey(nome) || global.existe(nome);
        }
    }
    
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
        // return tabela.get(nome).tipo;
    }
}