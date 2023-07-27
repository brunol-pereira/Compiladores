package br.ufscar.dc.compiladores.la.semantico;

import br.ufscar.dc.compiladores.la.semantico.TabelaDeSimbolos.TipoLA;

public class LASemantico extends LABaseVisitor<Void> {

    Escopo escopos = new Escopo(); // Gerenciar os escopos
    TabelaDeSimbolos tabelaDeSimbolos; // Tabela de símbolos

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        // Trata declarações locais de variáveis
        for (var ctxIdentVariable : ctx.variavel().identificador()) {
            var varIdent = "";
            for (var ident : ctxIdentVariable.IDENT())
                varIdent += ident.getText();
            var escopoAtual = escopos.obterEscopoAtual();

            // Verifica se a variável já foi declarada anteriormente no mesmo escopo
            if (escopoAtual.existe(varIdent)) {
                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                        "identificador " + varIdent + " ja declarado anteriormente\n");
            } else {
                // Obtém o tipo da variável
                var varTipo = ctx.variavel().tipo().getText();
                // Adiciona a variável à tabela de símbolos com seu tipo correspondente
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
        // Trata comandos, como leitura e atribuição de variáveis
        if (ctx.cmdLeia() != null) {
            var escopoAtual = escopos.obterEscopoAtual();
            // Verifica se as variáveis a serem lidas foram declaradas anteriormente
            for (var ident : ctx.cmdLeia().identificador()) {
                LASemanticoUtils.verificarTipo(escopoAtual, ident);
            }
        }

        if (ctx.cmdAtribuicao() != null) {
            var escopoAtual = escopos.obterEscopoAtual();
            // Verifica se a atribuição é compatível com o tipo da variável à esquerda
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
        // Trata expressões aritméticas e verifica se os tipos dos operandos são compatíveis
        var escopoAtual = escopos.obterEscopoAtual();
        LASemanticoUtils.verificarTipo(escopoAtual, ctx);
        return super.visitExp_aritmetica(ctx);
    }
    
}
