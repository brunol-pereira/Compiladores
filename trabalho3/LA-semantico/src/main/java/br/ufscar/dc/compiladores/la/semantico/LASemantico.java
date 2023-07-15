package br.ufscar.dc.compiladores.la.semantico;

import br.ufscar.dc.compiladores.la.semantico.TabelaDeSimbolos.TipoLA;

public class LASemantico extends LABaseVisitor<Void> {

    Escopo escopos = new Escopo();
    TabelaDeSimbolos tabelaDeSimbolos;

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        // Lógica para a regra "declaracao_local"
        for (var ctxIdentVariable : ctx.variavel().identificador()) {
            var varIdent = "";
            for (var ident : ctxIdentVariable.IDENT())
                varIdent += ident.getText();
            var escopoAtual = escopos.obterEscopoAtual();

            // Verifica se o identificador da variável já foi declarado anteriormente.
            if (escopoAtual.existe(varIdent)) {
                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                        "identificador " + varIdent + " ja declarado anteriormente\n");
            } else {
                var varTipo = ctx.variavel().tipo().getText();
                // Tratar os diferentes tipos de variáveis.
                if (varTipo.equals("inteiro")) {
                    escopoAtual.adicionar(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.INTEIRO);
                } else if (varTipo.equals("literal")) {
                    escopoAtual.adicionar(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LITERAL);
                } else if (varTipo.equals("real")) {
                    escopoAtual.adicionar(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.REAL);
                } else if (varTipo.equals("logico")) {
                    escopoAtual.adicionar(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LOGICO);
                } else {
                    // Caso o tipo não seja um tipo básico
                    if (escopoAtual.existe(varTipo) && escopoAtual.verificar(varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                        if (escopoAtual.existe(varIdent)) {
                            LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                    "identificador " + varIdent + " ja declarado anteriormente\n");
                        }
                    }

                    // Se o tipo não foi declarado, um erro semântico é adicionado informando que o tipo não foi declarado
                    if (!escopoAtual.existe(varTipo)) {
                        LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                "tipo " + varTipo + " nao declarado\n");
                        // A variável é adicionada ao escopo atual com um tipo inválido (INVALIDO).
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
        // Lógica para a regra "cmd", ou seja, o tratamento das ações a serem realizadas quando um comando é encontrado na análise do código.
        if (ctx.cmdLeia() != null) {
            // Obtemos o escopo atual através da variável escopoAtual 
            var escopoAtual = escopos.obterEscopoAtual();

            // Iteramos sobre os identificadores presentes no comando 
            for (var ident : ctx.cmdLeia().identificador()) {
                // Verificação semântica do tipo do identificador
                LASemanticoUtils.verificarTipo(escopoAtual, ident);
            }
        }

        if (ctx.cmdAtribuicao() != null) {
            var escopoAtual = escopos.obterEscopoAtual();
            var leftValue = LASemanticoUtils.verificarTipo(escopoAtual,
                    ctx.cmdAtribuicao().identificador());
            var rightValue = LASemanticoUtils.verificarTipo(escopoAtual,
                    ctx.cmdAtribuicao().expressao());
            // Verifica atribuição para ponteiros
            var atribuition = ctx.cmdAtribuicao().getText().split("<-");
            if (!LASemanticoUtils.verificarTipo(leftValue, rightValue) && !atribuition[0].contains("^")) {
                // Esse erro informa que a atribuição não é compatível para o identificador presente na atribuição.
                LASemanticoUtils.adicionarErroSemantico(ctx.cmdAtribuicao().identificador().IDENT(0).getSymbol(),
                        "atribuicao nao compativel para " + ctx.cmdAtribuicao().identificador().getText() + "\n");
            }
            
        }

        // Permite que a visita aos nós filhos da regra "cmd" seja continuada.
        return super.visitCmd(ctx);
    }

    @Override
    public Void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx){
        // Lógica para a regra "exp_aritmetica"
        var escopoAtual = escopos.obterEscopoAtual();
        LASemanticoUtils.verificarTipo(escopoAtual, ctx);

        return super.visitExp_aritmetica(ctx);
    }
    
}