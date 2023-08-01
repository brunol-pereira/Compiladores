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
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + identifier + " nao declarado\n");
            }

            else {
                EntradaTabelaDeSimbolos ident = tabelaDeSimbolos.verificar(part[0]);
                if (ident.estrutura == TabelaDeSimbolos.EstruturaLA.REGISTRO && part.length > 1) {
                    TabelaDeSimbolos fields = ident.argsRegFunc;
                    if (!fields.existe(part[1])) {
                        adicionarErroSemantico(ctx.IDENT(0).getSymbol(),
                                "identificador " + identifier + " nao declarado\n");
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
                adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + SemDimensao + " nao declarado\n");
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
                 if (ident.varTipo == TabelaDeSimbolos.TipoLA.REGISTRO)
                            return TabelaDeSimbolos.TipoLA.REGISTRO;
            }

        }
        return TabelaDeSimbolos.TipoLA.NAO_DECLARADO;

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
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos ts,
            LAParser.Fator_logicoContext ctx) {
        return verificarTipo(ts, ctx.parcela_logica());
    }

    // Verifica o tipo de uma parcela lógica em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos ts,
            LAParser.Parcela_logicaContext ctx) {
        if (ctx.exp_relacional() != null) {
            return verificarTipo(ts, ctx.exp_relacional());
        } else {
            return TabelaDeSimbolos.TipoLA.LOGICO;
        }
    }

    // Verifica o tipo de uma expressão relacional em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos ts,
            LAParser.Exp_relacionalContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;
        if (ctx.exp_aritmetica().size() == 1)
            for (Exp_aritmeticaContext ea : ctx.exp_aritmetica()) {
                TipoLA aux = verificarTipo(ts, ea);
                if (ret == null) {
                    ret = aux;
                } else if (!verificarTipo(ret, aux)) {
                    ret = TabelaDeSimbolos.TipoLA.INVALIDO;
                }
            }
        else {
            for (Exp_aritmeticaContext ea : ctx.exp_aritmetica()) {
                verificarTipo(ts, ea);
            }

            return TabelaDeSimbolos.TipoLA.LOGICO;
        }

        return ret;
    }

    // Verifica o tipo de uma expressão aritmética em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos ts,
            LAParser.Exp_aritmeticaContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        for (TermoContext te : ctx.termo()) {
            TipoLA aux = verificarTipo(ts, te);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret, aux)) {
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }
        return ret;
    }

    // Verifica o tipo de um termo em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos ts,
            LAParser.TermoContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        for (FatorContext fa : ctx.fator()) {
            TipoLA aux = verificarTipo(ts, fa);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret, aux)) {
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }
        return ret;
    }

    // Verifica o tipo de um fator em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos ts,
            LAParser.FatorContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        for (ParcelaContext pa : ctx.parcela()) {
            TipoLA aux = verificarTipo(ts, pa);
            if (ret == null) {
                ret = aux;
            } else if (!verificarTipo(ret, aux)) {
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }

        return ret;
    }

    // Verifica o tipo de uma parcela em um contexto específico
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos ts,
            LAParser.ParcelaContext ctx) {

        if (ctx.parcela_unario() != null) {
            return verificarTipo(ts, ctx.parcela_unario());
        } else {
            return verificarTipo(ts, ctx.parcela_nao_unario());
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
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos ts,
            LAParser.Parcela_nao_unarioContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        if (ctx.CADEIA() != null) {
            ret = TabelaDeSimbolos.TipoLA.LITERAL;
        }
        else{
            ret = verificarTipo(ts, ctx.identificador());
            if (ctx.getText().contains("&")){
                return TabelaDeSimbolos.TipoLA.ENDERECO;
            }
        }
        return ret;
    }
}

