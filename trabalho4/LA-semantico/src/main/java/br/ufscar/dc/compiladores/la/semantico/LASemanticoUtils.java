package br.ufscar.dc.compiladores.la.semantico;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Exp_aritmeticaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.ExpressaoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.FatorContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Fator_logicoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.ParcelaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.TermoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Termo_logicoContext;
import br.ufscar.dc.compiladores.la.semantico.TabelaDeSimbolos.TipoLA;

public class LASemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();

    // Adiciona um erro semântico à lista de erros
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        // int coluna = t.getCharPositionInLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }

    // Verifica o tipo de um identificador em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.IdentificadorContext ctx) {
        String identifier = ctx.getText();

        if (!identifier.contains("[") && !identifier.contains("]")) {

            String[] part = identifier.split("\\.");

            if (!tabelaDeSimbolos.existe(part[0])) {
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador" + identifier + "nao declarado\n");
            }

            else {
                EntradaTabelaDeSimbolos ident = tabelaDeSimbolos.verificar(part[0]);
                if (ident.estrutura == TabelaDeSimbolos.EstruturaLA.REGISTRO && part.length > 1) {
                    TabelaDeSimbolos fields = ident.argsRegFunc;
                    if (!fields.existe(part[1])) {
                        adicionarErroSemantico(ctx.IDENT(0).getSymbol(),
                                "identificador " + identifier + "nao declarado\n");
                    } else {

                        EntradaTabelaDeSimbolos tabela = fields.verificar(part[1]);
                        if (tabela.varTipo == TabelaDeSimbolos.TipoLA.INTEIRO)
                            return TabelaDeSimbolos.TipoLA.INTEIRO;
                        if (tabela.varTipo == TabelaDeSimbolos.TipoLA.LITERAL)
                            return TabelaDeSimbolos.TipoLA.LITERAL;
                        if (tabela.varTipo == TabelaDeSimbolos.TipoLA.REAL)
                            return TabelaDeSimbolos.TipoLA.REAL;
                        if (tabela.varTipo == TabelaDeSimbolos.TipoLA.LOGICO)
                            return TabelaDeSimbolos.TipoLA.LOGICO;
                        if (tabela.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO)
                            return TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO;
                        if (tabela.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL)
                            return TabelaDeSimbolos.TipoLA.PONTEIRO_REAL;
                        if (tabela.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO)
                            return TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO;
                        if (tabela.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL)
                            return TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL;
                    }
                }

                if (ident.estrutura == TabelaDeSimbolos.EstruturaLA.REGISTRO && part.length == 1) {
                    return TabelaDeSimbolos.TipoLA.REGISTRO;
                }
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.INTEIRO)
                    return TabelaDeSimbolos.TipoLA.INTEIRO;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.LITERAL)
                    return TabelaDeSimbolos.TipoLA.LITERAL;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.REAL)
                    return TabelaDeSimbolos.TipoLA.REAL;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.LOGICO)
                    return TabelaDeSimbolos.TipoLA.LOGICO;

                if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO)
                    return TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL)
                    return TabelaDeSimbolos.TipoLA.PONTEIRO_REAL;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO)
                    return TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL)
                    return TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL;
            }
        }
        else{
            String SemDimensao = "";

            for(var identificadorCtx: ctx.IDENT())

                SemDimensao += identificadorCtx.getText();

            for (var xp : ctx.dimensao().exp_aritmetica())
                verificarTipo(tabelaDeSimbolos, xp);


            if (!tabelaDeSimbolos.existe(SemDimensao)){
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + SemDimensao + "nao declarado\n");
            }
            else{
                EntradaTabelaDeSimbolos ident = tabelaDeSimbolos.verificar(SemDimensao);
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.INTEIRO)
                    return TabelaDeSimbolos.TipoLA.INTEIRO;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.LITERAL)
                    return TabelaDeSimbolos.TipoLA.LITERAL;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.REAL)
                    return TabelaDeSimbolos.TipoLA.REAL;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.LOGICO)
                    return TabelaDeSimbolos.TipoLA.LOGICO;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO)
                    return TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL)
                    return TabelaDeSimbolos.TipoLA.PONTEIRO_REAL;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO)
                    return TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO;
                if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL)
                    return TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL;
            }

        }
        return null;

    }

    // Verifica o tipo de uma expressão em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.ExpressaoContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;
        for (Termo_logicoContext t1 : ctx.termo_logico()) {
            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabelaDeSimbolos, t1);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret, aux)) {
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }

        return ret;
    }

    // Verifica o tipo de um termo lógico em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.Termo_logicoContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;
        for (Fator_logicoContext fL : ctx.fator_logico()) {
            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabelaDeSimbolos, fL);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret, aux)) {
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }

        return ret;
    }

    // Verifica o tipo de um fator lógico em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.Fator_logicoContext ctx) {
        return verificarTipo(tabelaDeSimbolos, ctx.parcela_logica());
    }

    // Verifica o tipo de uma parcela lógica em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.Parcela_logicaContext ctx) {
        if (ctx.exp_relacional() != null) {
            return verificarTipo(tabelaDeSimbolos, ctx.exp_relacional());
        } else {
            return TabelaDeSimbolos.TipoLA.LOGICO;
        }
    }

    // Verifica o tipo de uma expressão relacional em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.Exp_relacionalContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;
        if (ctx.exp_aritmetica().size() == 1)
            for (Exp_aritmeticaContext ea : ctx.exp_aritmetica()) {
                TipoLA aux = verificarTipo(tabelaDeSimbolos, ea);
                if (ret == null) {
                    ret = aux;
                } else if (!verificarTipo(ret, aux)) {
                    ret = TabelaDeSimbolos.TipoLA.INVALIDO;
                }
            }
        else {
            for (Exp_aritmeticaContext ea : ctx.exp_aritmetica()) {
                verificarTipo(tabelaDeSimbolos, ea);
            }

            return TabelaDeSimbolos.TipoLA.LOGICO;
        }

        return ret;
    }

    // Verifica o tipo de uma expressão aritmética em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.Exp_aritmeticaContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        for (TermoContext te : ctx.termo()) {
            TipoLA aux = verificarTipo(tabelaDeSimbolos, te);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret, aux)) {
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }
        return ret;
    }

    // Verifica o tipo de um termo em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.TermoContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        for (FatorContext fa : ctx.fator()) {
            TipoLA aux = verificarTipo(tabelaDeSimbolos, fa);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret, aux)) {
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }
        return ret;
    }

    // Verifica o tipo de um fator em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.FatorContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        for (ParcelaContext pa : ctx.parcela()) {
            TipoLA aux = verificarTipo(tabelaDeSimbolos, pa);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret, aux)) {
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }

        return ret;
    }

    // Verifica o tipo de uma parcela em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.ParcelaContext ctx) {

        if (ctx.parcela_unario() != null) {
            return verificarTipo(tabelaDeSimbolos, ctx.parcela_unario());
        } else {
            return verificarTipo(tabelaDeSimbolos, ctx.parcela_nao_unario());
        }
    }

    // Verifica o tipo de uma parcela unária em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.Parcela_unarioContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        if (ctx.NUM_INT() != null) {
            return TabelaDeSimbolos.TipoLA.INTEIRO;
        }
        if (ctx.NUM_REAL() != null) {
            return TabelaDeSimbolos.TipoLA.REAL;
        }
        if (ctx.IDENT() != null) {
            // function
            if (!tabelaDeSimbolos.existe(ctx.IDENT().getText())) {
                adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(),
                        "identificador " + ctx.IDENT().getText() + " nao declarado\n");
            }

            for (ExpressaoContext exp : ctx.expressao()) {
                TipoLA aux = verificarTipo(tabelaDeSimbolos, exp);
                if (ret == null) {
                    ret = aux;
                } else if (!verificarTipo(ret, aux)) {
                    ret = TabelaDeSimbolos.TipoLA.INVALIDO;
                }
            }

            if (tabelaDeSimbolos.existe(ctx.IDENT().getText())) {
                // return type
                EntradaTabelaDeSimbolos function = tabelaDeSimbolos.verificar(ctx.IDENT().getText()); 
                switch (function.TipoFuncao) {
                    case "inteiro":
                        ret = TabelaDeSimbolos.TipoLA.INTEIRO;
                        break;
                    case "literal":
                        ret = TabelaDeSimbolos.TipoLA.LITERAL;
                        break;
                    case "real":
                        ret = TabelaDeSimbolos.TipoLA.REAL;
                        break;
                    case "logico":
                        ret = TabelaDeSimbolos.TipoLA.LOGICO;
                        break;
                    case "^logico":
                        ret = TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO;
                        break;
                    case "^real":
                        ret = TabelaDeSimbolos.TipoLA.PONTEIRO_REAL;
                        break;
                    case "^literal":
                        ret = TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL;
                        break;
                    case "^inteiro":
                        ret = TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO;
                        break;
                    default:
                        ret = TabelaDeSimbolos.TipoLA.REGISTRO;
                        break;
                }

                String nameFun = ctx.IDENT().getText();
                EntradaTabelaDeSimbolos funProc = tabelaDeSimbolos.verificar(nameFun);

                ArrayList<TabelaDeSimbolos.TipoLA> parameterTypes = new ArrayList<>();

                for (ExpressaoContext exp : ctx.expressao()) {
                    parameterTypes.add(verificarTipo(tabelaDeSimbolos, exp));
                }

                if (!funProc.argsRegFunc.validar(parameterTypes)) {
                    adicionarErroSemantico(ctx.IDENT().getSymbol(),
                            "incompatibilidade de parametros na chamada de " + nameFun + "\n");
                }
            }
        }

        if (ctx.identificador() != null) {
            return verificarTipo(tabelaDeSimbolos, ctx.identificador());
        }

        if (ctx.IDENT() == null && ctx.expressao() != null) {
            for (ExpressaoContext exp : ctx.expressao()) {
                return verificarTipo(tabelaDeSimbolos, exp);
            }
        }

        return ret;
    }

    // Verifica se os tipos de atribuição são válidos
    public static boolean verificarTipo(TabelaDeSimbolos.TipoLA tipo1, TabelaDeSimbolos.TipoLA tipo2) {
        if (tipo1 == tipo2)
            return true;
        if (tipo1 == TabelaDeSimbolos.TipoLA.NAO_DECLARADO || tipo2 == TabelaDeSimbolos.TipoLA.NAO_DECLARADO)
            return true;
        if (tipo1 == TabelaDeSimbolos.TipoLA.INVALIDO || tipo2 == TabelaDeSimbolos.TipoLA.INVALIDO)
            return false;
        if ((tipo1 == TabelaDeSimbolos.TipoLA.INTEIRO || tipo1 == TabelaDeSimbolos.TipoLA.REAL) &&
                (tipo2 == TabelaDeSimbolos.TipoLA.INTEIRO || tipo2 == TabelaDeSimbolos.TipoLA.REAL))
            return true;
        if((
            tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO || 
            tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL || 
            tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO || 
            tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO
            ) && tipo2 == TabelaDeSimbolos.TipoLA.ENDERECO
            )
            return true;
            if ( 
                (
                    tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO
                    || 
                    tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL 
                    || 
                    tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO
                    || 
                    tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO
                ) 
                && 
                tipo2 == TabelaDeSimbolos.TipoLA.ENDERECO
            )
            return true;


        if (tipo1 != tipo2)
            return false;

        return true;
    }

    // Verifica o tipo de uma parcela não unária em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela,
            LAParser.Parcela_nao_unarioContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        if (ctx.CADEIA() != null) {
            ret = TabelaDeSimbolos.TipoLA.LITERAL;
        }
        else{
            ret = verificarTipo(tabela, ctx.identificador());
            if (ctx.getText().contains("&")){
                return TabelaDeSimbolos.TipoLA.ENDERECO;
            }
        }


        return ret;
    }
}


// package br.ufscar.dc.compiladores.LA;


// import java.util.ArrayList;
// import java.util.List;

// import org.antlr.v4.runtime.Token;
// import org.antlr.v4.runtime.tree.TerminalNode;

// import br.ufscar.dc.compiladores.LA.LAParser.Exp_aritmeticaContext;
// import br.ufscar.dc.compiladores.LA.LAParser.ExpressaoContext;
// import br.ufscar.dc.compiladores.LA.LAParser.FatorContext;
// import br.ufscar.dc.compiladores.LA.LAParser.Fator_logicoContext;
// import br.ufscar.dc.compiladores.LA.LAParser.IdentificadorContext;
// import br.ufscar.dc.compiladores.LA.LAParser.ParcelaContext;
// import br.ufscar.dc.compiladores.LA.LAParser.TermoContext;
// import br.ufscar.dc.compiladores.LA.LAParser.Termo_logicoContext;
// import br.ufscar.dc.compiladores.LA.SymbolTable.TypeLAVariable;

// public class LASemanticUtils {
//     //  Criação de uma lista para armazenar os erros semânticos
//     public static List<String> semanticErrors = new ArrayList<>();
    
//     // Adiciona um erro semântico à lista de erros. Recebe um Token e uma 
//     // mensagem como parâmetros, obtém o número da linha do token e adiciona 
//     // o erro formatado à lista.
//     public static void addSemanticError(Token t, String msg) {
//         int line = t.getLine();
//         semanticErrors.add(String.format("Linha %d: %s", line, msg));
//     }

//     // Obtém o tipo do símbolo a partir da tabela de símbolos
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable symbolTable,
//             LAParser.IdentificadorContext ctx) {
//         String identifier = ctx.getText();

//         if (!identifier.contains("[") && !identifier.contains("]")){
//             //No dimensions
//             String[] part = identifier.split("\\.");

//             if(!symbolTable.exists(part[0])){
//                 addSemanticError(ctx.IDENT(0).getSymbol(), "identificador " + identifier + " nao declarado\n");
//             }
//             else{
//                 SymbolTableEntry ident = symbolTable.check(part[0]);
//                 if(ident.identifierType == SymbolTable.TypeLAIdentifier.REGISTRO 
//                 && part.length > 1){
//                     SymbolTable fields = ident.argsRegFunc;
//                     if(!fields.exists(part[1])){
//                         addSemanticError(ctx.IDENT(0).getSymbol(), "identificador " + identifier + " nao declarado\n");
//                     }
//                     else{
//                         SymbolTableEntry ste = fields.check(part[1]);
//                         if (ste.variableType == SymbolTable.TypeLAVariable.INTEIRO)
//                             return SymbolTable.TypeLAVariable.INTEIRO;
//                         if (ste.variableType == SymbolTable.TypeLAVariable.LITERAL)
//                             return SymbolTable.TypeLAVariable.LITERAL;
//                         if (ste.variableType == SymbolTable.TypeLAVariable.REAL)
//                             return SymbolTable.TypeLAVariable.REAL;
//                         if (ste.variableType == SymbolTable.TypeLAVariable.LOGICO)
//                             return SymbolTable.TypeLAVariable.LOGICO;
//                         if (ste.variableType == SymbolTable.TypeLAVariable.PONT_INTE)
//                             return SymbolTable.TypeLAVariable.PONT_INTE;
//                         if (ste.variableType == SymbolTable.TypeLAVariable.PONT_REAL)
//                             return SymbolTable.TypeLAVariable.PONT_REAL;
//                         if (ste.variableType == SymbolTable.TypeLAVariable.PONT_LOGI)
//                             return SymbolTable.TypeLAVariable.PONT_LOGI;
//                         if (ste.variableType == SymbolTable.TypeLAVariable.PONT_LITE)
//                             return SymbolTable.TypeLAVariable.PONT_LITE;
//                     }
//                 }
//                 if (ident.identifierType == SymbolTable.TypeLAIdentifier.REGISTRO
//                         && part.length == 1) {
//                     return SymbolTable.TypeLAVariable.REGISTRO;
//                 }
//                 if (ident.variableType == SymbolTable.TypeLAVariable.INTEIRO)
//                     return SymbolTable.TypeLAVariable.INTEIRO;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.LITERAL)
//                     return SymbolTable.TypeLAVariable.LITERAL;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.REAL)
//                     return SymbolTable.TypeLAVariable.REAL;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.LOGICO)
//                     return SymbolTable.TypeLAVariable.LOGICO;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.PONT_INTE)
//                     return SymbolTable.TypeLAVariable.PONT_INTE;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.PONT_REAL)
//                     return SymbolTable.TypeLAVariable.PONT_REAL;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.PONT_LOGI)
//                     return SymbolTable.TypeLAVariable.PONT_LOGI;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.PONT_LITE)
//                     return SymbolTable.TypeLAVariable.PONT_LITE;
//             }
//         }
//         else{
//             // With dimension
//             String identifierNoDim = "";
//             // Ignores dimension and sees if variable already declared
//             for (TerminalNode identCtx : ctx.IDENT())
//                 identifierNoDim += identCtx.getText();

//             for (Exp_aritmeticaContext xp : ctx.dimensao().exp_aritmetica())
//                 verifyType(symbolTable, xp);

//             if (!symbolTable.exists(identifierNoDim)) {
//                 addSemanticError(ctx.IDENT(0).getSymbol(), "identificador " + identifierNoDim + " nao declarado\n");
//             }
//             else{
//                 SymbolTableEntry ident = symbolTable.check(identifierNoDim);
//                 if (ident.variableType == SymbolTable.TypeLAVariable.INTEIRO)
//                     return SymbolTable.TypeLAVariable.INTEIRO;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.LITERAL)
//                     return SymbolTable.TypeLAVariable.LITERAL;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.REAL)
//                     return SymbolTable.TypeLAVariable.REAL;
//                 if (ident.variableType == SymbolTable.TypeLAVariable.LOGICO)
//                     return SymbolTable.TypeLAVariable.LOGICO;
//             }
//         }   
//         return SymbolTable.TypeLAVariable.NAO_DECLARADO;
//     }

//     // Verifica o tipo em contexto de expressão
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable symbolTable,
//             LAParser.ExpressaoContext ctx) {
//         SymbolTable.TypeLAVariable ret = null;
//         for (Termo_logicoContext tl : ctx.termo_logico()) {
//             SymbolTable.TypeLAVariable aux = verifyType(symbolTable, tl);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verifyType(ret, aux)) {
//                 ret = SymbolTable.TypeLAVariable.INVALIDO;
//             }
//         }

//         return ret;
//     }

//     // Verifica o tipo em contexto de termo lógico
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable symbolTable, LAParser.Termo_logicoContext ctx){
//         SymbolTable.TypeLAVariable ret = null;
//         for (Fator_logicoContext fL : ctx.fator_logico()){
//             SymbolTable.TypeLAVariable aux = verifyType(symbolTable, fL);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verifyType(ret, aux)) {
//                 ret = SymbolTable.TypeLAVariable.INVALIDO;
//             }
//         }

//         return ret;
//     }

//     // Verifica o tipo em contexto de fator lógico
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable table,
//             LAParser.Fator_logicoContext ctx) {
//         return verifyType(table, ctx.parcela_logica());
//     }

//     // Verifica o tipo em contexto de parcela lógica
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable table,
//             LAParser.Parcela_logicaContext ctx) {
//         if (ctx.exp_relacional() != null) {
//             return verifyType(table, ctx.exp_relacional());
//         } else {
//             return SymbolTable.TypeLAVariable.LOGICO;
//         }
//     }

//     // Verifica o tipo em contexto de expressão relacional
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable table,
//             LAParser.Exp_relacionalContext ctx) {
//         SymbolTable.TypeLAVariable ret = null;
//         if (ctx.exp_aritmetica().size() == 1)
//             for (Exp_aritmeticaContext ea : ctx.exp_aritmetica()) {
//                 TypeLAVariable aux = verifyType(table, ea);
//                 if (ret == null) {
//                     ret = aux;
//                 } else if (!verifyType(ret, aux)) {
//                     ret = SymbolTable.TypeLAVariable.INVALIDO;
//                 }
//             } else {
//             for (Exp_aritmeticaContext ea : ctx.exp_aritmetica()) {
//                 verifyType(table, ea);
//             }

//             return SymbolTable.TypeLAVariable.LOGICO;
//         }

//         return ret;
//     }

//     // Verifica o tipo em contexto de expressão aritmética
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable table,
//             LAParser.Exp_aritmeticaContext ctx) {
//         SymbolTable.TypeLAVariable ret = null;

//         for (TermoContext te : ctx.termo()) {
//             TypeLAVariable aux = verifyType(table, te);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verifyType(ret, aux)) {
//                 ret = SymbolTable.TypeLAVariable.INVALIDO;
//             }
//         }
//         return ret;
//     }
//     // Verifica o tipo em contexto de termo
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable table,
//             LAParser.TermoContext ctx) {
//         SymbolTable.TypeLAVariable ret = null;

//         for (FatorContext fa : ctx.fator()) {
//             TypeLAVariable aux = verifyType(table, fa);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verifyType(ret, aux)) {
//                 ret = SymbolTable.TypeLAVariable.INVALIDO;
//             }
//         }
//         return ret;
//     }

//     // Verifica o tipo em contexto de fator
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable table,
//             LAParser.FatorContext ctx) {
//         SymbolTable.TypeLAVariable ret = null;

//         for (ParcelaContext pa : ctx.parcela()) {
//             TypeLAVariable aux = verifyType(table, pa);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verifyType(ret, aux)) {
//                 ret = SymbolTable.TypeLAVariable.INVALIDO;
//             }
//         }

//         return ret;
//     }

//     // Verifica o tipo em contexto de parcela
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable table,
//             LAParser.ParcelaContext ctx) {

//         if (ctx.parcela_unario() != null) {
//             return verifyType(table, ctx.parcela_unario());
//         } else {
//             return verifyType(table, ctx.parcela_nao_unario());
//         }
//     }

//     // Verifica o tipo em contexto de parcela unária
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable symbolTable,
//             LAParser.Parcela_unarioContext ctx) {
//         SymbolTable.TypeLAVariable ret = null;

//         if (ctx.NUM_INT() != null) {
//             return SymbolTable.TypeLAVariable.INTEIRO;
//         }
//         if (ctx.NUM_REAL() != null) {
//             return SymbolTable.TypeLAVariable.REAL;
//         }
//         if (ctx.IDENT() != null) {
//             // function
//             if (!symbolTable.exists(ctx.IDENT().getText())) {
//                 addSemanticError(ctx.identificador().IDENT(0).getSymbol(),
//                         "identificador " + ctx.IDENT().getText() + " nao declarado\n");
//             }

//             for (ExpressaoContext exp : ctx.expressao()) {
//                 TypeLAVariable aux = verifyType(symbolTable, exp);
//                 if (ret == null) {
//                     ret = aux;
//                 } else if (!verifyType(ret, aux)) {
//                     ret = SymbolTable.TypeLAVariable.INVALIDO;
//                 }
//             }

//             if (symbolTable.exists(ctx.IDENT().getText())) {
//                 // return type
//                 SymbolTableEntry function = symbolTable.check(ctx.IDENT().getText()); 
//                 switch (function.functionType) {
//                     case "inteiro":
//                         ret = SymbolTable.TypeLAVariable.INTEIRO;
//                         break;
//                     case "literal":
//                         ret = SymbolTable.TypeLAVariable.LITERAL;
//                         break;
//                     case "real":
//                         ret = SymbolTable.TypeLAVariable.REAL;
//                         break;
//                     case "logico":
//                         ret = SymbolTable.TypeLAVariable.LOGICO;
//                         break;
//                     case "^logico":
//                         ret = SymbolTable.TypeLAVariable.PONT_LOGI;
//                         break;
//                     case "^real":
//                         ret = SymbolTable.TypeLAVariable.PONT_REAL;
//                         break;
//                     case "^literal":
//                         ret = SymbolTable.TypeLAVariable.PONT_LITE;
//                         break;
//                     case "^inteiro":
//                         ret = SymbolTable.TypeLAVariable.PONT_INTE;
//                         break;
//                     default:
//                         ret = SymbolTable.TypeLAVariable.REGISTRO;
//                         break;
//                 }

//                 // Parameter type and number
//                 String nameFun = ctx.IDENT().getText();
//                 SymbolTableEntry funProc = symbolTable.check(nameFun);

//                 ArrayList<SymbolTable.TypeLAVariable> parameterTypes = new ArrayList<>();

//                 for (ExpressaoContext exp : ctx.expressao()) {
//                     parameterTypes.add(verifyType(symbolTable, exp));
//                 }

//                 if (!funProc.argsRegFunc.validType(parameterTypes)) {
//                     addSemanticError(ctx.IDENT().getSymbol(),
//                             "incompatibilidade de parametros na chamada de " + nameFun + "\n");
//                 }
//             }
//         }

//         if (ctx.identificador() != null) {
//             return verifyType(symbolTable, ctx.identificador());
//         }

//         if (ctx.IDENT() == null && ctx.expressao() != null) {
//             for (ExpressaoContext exp : ctx.expressao()) {
//                 return verifyType(symbolTable, exp);
//             }
//         }

//         return ret;
//     }

//     // Verifica se os tipos de atribuição são válidos
//     public static boolean verifyType(SymbolTable.TypeLAVariable tipo1, SymbolTable.TypeLAVariable tipo2) {
//         if (tipo1 == tipo2)
//             return true;
//         if (tipo1 == SymbolTable.TypeLAVariable.NAO_DECLARADO
//                 || tipo2 == SymbolTable.TypeLAVariable.NAO_DECLARADO)
//             return true;
//         if (tipo1 == SymbolTable.TypeLAVariable.INVALIDO || tipo2 == SymbolTable.TypeLAVariable.INVALIDO)
//             return false;
//         if ((tipo1 == SymbolTable.TypeLAVariable.INTEIRO || tipo1 == SymbolTable.TypeLAVariable.REAL) &&
//                 (tipo2 == SymbolTable.TypeLAVariable.INTEIRO || tipo2 == SymbolTable.TypeLAVariable.REAL))
//             return true;
//         if ( 
//                 (
//                     tipo1 == SymbolTable.TypeLAVariable.PONT_INTE 
//                     || 
//                     tipo1 == SymbolTable.TypeLAVariable.PONT_REAL 
//                     || 
//                     tipo1 == SymbolTable.TypeLAVariable.PONT_LOGI 
//                     || 
//                     tipo1 == SymbolTable.TypeLAVariable.PONT_LOGI 
//                 ) 
//                 && 
//                 tipo2 == SymbolTable.TypeLAVariable.ENDERECO
//             )
//             return true;
//         if (tipo1 != tipo2)
//             return false;

//         return true;
//     }

//     // Verifica o tipo em contexto de parcela não unária
//     public static SymbolTable.TypeLAVariable verifyType(SymbolTable table,
//             LAParser.Parcela_nao_unarioContext ctx) {
//         SymbolTable.TypeLAVariable ret = null;

//         if (ctx.CADEIA() != null) {
//             ret = SymbolTable.TypeLAVariable.LITERAL;
//         } 
//         else {
//             ret = verifyType(table, ctx.identificador());
//             if (ctx.getText().contains("&")) {
//                 return SymbolTable.TypeLAVariable.ENDERECO;
//             }
//         }
//         return ret;
//     }

// }