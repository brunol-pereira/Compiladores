package br.ufscar.dc.compiladores.la.semantico;

import br.ufscar.dc.compiladores.la.semantico.TabelaDeSimbolos.TipoLA;

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

            // Verifica se o identificador da variável já foi declarado anteriormente.
            if (currentScope.existe(variableIdentifier)) {
                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                        "identificador " + variableIdentifier + " ja declarado anteriormente\n");
            } else {
                var varTipo = ctx.variavel().tipo().getText();
                // Switch-case para tratar os diferentes tipos de variáveis.
                switch (varTipo) {
                    // Se o tipo for "inteiro", "literal", "real" ou "lógico", a variável é 
                    // adicionada ao escopo atual com o tipo correspondente utilizando a 
                    // função adicionar da classe currentScope.
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
                        // Caso o tipo não seja um tipo básico
                        if (currentScope.existe(varTipo) && currentScope.verificar(
                            // Verificamos se o tipo já foi declarado anteriormente no escopo atual e se é um tipo válido.
                            varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                            if (currentScope.existe(variableIdentifier)) {
                                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                        "identificador " + variableIdentifier + " ja declarado anteriormente\n");
                            }
                        }

                        //Se o tipo não foi declarado, um erro semântico é adicionado informando que o tipo não foi declarado
                        if(!currentScope.existe(varTipo)){
                            LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                            "tipo " + varTipo + " nao declarado\n");
                            // A variável é adicionada ao escopo atual com um tipo inválido (INVALIDO).
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
            // Obtemos o escopo atual através da variável currentScope 
            var currentScope = escopos.obterEscopoAtual();

            // Iteramos sobre os identificadores presentes no comando 
            for (var ident : ctx.cmdLeia().identificador()) {
                // Verificação semântica do tipo do identificador
                LASemanticoUtils.verificarTipo(currentScope, ident);
            }
        }

        if (ctx.cmdAtribuicao() != null) {
            var currentScope = escopos.obterEscopoAtual();
            var leftValue = LASemanticoUtils.verificarTipo(currentScope,
                    ctx.cmdAtribuicao().identificador());
            var rightValue = LASemanticoUtils.verificarTipo(currentScope,
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
        var currentScope = escopos.obterEscopoAtual();
        LASemanticoUtils.verificarTipo(currentScope, ctx);

        return super.visitExp_aritmetica(ctx);
    }
    
}