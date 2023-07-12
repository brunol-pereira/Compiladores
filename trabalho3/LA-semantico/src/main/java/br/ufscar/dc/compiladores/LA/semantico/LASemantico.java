package br.ufscar.dc.compiladores.LA.semantico;

import br.ufscar.dc.compiladores.LA.semantico.TabelaDeSimbolos.TipoLA;

public class LASemantico extends LABaseVisitor<Void> {

    Escopo escopos = new Escopo();
    TabelaDeSimbolos tabelaDeSimbolos;

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        // Lógica para a regra "declaracao_local"
        for (var ctxIdentVariable : ctx.variavel().identificador()) {
            var variableIdentifier = "";
            for (var ident : ctxIdentVariable.IDENT())
                variableIdentifier += ident.getText();
            var currentScope = escopos.obterEscopoAtual();

            // Verifica se o identificador da variável já havia sido declarado
            if (currentScope.existe(variableIdentifier)) {
                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                        "identificador " + variableIdentifier + " ja declarado anteriormente\n");
            } else {
                var varTipo = ctx.variavel().tipo().getText();
                switch (varTipo) {
                    // Se a variável for de um dos tipos abaixo ela é adicionada ao escopo atual
                    case "inteiro":
                        currentScope.adicionar(variableIdentifier,
                                TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.INTEIRO);
                        break;
                    case "literal":
                        currentScope.adicionar(variableIdentifier,
                                TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LITERAL);
                        break;
                    case "real":
                        currentScope.adicionar(variableIdentifier,
                                TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.REAL);
                        break;
                    case "logico":
                        currentScope.adicionar(variableIdentifier,
                                TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LOGICO);
                        break;
                    default: 
                        // Para verificar se é de outro tipo
                        if (currentScope.existe(varTipo) && currentScope.verificar(
                            varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                            // Verifica se o tipo já foi declarado antes
                            if (currentScope.existe(variableIdentifier)) {
                                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                        "identificador " + variableIdentifier + " ja declarado anteriormente\n");
                            }
                        }

                        // Se o tipo não foi declarado, retorna um erro semântico
                        if(!currentScope.existe(varTipo)){
                            LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                            "tipo " + varTipo + " nao declarado\n");
                            currentScope.adicionar(variableIdentifier,
                                        TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                        TabelaDeSimbolos.TipoLA.INVALIDO);
                        }

                        break;
                }
            }
        }

        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitCmd(LAParser.CmdContext ctx) {
        // Lógica para a regra "cmd", ou seja, o tratamento das ações a serem realizadas quando um comando é encontrado na análise do código.
        if (ctx.cmdLeia() != null) {
            // Obtém o escopo atual
            var currentScope = escopos.obterEscopoAtual();

            // Passa por todos identificadores
            for (var ident : ctx.cmdLeia().identificador()) {
                // Verificação o tipo do identificador
                LASemanticoUtils.verificarTipo(currentScope, ident);
            }
        }

        if (ctx.cmdAtribuicao() != null) {
            var currentScope = escopos.obterEscopoAtual();
            var leftValue = LASemanticoUtils.verificarTipo(currentScope,
                    ctx.cmdAtribuicao().identificador());
            var rightValue = LASemanticoUtils.verificarTipo(currentScope,
                    ctx.cmdAtribuicao().expressao());
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
        var currentScope = escopos.obterEscopoAtual();
        LASemanticoUtils.verificarTipo(currentScope, ctx);

        return super.visitExp_aritmetica(ctx);
    }
    
}
