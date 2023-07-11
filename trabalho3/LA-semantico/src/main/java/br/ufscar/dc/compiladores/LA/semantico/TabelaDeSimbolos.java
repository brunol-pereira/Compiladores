package br.ufscar.dc.compiladores.LA.semantico;

import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {

    public Table.Tipos returnType;
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
        String nome;
        TipoLA varTipo;
        EstruturaLA estrutura;

        public EntradaTabelaDeSimbolos(String nome, EstruturaLA estrutura,TipoLA varTipo){
            this.nome = nome;
            this.varTipo = varTipo;
            this.estrutura = estrutura;
        }
    }

    private HashMap<String, EntradaTabelaDeSimbolos> tabelaDeSimbolos;
    private TabelaDeSimbolos global;

    public TabelaDeSimbolos() {
        this.TabelaDeSimbolos = new HashMap<>();
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
        if(global = null){
            return tabelaDeSimbolos.containsKey(nome);
        }else {
            return tabelaDeSimbolos.containsKey(nome) || global.existe(nome);
        }
    }
    
    public EntradaTabelaDeSimbolos verificar(String nome) {
        if(global = null){
            return tabelaDeSimbolos.get(nome);
        }else{
            if(tabelaDeSimbolos.containsKey(nome)){
                return tabelaDeSimbolos.get(name);
            }else{
                return global.verificar(nome);
            }
        }
        // return tabela.get(nome).tipo;
    }
}