package br.ufscar.dc.compiladores.la.semantico;

import br.ufscar.dc.compiladores.la.semantico.TabelaDeSimbolos.TipoLA;

public class LASemantico extends LABaseVisitor<Void> {

    Escopo escopos = new Escopo();
    TabelaDeSimbolos tabelaDeSimbolos;

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        for (var ctxIdentVariable : ctx.variavel().identificador()) {
            var varIdent = "";
            for (var ident : ctxIdentVariable.IDENT())
                varIdent += ident.getText();
            var escopoAtual = escopos.obterEscopoAtual();

            if (escopoAtual.existe(varIdent)) {
                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                        "identificador " + varIdent + " ja declarado anteriormente\n");
            } else {
                var varTipo = ctx.variavel().tipo().getText();
                if (varTipo.equals("inteiro")) {
                    escopoAtual.adicionar(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.INTEIRO);
                } else if (varTipo.equals("literal")) {
                    escopoAtual.adicionar(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LITERAL);
                } else if (varTipo.equals("real")) {
                    escopoAtual.adicionar(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.REAL);
                } else if (varTipo.equals("logico")) {
                    escopoAtual.adicionar(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LOGICO);
                } else {
                    if (escopoAtual.existe(varTipo) && escopoAtual.verificar(varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                        if (escopoAtual.existe(varIdent)) {
                            LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                    "identificador " + varIdent + " ja declarado anteriormente\n");
                        }
                    }

                    if (!escopoAtual.existe(varTipo)) {
                        LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                "tipo " + varTipo + " nao declarado\n");
                        escopoAtual.adicionar(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                TabelaDeSimbolos.TipoLA.INVALIDO);
                    }
                }
            }
        }

        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitCmd(LAParser.CmdContext ctx) {
        if (ctx.cmdLeia() != null) {
            var escopoAtual = escopos.obterEscopoAtual();
            for (var ident : ctx.cmdLeia().identificador()) {
                LASemanticoUtils.verificarTipo(escopoAtual, ident);
            }
        }

        if (ctx.cmdAtribuicao() != null) {
            var escopoAtual = escopos.obterEscopoAtual();
            var leftValue = LASemanticoUtils.verificarTipo(escopoAtual,
                    ctx.cmdAtribuicao().identificador());
            var rightValue = LASemanticoUtils.verificarTipo(escopoAtual,
                    ctx.cmdAtribuicao().expressao());
            var atribuition = ctx.cmdAtribuicao().getText().split("<-");
            if (!LASemanticoUtils.verificarTipo(leftValue, rightValue) && !atribuition[0].contains("^")) {
                LASemanticoUtils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(),
                        "atribuicao nao compativel para " + ctx.cmdAtribuicao().identificador().getText() + "\n");
            }
            
        }

        return super.visitCmd(ctx);
    }

    @Override
    public Void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx){
        var escopoAtual = escopos.obterEscopoAtual();
        LASemanticoUtils.verificarTipo(escopoAtual, ctx);
        return super.visitExp_aritmetica(ctx);
    }
    
}