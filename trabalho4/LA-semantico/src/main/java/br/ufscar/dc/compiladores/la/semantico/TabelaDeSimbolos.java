package br.ufscar.dc.compiladores.la.semantico;

import java.util.ArrayList;
import java.util.HashMap;

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
    public void put(String nome, EstruturaLA estrutura,TipoLA varTipo ) {

        EntradaTabelaDeSimbolos tabela = new EntradaTabelaDeSimbolos();
        tabela.nome = nome;
        tabela.estrutura = estrutura;
        tabela.varTipo = varTipo;
        tabelaDeSimbolos.put(nome, tabela);
    }

     public void put(String nome, EstruturaLA estrutura, TipoLA varTipo, TabelaDeSimbolos argsRegFunc) {

        EntradaTabelaDeSimbolos tabela = new EntradaTabelaDeSimbolos();
        tabela.nome = nome;
        tabela.estrutura = estrutura;
        tabela.varTipo = varTipo;
        tabela.argsRegFunc = argsRegFunc;
        tabelaDeSimbolos.put(nome, tabela);
    }

     public void put(String nome, EstruturaLA estrutura, TipoLA varTipo, TabelaDeSimbolos argsRegFunc, String TipoFunc) {

        EntradaTabelaDeSimbolos tabela = new EntradaTabelaDeSimbolos();
        tabela.nome = nome;
        tabela.estrutura = estrutura;
        tabela.varTipo = varTipo;
        tabela.argsRegFunc = argsRegFunc;
        tabela.TipoFuncao = TipoFunc;
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

    public boolean validar(ArrayList<TabelaDeSimbolos.TipoLA> types){
        int contador = 0;
        
        if(TabelaDeSimbolos.size() != types.size())
            return false;
        for(EntradaTabelaDeSimbolos entry: tabelaDeSimbolos.values()){
            if(types.get(contador) != entry.varTipo){
                return false;
            }
            contador++;
        }
        
        return true;
    }


//--------------Correção do visual studio---------------------//

    private static int size() {
        return 0;
    }

//------------------------------------------------------------//    
}

// package br.ufscar.dc.compiladores.LA;

// import java.util.ArrayList;
// import java.util.HashMap;

// public class SymbolTable {

//     public enum TypeLAVariable{
//         LITERAL,
//         INTEIRO,
//         REAL,
//         LOGICO,
//         NAO_DECLARADO,
//         INVALIDO,
//         PONT_INTE,
//         PONT_REAL,
//         PONT_LOGI,
//         PONT_LITE,
//         ENDERECO,
//         REGISTRO
//     }

//     public enum TypeLAIdentifier{
//         VARIAVEL,
//         CONSTANTE,
//         TIPO,
//         PROCEDIMENTO,
//         FUNCAO,
//         REGISTRO
//     }

//     private HashMap<String, SymbolTableEntry> symbolTable;
//     private SymbolTable global;

//     public SymbolTable() {
//         this.symbolTable = new HashMap<>();
//         this.global = null;
//     }

//     void setGlobal(SymbolTable global){
//         this.global = global;
//     }

//     public void put(String name, TypeLAIdentifier identifierType, TypeLAVariable variableType) {
//         SymbolTableEntry ste = new SymbolTableEntry();
//         ste.name = name;
//         ste.identifierType = identifierType;
//         ste.variableType = variableType;
//         symbolTable.put(name, ste);
//     }

//     public void put(String name, TypeLAIdentifier identifierType, TypeLAVariable variableType, SymbolTable argsRegFunc) {
//         SymbolTableEntry ste = new SymbolTableEntry();
//         ste.name = name;
//         ste.identifierType = identifierType;
//         ste.variableType = variableType;
//         ste.argsRegFunc = argsRegFunc;
//         symbolTable.put(name, ste);
//     }

//     public void put(String name, TypeLAIdentifier identifierType, TypeLAVariable variableType, SymbolTable argsRegFunc, String funcType) {
//         SymbolTableEntry ste = new SymbolTableEntry();
//         ste.name = name;
//         ste.identifierType = identifierType;
//         ste.variableType = variableType;
//         ste.argsRegFunc = argsRegFunc;
//         ste.functionType = funcType;
//         symbolTable.put(name, ste);
//     }
   
//     // returns true or false if an identifier exists in the table
//     public boolean exists(String name) {
//         if(global == null) {
//             return symbolTable.containsKey(name);
//         } else {
//             return symbolTable.containsKey(name) || global.exists(name);
//         }
//     }

//     // returns an entry of the symbol table given a name
//     public SymbolTableEntry check(String name) {
//         if(global == null)
//             return symbolTable.get(name);
//         else{
//             if(symbolTable.containsKey(name))
//                 return symbolTable.get(name);
//             else
//                 return global.check(name);
//         }
//     }

//     // type validation for registers and functions
//     public boolean validType(ArrayList<SymbolTable.TypeLAVariable> types){
//         int counter = 0;
        
//         if(symbolTable.size() != types.size())
//             return false;
//         for(SymbolTableEntry entry: symbolTable.values()){
//             if(types.get(counter) != entry.variableType){
//                 return false;
//             }
//             counter++;
//         }
        
//         return true;
//     }
    
// }