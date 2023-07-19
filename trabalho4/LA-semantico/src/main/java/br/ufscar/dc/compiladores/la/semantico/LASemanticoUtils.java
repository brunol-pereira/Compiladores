//Forma rearranjada pelo chatgpt

package br.ufscar.dc.compiladores.la.semantico;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

public class LASemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();

    // Adiciona um erro semântico à lista de erros
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        int coluna = t.getCharPositionInLine();
        errosSemanticos.add(String.format("Linha %d, coluna %d: %s", linha, coluna, mensagem));
    }

    // Verifica o tipo de um identificador em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
            LAParser.IdentificadorContext ctx) {
        String identifier = ctx.getText();

        if (!identifier.contains("[") && !identifier.contains("]")) {
            String[] parts = identifier.split("\\.");

            if (!tabelaDeSimbolos.exists(parts[0])) {
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "Identificador '" + identifier + "' não declarado");
            } else {
                EntradaTabelaDeSimbolos ident = tabelaDeSimbolos.check(parts[0]);
                if (ident.estrutura == TabelaDeSimbolos.TipoLA.REGISTRO && parts.length > 1) {
                    TabelaDeSimbolos fields = ident.argsRegFunc;
                    if (!fields.exists(parts[1])) {
                        adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "Identificador '" + identifier + "' não declarado");
                    } else {
                        EntradaTabelaDeSimbolos tabela = fields.check(parts[1]);
                        return tabela.varTipo;
                    }
                } else {
                    return ident.varTipo;
                }
            }
        } else {
            String semDimensao = identifier.replaceAll("[\\[\\]]", "");

            for (var xp : ctx.dimensao().exp_aritmetica()) {
                verificarTipo(tabelaDeSimbolos, xp);
            }

            if (!tabelaDeSimbolos.exists(semDimensao)) {
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "Identificador '" + semDimensao + "' não declarado");
            } else {
                EntradaTabelaDeSimbolos ident = tabelaDeSimbolos.check(semDimensao);
                return ident.varTipo;
            }
        }

        return TabelaDeSimbolos.TipoLA.INVALIDO;
    }

    // Restante das funções de verificação de tipos...

    // Verifica se os tipos de atribuição são válidos
    public static boolean verificarTipo(TabelaDeSimbolos.TipoLA tipo1, TabelaDeSimbolos.TipoLA tipo2) {
        if (tipo1 == tipo2) {
            return true;
        }
        if (tipo1 == TabelaDeSimbolos.TipoLA.NAO_DECLARADO || tipo2 == TabelaDeSimbolos.TipoLA.NAO_DECLARADO) {
            return true;
        }
        if (tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO || tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL ||
                tipo1 == TabelaDeSimbolos.TipoLA.PONTERIO_LOGICO || tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL) {
            return tipo2 == TabelaDeSimbolos.TipoLA.ENDERECO;
        }

        adicionarErroSemantico(null, "Tipos de atribuição inválidos: " + tipo1 + " e " + tipo2);
        return false;
    }
}


//Forma que a gente tinha feito + modificações

// package br.ufscar.dc.compiladores.la.semantico;

// import java.util.ArrayList;
// import java.util.List;
// import org.antlr.v4.runtime.Token;

// public class LASemanticoUtils {
//     public static List<String> errosSemanticos = new ArrayList<>();

//     // Adiciona um erro semântico à lista de erros
//     public static void adicionarErroSemantico(Token t, String mensagem) {
//         int linha = t.getLine();
//         // int coluna = t.getCharPositionInLine();
//         errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
//     }

//     // Verifica o tipo de um identificador em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.IdentificadorContext ctx) {
//         var identifier = ctx.getText();

//         if (!identifier.contains("[") && !identifier.contains("]")) {

//             var part = identifier.split("\\.");

//             if (!tabelaDeSimbolos.exists(part[0])) {
//                 adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador" + identifier + "nao declarado\n");
//             }

//             else {
//                 EntradaTabelaDeSimbolos ident = tabelaDeSimbolos.check(part[0]);
//                 if (ident.estrutura == TabelaDeSimbolos.TipoLA.REGISTRO && part.length > 1) {
//                     TabelaDeSimbolos fields = ident.argsRegFunc;
//                     if (!fields.exists(part[1])) {
//                         adicionarErroSemantico(ctx.IDENT(0).getSymbol,
//                                 "identificador " + identifier + "nao declarado\n");
//                     } else {

//                         EntradaTabelaDeSimbolos tabela = fields.check(part[1]);
//                         if (tabela.varTipo == TabelaDeSimbolos.TipoLA.INTEIRO)
//                             return TabelaDeSimbolos.TipoLA.INTEIRO;
//                         if (tabela.varTipo == TabelaDeSimbolos.TipoLA.LITERAL)
//                             return TabelaDeSimbolos.TipoLA.LITERAL;
//                         if (tabela.varTipo == TabelaDeSimbolos.TipoLA.REAL)
//                             return TabelaDeSimbolos.TipoLA.REAL;
//                         if (tabela.varTipo == TabelaDeSimbolos.TipoLA.LOGICO)
//                             return TabelaDeSimbolos.TipoLA.LOGICO;
//                         if (tabela.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO)
//                             return TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO;
//                         if (tabela.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL)
//                             return TabelaDeSimbolos.TipoLA.PONTEIRO_REAL;
//                         if (tabela.varTipo == TabelaDeSimbolos.TipoLA.PONTERIO_LOGICO)
//                             return TabelaDeSimbolos.TipoLA.PONTERIO_LOGICO;
//                         if (tabela.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL)
//                             return TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL;
//                     }
//                 }

//                 if (ident.estrutura == TabelaDeSimbolos.TipoLA.REGISTRO && part.length == 1) {
//                     return TabelaDeSimbolos.TipoLA.REGISTRO;
//                 }
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.INTEIRO)
//                     return TabelaDeSimbolos.TipoLA.INTEIRO;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.LITERAL)
//                     return TabelaDeSimbolos.TipoLA.LITERAL;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.REAL)
//                     return TabelaDeSimbolos.TipoLA.REAL;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.LOGICO)
//                     return TabelaDeSimbolos.TipoLA.LOGICO;

//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO)
//                     return TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL)
//                     return TabelaDeSimbolos.TipoLA.PONTEIRO_REAL;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTERIO_LOGICO)
//                     return TabelaDeSimbolos.TipoLA.PONTERIO_LOGICO;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL)
//                     return TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL;
//             }
//         }
//         else{
//             var SemDimensao = "";

//             for(var identificadorCtx: ctx.IDENT())

//                 SemDimensao += identificadorCtx.getText();

//             for (var xp : ctx.dimensao().exp_aritmetica())
//                 verificarTipo(TabelaDeSimbolos, xp);

//             if (!tabelaDeSimbolos.exists(SemDimensao)){
//                 adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + SemDimensao + "nao declarado\n");
//             }
//             else{
//                 EntradaTabelaDeSimbolos ident = tabelaDeSimbolos.check(SemDimensao);
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.INTEIRO)
//                     return TabelaDeSimbolos.TipoLA.INTEIRO;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.LITERAL)
//                     return TabelaDeSimbolos.TipoLA.LITERAL;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.REAL)
//                     return TabelaDeSimbolos.TipoLA.REAL;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.LOGICO)
//                     return TabelaDeSimbolos.TipoLA.LOGICO;

//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO)
//                     return TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL)
//                     return TabelaDeSimbolos.TipoLA.PONTEIRO_REAL;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTERIO_LOGICO)
//                     return TabelaDeSimbolos.TipoLA.PONTERIO_LOGICO;
//                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL)
//                     return TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL;
//             }
//         }

//     }

//     // Verifica o tipo de uma expressão em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.ExpressaoContext ctx) {
//         TabelaDeSimbolos.TipoLA ret = null;
//         for (var tl : ctx.termo_logico()) {
//             TabelaDeSimbolos.TipoLA aux = verificarTipo(tabelaDeSimbolos, tl);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verificarTipo(ret, aux)) {
//                 ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//             }
//         }

//         return ret;
//     }

//     // Verifica o tipo de um termo lógico em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.Termo_logicoContext ctx) {
//         TabelaDeSimbolos.TipoLA ret = null;
//         for (var fL : ctx.fator_logico()) {
//             TabelaDeSimbolos.TipoLA aux = verificarTipo(tabelaDeSimbolos, fL);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verificarTipo(ret, aux)) {
//                 ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//             }
//         }

//         return ret;
//     }

//     // Verifica o tipo de um fator lógico em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.Fator_logicoContext ctx) {
//         return verificarTipo(tabelaDeSimbolos, ctx.parcela_logica());
//     }

//     // Verifica o tipo de uma parcela lógica em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.Parcela_logicaContext ctx) {
//         if (ctx.exp_relacional() != null) {
//             return verificarTipo(tabelaDeSimbolos, ctx.exp_relacional());
//         } else {
//             return TabelaDeSimbolos.TipoLA.LOGICO;
//         }
//     }

//     // Verifica o tipo de uma expressão relacional em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.Exp_relacionalContext ctx) {
//         TabelaDeSimbolos.TipoLA ret = null;
//         if (ctx.exp_aritmetica().size() == 1)
//             for (var ea : ctx.exp_aritmetica()) {
//                 var aux = verificarTipo(tabelaDeSimbolos, ea);
//                 if (ret == null) {
//                     ret = aux;
//                 } else if (!verificarTipo(ret, aux)) {
//                     ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//                 }
//             }
//         else {
//             for (var ea : ctx.exp_aritmetica()) {
//                 verificarTipo(tabelaDeSimbolos, ea);
//             }

//             return TabelaDeSimbolos.TipoLA.LOGICO;
//         }

//         return ret;
//     }

//     // Verifica o tipo de uma expressão aritmética em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.Exp_aritmeticaContext ctx) {
//         TabelaDeSimbolos.TipoLA ret = null;

//         for (var te : ctx.termo()) {
//             var aux = verificarTipo(tabelaDeSimbolos, te);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verificarTipo(ret, aux)) {
//                 ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//             }
//         }
//         return ret;
//     }

//     // Verifica o tipo de um termo em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.TermoContext ctx) {
//         TabelaDeSimbolos.TipoLA ret = null;

//         for (var fa : ctx.fator()) {
//             var aux = verificarTipo(tabelaDeSimbolos, fa);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verificarTipo(ret, aux)) {
//                 ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//             }
//         }
//         return ret;
//     }

//     // Verifica o tipo de um fator em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.FatorContext ctx) {
//         TabelaDeSimbolos.TipoLA ret = null;

//         for (var pa : ctx.parcela()) {
//             var aux = verificarTipo(tabelaDeSimbolos, pa);
//             if (ret == null) {
//                 ret = aux;
//             } else if (!verificarTipo(ret, aux)) {
//                 ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//             }
//         }

//         return ret;
//     }

//     // Verifica o tipo de uma parcela em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.ParcelaContext ctx) {

//         if (ctx.parcela_unario() != null) {
//             return verificarTipo(tabelaDeSimbolos, ctx.parcela_unario());
//         } else {
//             return verificarTipo(tabelaDeSimbolos, ctx.parcela_nao_unario());
//         }
//     }

//     // Verifica o tipo de uma parcela unária em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.Parcela_unarioContext ctx) {
//         TabelaDeSimbolos.TipoLA ret = null;

//         if (ctx.NUM_INT() != null) {
//             return TabelaDeSimbolos.TipoLA.INTEIRO;
//         }
//         if (ctx.NUM_REAL() != null) {
//             return TabelaDeSimbolos.TipoLA.REAL;
//         }
//         if (ctx.IDENT() != null) {
//             // function
//             if (!tabelaDeSimbolos.existe(ctx.IDENT().getText())) {
//                 adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(),
//                         "identificador " + ctx.IDENT().getText() + " nao declarado\n");
//             }

//             for (var exp : ctx.expressao()) {
//                 var aux = verificarTipo(tabelaDeSimbolos, exp);
//                 if (ret == null) {
//                     ret = aux;
//                 } else if (!verificarTipo(ret, aux)) {
//                     ret = TabelaDeSimbolos.TipoLA.INVALIDO;
//                 }
//             }
//         }

//         if (ctx.identificador() != null) {
//             return verificarTipo(tabelaDeSimbolos, ctx.identificador());
//         }

//         if (ctx.IDENT() == null && ctx.expressao() != null) {
//             for (var exp : ctx.expressao()) {
//                 return verificarTipo(tabelaDeSimbolos, exp);
//             }
//         }

//         return ret;
//     }

//     // Verifica se os tipos de atribuição são válidos
//     public static boolean verificarTipo(TabelaDeSimbolos.TipoLA tipo1, TabelaDeSimbolos.TipoLA tipo2) {
//         if (tipo1 == tipo2)
//             return true;
//         if (tipo1 == TabelaDeSimbolos.TipoLA.NAO_DECLARADO || tipo2 == TabelaDeSimbolos.TipoLA.NAO_DECLARADO)
//             return true;
//         if (tipo1 == TabelaDeSimbolos.TipoLA.INVALIDO || tipo2 == TabelaDeSimbolos.TipoLA.INVALIDO)
//             return false;
//         if ((tipo1 == TabelaDeSimbolos.TipoLA.INTEIRO || tipo1 == TabelaDeSimbolos.TipoLA.REAL) &&
//                 (tipo2 == TabelaDeSimbolos.TipoLA.INTEIRO || tipo2 == TabelaDeSimbolos.TipoLA.REAL))
//             return true;
//         if((
//             tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO || 
//             tipo1 == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL || 
//             tipo1 == TabelaDeSimbolos.TipoLA.PONTERIO_LOGICO || 
//             tipo1 == TabelaDeSimbolos.TipoLA.PONTERIO_LOGICO
//             ) && tipo2 == TabelaDeSimbolos.TipoLA.ENDERECO
//             )
//             return true;
//         if (tipo1 != tipo2)
//             return false;

//         return true;
//     }

//     // Verifica o tipo de uma parcela não unária em um contexto específico
//     public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos,
//             LAParser.Parcela_nao_unarioContext ctx) {
//         TabelaDeSimbolos.TipoLA ret = null;

//         if (ctx.CADEIA() != null) {
//             ret = TabelaDeSimbolos.TipoLA.LITERAL;
//         }
//         else{
//             ret = verificarTipo(tabelaDeSimbolos, ctx.identificador());
//             if (ctx.getText().contains("&")){
//                 return TabelaDeSimbolos.TipoLA.ENDERECO;
//             }
//         }


//         return ret;
//     }
// }