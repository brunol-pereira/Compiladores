package br.ufscar.dc.compiladores.LA.semantico;

import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {
    // public TabelaDeSimbolos.estrutTipoLA returnType;

    public enum EstrutTipoLA {
        INTEIRO,
        REAL,
        LITERAL,
        LOGICO,
        NAO_DECLARADO,
        INVALIDO
    }

    public enum VarTipoLA{
        VARIAVEL,
        CONSTANTE,
        TIPO,
        PROCEDIMENTO,
        FUNCAO
        // REGISTRO,
        // PONTEIRO,
    } 

    class EntradaTabelaDeSimbolos {
        String nome;
        EstrutTipoLA estrutura;
        VarTipoLA varTipo;

        public EntradaTabelaDeSimbolos(String nome, EstrutTipoLA estrutura,VarTipoLA estrutura){
            this.nome = nome;
            this.varTipo = varTipo;
            this.estrutura = estrutura;
        }
    }

    private HashMap<String, EntradaTabelaDeSimbolos> tabelaDeSimbolos;
    private TabelaDeSimbolos global;
    
    void setGlobal(TabelaDeSimbolos global){
        this.global = global;
    }
    
    public void adicionar(String nome, VarTipoLA varTipo, EstrutTipoLA estrutura ) {
        // TabelaDeSimbolos tabela = new TabelaDeSimbolos();
        // this.nome = nome;
        // this.varTipo = varTipo;
        // this.estrutura = estrutura;
        tabelaDeSimbolos.put(nome, new EntradaTabelaDeSimbolos(tipo));
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