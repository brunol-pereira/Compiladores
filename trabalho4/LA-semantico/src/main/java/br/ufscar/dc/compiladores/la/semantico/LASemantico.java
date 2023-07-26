// Pacote e importações do código
package br.ufscar.dc.compiladores.la.semantico;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdAtribuicaoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdChamadaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdLeiaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Decl_local_globalContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Declaracao_globalContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Exp_aritmeticaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.ExpressaoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.IdentificadorContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.VariavelContext;
import br.ufscar.dc.compiladores.la.semantico.TabelaDeSimbolos.TipoLA;;

// Definição da classe LASemantico
public class LASemantico extends LABaseVisitor<Void> {

    Escopo escopos = new Escopo(); // Objeto para gerenciar escopos
    TabelaDeSimbolos tabelaDeSimbolos; // Tabela de símbolos

    // Método para definir o tipo e adicionar à tabela de símbolos
    public Boolean defineTypeAndAddtoScope(String varIdent, String varTipo, TabelaDeSimbolos tabelaDeSimbolos) {
        // Switch-case para identificar o tipo da variável e adicioná-la à tabela de
        // símbolos conforme o tipo correspondente.
        switch (varTipo) {
            case "inteiro":
                tabelaDeSimbolos.put(varIdent,
                        TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.INTEIRO);
                break;
            case "real":
                tabelaDeSimbolos.put(varIdent,
                        TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.REAL);
                break;
            case "logico":
                tabelaDeSimbolos.put(varIdent,
                        TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LOGICO);
                break;
            case "literal":
                tabelaDeSimbolos.put(varIdent,
                        TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LITERAL);
                break;
            case "^literal":
                tabelaDeSimbolos.put(varIdent,
                        TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_LITERAL);
                break;
            case "^logico":
                tabelaDeSimbolos.put(varIdent,
                        TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_LOGICO);
                break;
            case "^real":
                tabelaDeSimbolos.put(varIdent,
                        TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_REAL);
                break;
            case "^inteiro":
                tabelaDeSimbolos.put(varIdent,
                        TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_INTEIRO);
                break;
            default:
                return false; // Caso o tipo seja inválido, retorna falso.
        }
        return true; // Se tudo ocorrer corretamente, retorna verdadeiro.
    }

    // Método para visitar e realizar ações na declaração local do programa
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        // Lógica para tratamento das declarações locais
        if (ctx.IDENT() != null) {
            // Verifica se existe um IDENT (sequência de caracteres que define um
            // identificador (nome))
            String identificador = ctx.IDENT().getText();
            TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

            if (ctx.tipo_basico() != null) {
                // constant declaration
                // 'constante' IDENT ':' tipo_basico '=' valor_constante
                if (escopoAtual.existe(identificador)) {
                    LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                            "identificador " + identificador + " ja declarado anteriormente\n");
                } else {
                    String constantType = ctx.tipo_basico().getText();
                    switch (constantType) {
                        case "inteiro":
                            escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.CONSTANTE,
                                    TipoLA.INTEIRO);
                            break;
                        case "literal":
                            escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.CONSTANTE,
                                    TipoLA.LITERAL);
                            break;
                        case "real":
                            escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.CONSTANTE,
                                    TipoLA.REAL);
                            break;
                        case "logico":
                            escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.CONSTANTE,
                                    TipoLA.LOGICO);
                            break;
                        default:
                            // Caso não entre em nenhum dos casos acima
                            break;
                    }
                }
            } else {
                // 'tipo' IDENT ':' tipo
                if (escopoAtual.existe(identificador)) {
                    LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                            "identificador " + identificador + " ja declarado anteriormente\n");
                } else {
                    TabelaDeSimbolos fieldsTypes = new TabelaDeSimbolos();
                    escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.TIPO, null, fieldsTypes);
                    for (VariavelContext variable : ctx.tipo().registro().variavel()) {
                        for (IdentificadorContext ctxIdentVariable : variable.identificador()) {
                            String varIdent = ctxIdentVariable.getText();
                            if (fieldsTypes.existe(varIdent)) {
                                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                        "identificador " + varIdent + " ja declarado anteriormente\n");
                            } else {
                                String varTipo = variable.tipo().getText();
                                if (!defineTypeAndAddtoScope(varIdent, varTipo, fieldsTypes)) {
                                    // Não faz nada
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // 'declare' variavel
            if (ctx.variavel().tipo().registro() == null) {
                // Não é registro
                for (IdentificadorContext ctxIdentVariable : ctx.variavel().identificador()) {
                    String varIdent = "";
                    for (TerminalNode ident : ctxIdentVariable.IDENT())
                        varIdent += ident.getText();
                    TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

                    if (ctxIdentVariable.dimensao() != null)
                        // Se dimensão existe
                        for (Exp_aritmeticaContext expDim : ctxIdentVariable.dimensao().exp_aritmetica())
                            LASemanticoUtils.verificarTipo(escopoAtual, expDim);

                    // Verifica se o identificador da variável já foi declarado anteriormente.
                    if (escopoAtual.existe(varIdent)) {
                        LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                "identificador " + varIdent + " ja declarado anteriormente\n");
                    } else {
                        String varTipo = ctx.variavel().tipo().getText();

                        if (!defineTypeAndAddtoScope(varIdent, varTipo, escopoAtual)) {
                            // Caso o tipo não seja um tipo básico
                            if (escopoAtual.existe(varTipo) && escopoAtual.verificar(
                                    // Verificamos se o tipo já foi declarado anteriormente no escopo atual e se é
                                    // um tipo válido.
                                    varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                                if (escopoAtual.existe(varIdent)) {
                                    LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                            "identificador " + varIdent + " ja declarado anteriormente\n");
                                } else {
                                    EntradaTabelaDeSimbolos entry = escopoAtual.verificar(varTipo);
                                    TabelaDeSimbolos fieldsType = entry.argsRegFunc;
                                    escopoAtual.put(varIdent,
                                            TabelaDeSimbolos.EstruturaLA.REGISTRO, null, fieldsType);
                                }
                            }

                            // Se o tipo não foi declarado, um erro semântico é adicionado informando que o
                            // tipo não foi declarado
                            if (!escopoAtual.existe(varTipo)) {
                                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                        "tipo " + varTipo + " nao declarado\n");
                                // A variável é adicionada ao escopo atual com um tipo inválido (INVALIDO).
                                escopoAtual.put(varIdent,
                                        TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                        TabelaDeSimbolos.TipoLA.INVALIDO);
                            }
                        }
                    }
                }
            } else {
                ArrayList<String> registroidentificadores = new ArrayList<>();
                for (IdentificadorContext ctxIdentReg : ctx.variavel().identificador()) {
                    String identificadorNome = ctxIdentReg.getText();
                    TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

                    if (escopoAtual.existe(identificadorNome)) {
                        // Identificador único
                        LASemanticoUtils.adicionarErroSemantico(ctxIdentReg.IDENT(0).getSymbol(),
                                "identificador " + identificadorNome + " ja declarado anteriormente\n");
                    } else {
                        TabelaDeSimbolos fields = new TabelaDeSimbolos();
                        escopoAtual.put(identificadorNome, TabelaDeSimbolos.EstruturaLA.REGISTRO, null,
                                fields);
                        registroidentificadores.add(identificadorNome);
                    }
                }

                for (VariavelContext ctxVariableRegister : ctx.variavel().tipo().registro().variavel()) {
                    for (IdentificadorContext ctxVariableRegisterIdent : ctxVariableRegister.identificador()) {
                        String registerFieldName = ctxVariableRegisterIdent.getText();
                        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

                        for (String registroidentificador : registroidentificadores) {
                            EntradaTabelaDeSimbolos entry = escopoAtual.verificar(registroidentificador);
                            TabelaDeSimbolos registerFields = entry.argsRegFunc;

                            if (registerFields.existe(registerFieldName)) {
                                LASemanticoUtils.adicionarErroSemantico(ctxVariableRegisterIdent.IDENT(0).getSymbol(),
                                        "identificador " + registerFieldName + " ja declarado anteriormente\n");
                            } else {
                                String varTipo = ctxVariableRegister.tipo().getText();
                                if (!defineTypeAndAddtoScope(registerFieldName, varTipo, registerFields)) {
                                    if (!escopoAtual.existe(varTipo)) {
                                        LASemanticoUtils.adicionarErroSemantico(
                                                ctxVariableRegisterIdent.IDENT(0).getSymbol(),
                                                "tipo " + varTipo + " nao declarado\n");
                                        escopoAtual.put(registerFieldName,
                                                TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                                TabelaDeSimbolos.TipoLA.INVALIDO);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.visitDeclaracao_local(ctx);
    }

    // Método para visitar e realizar ações na declaração global do programa
    @Override
    public Void visitDeclaracao_global(Declaracao_globalContext ctx) {
        // Lógica para tratamento das declarações globais
        String identificador = ctx.IDENT().getText();

        // Obtendo os escopos
        List<TabelaDeSimbolos> scopes = escopos.percorrerEscopoAninhados();
        if (scopes.size() > 1) {
            escopos.obterEscopoAtual();
        }
        TabelaDeSimbolos globalScope = escopos.obterEscopoAtual();

        if (ctx.tipo_estendido() != null) {
            escopos.criarNovoEscopo();
            TabelaDeSimbolos functionScope = escopos.obterEscopoAtual();
            functionScope.setGlobal(globalScope); // Adiciona um escopo global

            if (globalScope.existe(identificador)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                        "identificador " + identificador + " ja declarado anteriormente\n");
            } else {
                TabelaDeSimbolos funcParameters = new TabelaDeSimbolos();
                globalScope.put(identificador, TabelaDeSimbolos.EstruturaLA.FUNCAO, null, funcParameters,
                        ctx.tipo_estendido().getText());

                for (LAParser.ParametroContext declaredParameter : ctx.parametros().parametro()) {
                    String varTipo = declaredParameter.tipo_estendido().getText();

                    for (LAParser.IdentificadorContext ident : declaredParameter.identificador()) {
                        // Depois de declarar o tipo do parâmetro, podemos declarar múltiplos parâmetros do mesmo tipo
                        String parametroIdentificador = ident.getText();

                        if (functionScope.existe(parametroIdentificador)) {
                            // Outro parâmetro com mesmo nome que já tenha sido definido
                            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                                    "identificador " + parametroIdentificador + " ja declarado anteriormente\n");
                        } else {
                            if (defineTypeAndAddtoScope(parametroIdentificador, varTipo, functionScope)) {
                                // Caso consiga definir os tipos para o escopo da função, reproduz para os
                                // parâmetros
                                defineTypeAndAddtoScope(parametroIdentificador, varTipo, funcParameters);
                            } else {
                                // Caso não seja um dos tipo_estendido
                                if (globalScope.existe(varTipo) && globalScope.verificar(
                                        varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                                    if (functionScope.existe(parametroIdentificador)) {
                                        LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),
                                                "identificador " + parametroIdentificador
                                                        + " ja declarado anteriormente\n");
                                    } else {
                                        EntradaTabelaDeSimbolos fields = globalScope.verificar(varTipo);
                                        TabelaDeSimbolos nestedTableType = fields.argsRegFunc;

                                        functionScope.put(parametroIdentificador,
                                                TabelaDeSimbolos.EstruturaLA.REGISTRO,
                                                TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType,
                                                varTipo);
                                        funcParameters.put(parametroIdentificador,
                                                TabelaDeSimbolos.EstruturaLA.REGISTRO,
                                                TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType,
                                                varTipo);
                                    }
                                }
                                if (!globalScope.existe(varTipo)) {
                                    LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),
                                            "tipo " + varTipo + " nao declarado\n");
                                    functionScope.put(parametroIdentificador,
                                            TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                            TabelaDeSimbolos.TipoLA.INVALIDO);
                                    funcParameters.put(parametroIdentificador,
                                            TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                            TabelaDeSimbolos.TipoLA.INVALIDO);
                                }
                            }
                        }

                    }
                }
            }

        } else {
            escopos.criarNovoEscopo();
            TabelaDeSimbolos procScope = escopos.obterEscopoAtual();
            procScope.setGlobal(globalScope); // Adiciona um escopo global

            if (globalScope.existe(identificador)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                        "identificador " + identificador + " ja declarado anteriormente\n");
            } else {
                TabelaDeSimbolos procParameters = new TabelaDeSimbolos();
                globalScope.put(identificador, TabelaDeSimbolos.EstruturaLA.PROCEDIMENTO, null, procParameters);

                for (LAParser.ParametroContext declaredParameter : ctx.parametros().parametro()) {
                    String varTipo = declaredParameter.tipo_estendido().getText();

                    for (LAParser.IdentificadorContext ident : declaredParameter.identificador()) {
                         // Depois de declarar o tipo do parâmetro, podemos declarar múltiplos parâmetros do mesmo tipo
                        String parametroIdentificador = ident.getText();

                        if (procScope.existe(parametroIdentificador)) {
                            // Outro parâmetro com mesmo nome que já tenha sido definido
                            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                                    "identificador " + parametroIdentificador + " ja declarado anteriormente\n");
                        } else {
                            if (defineTypeAndAddtoScope(parametroIdentificador, varTipo, procScope)) {
                                defineTypeAndAddtoScope(parametroIdentificador, varTipo, procParameters);
                            } else {
                                if (globalScope.existe(varTipo) && globalScope.verificar(
                                        varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                                    if (procScope.existe(parametroIdentificador)) {
                                        LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),
                                                "identificador " + parametroIdentificador
                                                        + " ja declarado anteriormente\n");
                                    } else {
                                        EntradaTabelaDeSimbolos fields = globalScope.verificar(varTipo);
                                        TabelaDeSimbolos nestedTableType = fields.argsRegFunc;

                                        procScope.put(parametroIdentificador,
                                                TabelaDeSimbolos.EstruturaLA.REGISTRO,
                                                TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType,
                                                varTipo);
                                        procParameters.put(parametroIdentificador,
                                                TabelaDeSimbolos.EstruturaLA.REGISTRO,
                                                TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType,
                                                varTipo);
                                    }
                                }

                                if (!globalScope.existe(varTipo)) {
                                    LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),
                                            "tipo " + varTipo + " nao declarado\n");
                                    procScope.put(parametroIdentificador,
                                            TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                            TabelaDeSimbolos.TipoLA.INVALIDO);
                                    procParameters.put(parametroIdentificador,
                                            TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                            TabelaDeSimbolos.TipoLA.INVALIDO);
                                }
                            }
                        }
                    }
                }
            }

        }

        return super.visitDeclaracao_global(ctx);
    }

    // Método para visitar e realizar ações na chamada de procedimento ou função
    @Override
    public Void visitCmdChamada(CmdChamadaContext ctx) {
        // Lógica para tratamento de chamadas de procedimentos ou funções

        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        String identificador = ctx.IDENT().getText();

        if (!escopoAtual.existe(identificador)) {
            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                    "identificador " + identificador + " nao declarado\n");
        } else {
            EntradaTabelaDeSimbolos funProc = escopoAtual.verificar(identificador);
            ArrayList<TabelaDeSimbolos.TipoLA> parameterTypes = new ArrayList<>();
            for (ExpressaoContext exp : ctx.expressao()) {
                parameterTypes.add(LASemanticoUtils.verificarTipo(escopoAtual, exp));
            }
            if (!funProc.argsRegFunc.validar(parameterTypes)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                        "incompatibilidade de parametros na chamada de " + identificador + "\n");
            }
        }

        return super.visitCmdChamada(ctx);
    }

    // Método para visitar e realizar ações na atribuição de valores a variáveis
    @Override
    public Void visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        // Lógica para tratamento de atribuições de valores a variáveis
        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        TipoLA leftValue = LASemanticoUtils.verificarTipo(escopoAtual,
                ctx.identificador());
        TipoLA rightValue = LASemanticoUtils.verificarTipo(escopoAtual,
                ctx.expressao());
        // Verifica atribuição para ponteiros
        String[] atribuition = ctx.getText().split("<-");
        if (!LASemanticoUtils.verificarTipo(leftValue, rightValue) && !atribuition[0].contains("^")) {
            // Esse erro informa que a atribuição não é compatível para o identificador
            // presente na atribuição.
            LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(),
                    "atribuicao nao compativel para " + ctx.identificador().getText() + "\n");
        }
        // Type.verificaring
        if (atribuition[0].contains("^")) {
            if (leftValue == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO
                    &&
                    rightValue != TabelaDeSimbolos.TipoLA.INTEIRO)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(),
                        "atribuicao nao compativel para " + atribuition[0] + "\n");
            if (leftValue == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO
                    &&
                    rightValue != TabelaDeSimbolos.TipoLA.LOGICO)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(),
                        "atribuicao nao compativel para " + atribuition[0] + "\n");
            if (leftValue == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL
                    &&
                    rightValue != TabelaDeSimbolos.TipoLA.REAL)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(),
                        "atribuicao nao compativel para " + atribuition[0] + "\n");
            if (leftValue == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL
                    &&
                    rightValue != TabelaDeSimbolos.TipoLA.LITERAL)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(),
                        "atribuicao nao compativel para " + atribuition[0] + "\n");
        }
        return super.visitCmdAtribuicao(ctx);
    }

    // Método para visitar e realizar ações na leitura de valores para variáveis
    @Override
    public Void visitCmdLeia(CmdLeiaContext ctx) {
        // Lógica para tratamento da leitura de valores para variáveis

        // Obtemos o escopo atual através da variável escopoAtual
        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

        // Iteramos sobre os identificadores presentes no comando
        for (IdentificadorContext ident : ctx.identificador()) {
            // Verificação semântica do tipo do identificador
            LASemanticoUtils.verificarTipo(escopoAtual, ident);
        }
        return super.visitCmdLeia(ctx);
    }

    // Método para visitar e realizar ações em expressões aritméticas
    @Override
    public Void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        // Lógica para tratamento de expressões aritméticas
        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        LASemanticoUtils.verificarTipo(escopoAtual, ctx);
        return super.visitExp_aritmetica(ctx);
    }

    // Método para visitar o programa
    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        // Lógica para tratamento do programa
        for (CmdContext ctxCmd : ctx.corpo().cmd()) {
            if (ctxCmd.cmdRetorne() != null) {
                LASemanticoUtils.adicionarErroSemantico(ctxCmd.cmdRetorne().getStart(),
                        "comando retorne nao permitido nesse escopo\n");
            }
        }

        for (Decl_local_globalContext ctxDec : ctx.declaracoes().decl_local_global()) {
            if (ctxDec.declaracao_global() != null && ctxDec.declaracao_global().tipo_estendido() == null) {
                for (CmdContext ctxCmd : ctxDec.declaracao_global().cmd()) {
                    if (ctxCmd.cmdRetorne() != null)
                        LASemanticoUtils.adicionarErroSemantico(ctxCmd.cmdRetorne().getStart(),
                                "comando retorne nao permitido nesse escopo\n");
                }
            }
        }

        return super.visitPrograma(ctx);
    }

    // Método para visitar o corpo do programa
    @Override
    public Void visitCorpo(LAParser.CorpoContext ctx) {
        // Lógica para tratamento do corpo do programa
        List<TabelaDeSimbolos> scopes = escopos.percorrerEscopoAninhados();
        if (scopes.size() > 1) {
            escopos.obterEscopoAtual();
        }

        return super.visitCorpo(ctx);
    }
}

// // Importação das bibliotecas e classes necessárias
// package br.ufscar.dc.compiladores.LA;

// import java.util.ArrayList;
// import java.util.List;

// import org.antlr.v4.runtime.tree.TerminalNode;

// import br.ufscar.dc.compiladores.LA.LAParser.CmdAtribuicaoContext;
// import br.ufscar.dc.compiladores.LA.LAParser.CmdChamadaContext;
// import br.ufscar.dc.compiladores.LA.LAParser.CmdContext;
// import br.ufscar.dc.compiladores.LA.LAParser.CmdLeiaContext;
// import br.ufscar.dc.compiladores.LA.LAParser.Decl_local_globalContext;
// import br.ufscar.dc.compiladores.LA.LAParser.Declaracao_globalContext;
// import br.ufscar.dc.compiladores.LA.LAParser.Exp_aritmeticaContext;
// import br.ufscar.dc.compiladores.LA.LAParser.ExpressaoContext;
// import br.ufscar.dc.compiladores.LA.LAParser.IdentificadorContext;
// import br.ufscar.dc.compiladores.LA.LAParser.VariavelContext;
// import br.ufscar.dc.compiladores.LA.SymbolTable.TypeLAVariable;

    
// public class LAvisitor extends LABaseVisitor<Void> {
//     Scopes nestedScopes = new Scopes();
//     SymbolTable symbolTable;

//     public Boolean defineTypeAndAddtoScope(String variableIdentifier, String variableType, SymbolTable symbolTable){
//         switch (variableType) {
//             case "inteiro":
//                 symbolTable.put(variableIdentifier,
//                         SymbolTable.TypeLAIdentifier.VARIAVEL, TypeLAVariable.INTEIRO);
//                 break;
//             case "literal":
//                 symbolTable.put(variableIdentifier,
//                         SymbolTable.TypeLAIdentifier.VARIAVEL, TypeLAVariable.LITERAL);
//                 break;
//             case "real":
//                 symbolTable.put(variableIdentifier,
//                         SymbolTable.TypeLAIdentifier.VARIAVEL, TypeLAVariable.REAL);
//                 break;
//             case "logico":
//                 symbolTable.put(variableIdentifier,
//                         SymbolTable.TypeLAIdentifier.VARIAVEL, TypeLAVariable.LOGICO);
//                 break;
//             case "^logico":
//                 symbolTable.put(variableIdentifier,
//                         SymbolTable.TypeLAIdentifier.VARIAVEL, TypeLAVariable.PONT_LOGI);
//                 break;
//             case "^real":
//                 symbolTable.put(variableIdentifier,
//                         SymbolTable.TypeLAIdentifier.VARIAVEL, TypeLAVariable.PONT_REAL);
//                 break;
//             case "^literal":
//                 symbolTable.put(variableIdentifier,
//                         SymbolTable.TypeLAIdentifier.VARIAVEL, TypeLAVariable.PONT_LITE);
//                 break;
//             case "^inteiro":
//                 symbolTable.put(variableIdentifier,
//                         SymbolTable.TypeLAIdentifier.VARIAVEL, TypeLAVariable.PONT_INTE);
//                 break;
//             default:
//                 return false;
//         }
//         return true;
//     }


//     @Override
//     public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
//         // Lógica para a regra "declaracao_local"
//         if(ctx.IDENT() != null){
//             //Existe um IDENT (sequencia de caracteres que define um identificador (nome))
//             String identifier = ctx.IDENT().getText();
//             SymbolTable currentScope = nestedScopes.getCurrentScope();

//             if (ctx.tipo_basico() != null) { 
//                 // constant declaration
//                 // 'constante' IDENT ':' tipo_basico '=' valor_constante
//                 if (currentScope.exists(identifier)) {
//                     LASemanticUtils.addSemanticError(ctx.IDENT().getSymbol(),
//                             "identifier " + identifier + " ja declarado anteriormente\n");
//                 } else {
//                     String constantType = ctx.tipo_basico().getText();
//                     switch (constantType) {
//                         case "inteiro":
//                             currentScope.put(identifier, SymbolTable.TypeLAIdentifier.CONSTANTE,
//                                     TypeLAVariable.INTEIRO);
//                             break;
//                         case "literal":
//                             currentScope.put(identifier, SymbolTable.TypeLAIdentifier.CONSTANTE,
//                                     TypeLAVariable.LITERAL);
//                             break;
//                         case "real":
//                             currentScope.put(identifier, SymbolTable.TypeLAIdentifier.CONSTANTE,
//                                     TypeLAVariable.REAL);
//                             break;
//                         case "logico":
//                             currentScope.put(identifier, SymbolTable.TypeLAIdentifier.CONSTANTE,
//                                     TypeLAVariable.LOGICO);
//                             break;
//                         default:
//                             // never reached
//                             break;
//                     }
//                 }
//             } else {
//                 // type declaration
//                 // 'tipo' IDENT ':' tipo
//                 if (currentScope.exists(identifier)) {
//                     LASemanticUtils.addSemanticError(ctx.IDENT().getSymbol(),
//                             "identifier " + identifier + " ja declarado anteriormente\n");
//                 } else {
//                     SymbolTable fieldsTypes = new SymbolTable();
//                     currentScope.put(identifier, SymbolTable.TypeLAIdentifier.TIPO, null, fieldsTypes);
//                     for (VariavelContext variable : ctx.tipo().registro().variavel()) {
//                         for (IdentificadorContext ctxIdentVariable : variable.identificador()) {
//                             String variableIdentifier = ctxIdentVariable.getText();
//                             if (fieldsTypes.exists(variableIdentifier)) {
//                                 LASemanticUtils.addSemanticError(ctxIdentVariable.IDENT(0).getSymbol(),
//                                         "identificador " + variableIdentifier + " ja declarado anteriormente\n");
//                             } else {
//                                 String variableType = variable.tipo().getText();
//                                 if(!defineTypeAndAddtoScope(variableIdentifier, variableType, fieldsTypes)){
//                                     //nothing happens
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }
//         }else{
//             //'declare' variavel
//             if(ctx.variavel().tipo().registro() == null){
//                 //Não é registro
//                 for (IdentificadorContext ctxIdentVariable : ctx.variavel().identificador()) {
//                     String variableIdentifier = "";
//                     for (TerminalNode ident : ctxIdentVariable.IDENT())
//                         variableIdentifier += ident.getText();
//                     SymbolTable currentScope = nestedScopes.getCurrentScope();

//                     if (ctxIdentVariable.dimensao() != null)
//                             // dimension exists
//                             for (Exp_aritmeticaContext expDim : ctxIdentVariable.dimensao().exp_aritmetica())
//                                 LASemanticUtils.verifyType(currentScope, expDim);

//                     // Verifica se o identificador da variável já foi declarado anteriormente.
//                     if (currentScope.exists(variableIdentifier)) {
//                         LASemanticUtils.addSemanticError(ctxIdentVariable.IDENT(0).getSymbol(),
//                                 "identificador " + variableIdentifier + " ja declarado anteriormente\n");
//                     } else {
//                         String variableType = ctx.variavel().tipo().getText();
                        
//                         if(!defineTypeAndAddtoScope(variableIdentifier, variableType, currentScope)){
//                             // Caso o tipo não seja um tipo básico
//                             if (currentScope.exists(variableType) && currentScope.check(
//                                 // Verificamos se o tipo já foi declarado anteriormente no escopo atual e se é um tipo válido.
//                                 variableType).identifierType == SymbolTable.TypeLAIdentifier.TIPO) {
//                                 if (currentScope.exists(variableIdentifier)) {
//                                     LASemanticUtils.addSemanticError(ctxIdentVariable.IDENT(0).getSymbol(),
//                                             "identificador " + variableIdentifier + " ja declarado anteriormente\n");
//                                 }
//                                 else{
//                                     SymbolTableEntry entry = currentScope.check(variableType);
//                                     SymbolTable fieldsType = entry.argsRegFunc;
//                                     currentScope.put(variableIdentifier,
//                                             SymbolTable.TypeLAIdentifier.REGISTRO, null, fieldsType);
//                                 }
//                             }

//                             //Se o tipo não foi declarado, um erro semântico é adicionado informando que o tipo não foi declarado
//                             if(!currentScope.exists(variableType)){
//                                 LASemanticUtils.addSemanticError(ctxIdentVariable.IDENT(0).getSymbol(),
//                                 "tipo " + variableType + " nao declarado\n");
//                                 // A variável é adicionada ao escopo atual com um tipo inválido (INVALIDO).
//                                 currentScope.put(variableIdentifier,
//                                             SymbolTable.TypeLAIdentifier.VARIAVEL,
//                                             SymbolTable.TypeLAVariable.INVALIDO);
//                             }
//                         }
//                     }
//                 }
//             }
//             else{
//                 // Register with type declaration
//                 ArrayList<String> registerIdentifiers = new ArrayList<>();
//                 for (IdentificadorContext ctxIdentReg : ctx.variavel().identificador()) {
//                     String identifierName = ctxIdentReg.getText();
//                     SymbolTable currentScope = nestedScopes.getCurrentScope();

//                     if (currentScope.exists(identifierName)) {
//                         // identifier must be unique 
//                         LASemanticUtils.addSemanticError(ctxIdentReg.IDENT(0).getSymbol(),
//                                 "identificador " + identifierName + " ja declarado anteriormente\n");
//                     } else {
//                         SymbolTable fields = new SymbolTable();
//                         currentScope.put(identifierName, SymbolTable.TypeLAIdentifier.REGISTRO, null,
//                                 fields);
//                         registerIdentifiers.add(identifierName);
//                     }
//                 }

//                 for (VariavelContext ctxVariableRegister : ctx.variavel().tipo().registro().variavel()) {
//                     // populate register context
//                     for (IdentificadorContext ctxVariableRegisterIdent : ctxVariableRegister.identificador()) {
//                         String registerFieldName = ctxVariableRegisterIdent.getText();
//                         SymbolTable currentScope = nestedScopes.getCurrentScope();

//                         for (String registerIdentifier : registerIdentifiers) {
//                             SymbolTableEntry entry = currentScope.check(registerIdentifier);
//                             SymbolTable registerFields = entry.argsRegFunc;

//                             if (registerFields.exists(registerFieldName)) {
//                                 LASemanticUtils.addSemanticError(ctxVariableRegisterIdent.IDENT(0).getSymbol(),
//                                         "identificador " + registerFieldName + " ja declarado anteriormente\n");
//                             } else {
//                                 String variableType = ctxVariableRegister.tipo().getText();
//                                 if(!defineTypeAndAddtoScope(registerFieldName, variableType, registerFields)){
//                                     // not a basic/primitive type
//                                     if (!currentScope.exists(variableType)) {
//                                         LASemanticUtils.addSemanticError(
//                                                 ctxVariableRegisterIdent.IDENT(0).getSymbol(),
//                                                 "tipo " + variableType + " nao declarado\n");
//                                         currentScope.put(registerFieldName,
//                                                 SymbolTable.TypeLAIdentifier.VARIAVEL,
//                                                 SymbolTable.TypeLAVariable.INVALIDO);
//                                     }
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }
//         }
//         return super.visitDeclaracao_local(ctx);
//     }

//     @Override
//     public Void visitDeclaracao_global(Declaracao_globalContext ctx){
//         String identifier = ctx.IDENT().getText();

//         // Geting scopes
//         List<SymbolTable> scopes = nestedScopes.runNestedScopes();
//         if (scopes.size() > 1) {
//             nestedScopes.giveupScope();
//         }
//         SymbolTable globalScope = nestedScopes.getCurrentScope();

//         if(ctx.tipo_estendido() != null){
//             //has a type and returns, is a function
//             nestedScopes.createNewScope();
//             SymbolTable functionScope = nestedScopes.getCurrentScope();
//             functionScope.setGlobal(globalScope); //Add global scope reference to symbolTable

//             if(globalScope.exists(identifier)){
//                 LASemanticUtils.addSemanticError(ctx.IDENT().getSymbol(),
//                         "identifier " + identifier + " ja declarado anteriormente\n");
//             }
//             else{
//                 SymbolTable funcParameters = new SymbolTable();
//                 globalScope.put(identifier, SymbolTable.TypeLAIdentifier.FUNCAO, null, funcParameters,
//                         ctx.tipo_estendido().getText());

//                 for(LAParser.ParametroContext declaredParameter: ctx.parametros().parametro()){
//                     String variableType =  declaredParameter.tipo_estendido().getText();

//                     for(LAParser.IdentificadorContext ident: declaredParameter.identificador()){
//                         //After declaring a type of a parameter, is possible to declare multiple parameters of same type
//                         String parameterIdentifier = ident.getText();

//                         if(functionScope.exists(parameterIdentifier)){
//                             //Another parameter with same name, already defined
//                             LASemanticUtils.addSemanticError(ctx.IDENT().getSymbol(),
//                                 "identifier " + parameterIdentifier + " ja declarado anteriormente\n");
//                         }
//                         else{
//                             if(defineTypeAndAddtoScope(parameterIdentifier, variableType, functionScope)){ 
//                                 //Caso consiga definir os tipos para o escopo da função, reproduz para os parametros
//                                 defineTypeAndAddtoScope(parameterIdentifier, variableType, funcParameters);
//                             }else{
//                                 //Caso não seja um dos tipo_estendido 
//                                 if (globalScope.exists(variableType) && globalScope.check(
//                                     variableType).identifierType == SymbolTable.TypeLAIdentifier.TIPO) {
//                                     if (functionScope.exists(parameterIdentifier)) {
//                                         LASemanticUtils.addSemanticError(ident.IDENT(0).getSymbol(),
//                                                 "identifier " + parameterIdentifier + " ja declarado anteriormente\n");
//                                     } else {
//                                         SymbolTableEntry fields = globalScope.check(variableType);
//                                         SymbolTable nestedTableType = fields.argsRegFunc;

//                                         functionScope.put(parameterIdentifier,
//                                                 SymbolTable.TypeLAIdentifier.REGISTRO,
//                                                 SymbolTable.TypeLAVariable.REGISTRO, nestedTableType,
//                                                 variableType);
//                                         funcParameters.put(parameterIdentifier,
//                                                 SymbolTable.TypeLAIdentifier.REGISTRO,
//                                                 SymbolTable.TypeLAVariable.REGISTRO, nestedTableType,
//                                                 variableType);
//                                     }
//                                 }
//                                 if (!globalScope.exists(variableType)) {
//                                     LASemanticUtils.addSemanticError(ident.IDENT(0).getSymbol(),
//                                             "tipo " + variableType + " nao declarado\n");
//                                     functionScope.put(parameterIdentifier,
//                                             SymbolTable.TypeLAIdentifier.VARIAVEL,
//                                             SymbolTable.TypeLAVariable.INVALIDO);
//                                     funcParameters.put(parameterIdentifier,
//                                             SymbolTable.TypeLAIdentifier.VARIAVEL,
//                                             SymbolTable.TypeLAVariable.INVALIDO);
//                                 }
//                             }
//                         }

//                     }
//                 }
//             }

//         }else{
//             //is a procedure
//             nestedScopes.createNewScope();
//             SymbolTable procScope = nestedScopes.getCurrentScope();
//             procScope.setGlobal(globalScope); //Add global scope reference to symbolTable

//             if(globalScope.exists(identifier)){
//                 LASemanticUtils.addSemanticError(ctx.IDENT().getSymbol(),
//                         "identifier " + identifier + " ja declarado anteriormente\n");
//             }
//             else{
//                 SymbolTable procParameters = new SymbolTable();
//                 globalScope.put(identifier, SymbolTable.TypeLAIdentifier.PROCEDIMENTO, null, procParameters);

//                 for(LAParser.ParametroContext declaredParameter: ctx.parametros().parametro()){
//                     String variableType =  declaredParameter.tipo_estendido().getText();

//                     for(LAParser.IdentificadorContext ident: declaredParameter.identificador()){
//                         //After declaring a type of a parameter, is possible to declare multiple parameters of same type
//                         String parameterIdentifier = ident.getText();

//                         if(procScope.exists(parameterIdentifier)){
//                             //Another parameter with same name, already defined
//                             LASemanticUtils.addSemanticError(ctx.IDENT().getSymbol(),
//                                 "identifier " + parameterIdentifier + " ja declarado anteriormente\n");
//                         }
//                         else{
//                             if(defineTypeAndAddtoScope(parameterIdentifier, variableType, procScope)){
//                                 defineTypeAndAddtoScope(parameterIdentifier, variableType, procParameters);
//                             }else{
//                                 if (globalScope.exists(variableType) && globalScope.check(
//                                         variableType).identifierType == SymbolTable.TypeLAIdentifier.TIPO) {
//                                     if (procScope.exists(parameterIdentifier)) {
//                                         LASemanticUtils.addSemanticError(ident.IDENT(0).getSymbol(),
//                                                 "identifier " + parameterIdentifier + " ja declarado anteriormente\n");
//                                     } else {
//                                         SymbolTableEntry fields = globalScope.check(variableType);
//                                         SymbolTable nestedTableType = fields.argsRegFunc;

//                                         procScope.put(parameterIdentifier,
//                                                 SymbolTable.TypeLAIdentifier.REGISTRO,
//                                                 SymbolTable.TypeLAVariable.REGISTRO, nestedTableType,
//                                                 variableType);
//                                         procParameters.put(parameterIdentifier,
//                                                 SymbolTable.TypeLAIdentifier.REGISTRO,
//                                                 SymbolTable.TypeLAVariable.REGISTRO, nestedTableType,
//                                                 variableType);
//                                     }
//                                 }

//                                 if (!globalScope.exists(variableType)) {
//                                     LASemanticUtils.addSemanticError(ident.IDENT(0).getSymbol(),
//                                             "tipo " + variableType + " nao declarado\n");
//                                     procScope.put(parameterIdentifier,
//                                             SymbolTable.TypeLAIdentifier.VARIAVEL,
//                                             SymbolTable.TypeLAVariable.INVALIDO);
//                                     procParameters.put(parameterIdentifier,
//                                             SymbolTable.TypeLAIdentifier.VARIAVEL,
//                                             SymbolTable.TypeLAVariable.INVALIDO);
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }


//         }
        

//         return super.visitDeclaracao_global(ctx);
//     }

//     @Override
//     public Void visitCmdChamada(CmdChamadaContext ctx){

//         SymbolTable currentScope = nestedScopes.getCurrentScope();
//         String identifier  = ctx.IDENT().getText();

//         if (!currentScope.exists(identifier)) {
//             LASemanticUtils.addSemanticError(ctx.IDENT().getSymbol(),
//                     "identificador " + identifier + " nao declarado\n");
//         } else {
//             SymbolTableEntry funProc = currentScope.check(identifier);
//             ArrayList<SymbolTable.TypeLAVariable> parameterTypes = new ArrayList<>();
//             for (ExpressaoContext exp : ctx.expressao()) {
//                 parameterTypes.add(LASemanticUtils.verifyType(currentScope, exp));
//             }
//             if (!funProc.argsRegFunc.validType(parameterTypes)) {
//                 LASemanticUtils.addSemanticError(ctx.IDENT().getSymbol(),
//                         "incompatibilidade de parametros na chamada de " + identifier + "\n");
//             }
//         }

//         return super.visitCmdChamada(ctx);
//     }


//     @Override
//     public Void visitCmdAtribuicao(CmdAtribuicaoContext ctx){
//         SymbolTable currentScope = nestedScopes.getCurrentScope();
//         TypeLAVariable leftValue = LASemanticUtils.verifyType(currentScope,
//                 ctx.identificador());
//         TypeLAVariable rightValue = LASemanticUtils.verifyType(currentScope,
//                 ctx.expressao());
//         // Verifica atribuição para ponteiros
//         String[] atribuition = ctx.getText().split("<-");
//         if (!LASemanticUtils.verifyType(leftValue, rightValue) && !atribuition[0].contains("^")) {
//             // Esse erro informa que a atribuição não é compatível para o identificador presente na atribuição.
//             LASemanticUtils.addSemanticError(ctx.identificador().IDENT(0).getSymbol(),
//                     "atribuicao nao compativel para " + ctx.identificador().getText() + "\n");
//         }
//         // Type Checking
//         if (atribuition[0].contains("^")){
//             if (
//                 leftValue == SymbolTable.TypeLAVariable.PONT_INTE
//                 && 
//                 rightValue != SymbolTable.TypeLAVariable.INTEIRO
//                 )
//                 LASemanticUtils.addSemanticError(ctx.identificador().IDENT(0).getSymbol(),
//                         "atribuicao nao compativel para " + atribuition[0] + "\n");
//             if (
//                 leftValue == SymbolTable.TypeLAVariable.PONT_LOGI
//                 && 
//                 rightValue != SymbolTable.TypeLAVariable.LOGICO
//                 )
//                 LASemanticUtils.addSemanticError(ctx.identificador().IDENT(0).getSymbol(),
//                         "atribuicao nao compativel para " + atribuition[0] + "\n");
//             if (
//                 leftValue == SymbolTable.TypeLAVariable.PONT_REAL
//                 && 
//                 rightValue != SymbolTable.TypeLAVariable.REAL
//                 )
//                 LASemanticUtils.addSemanticError(ctx.identificador().IDENT(0).getSymbol(),
//                         "atribuicao nao compativel para " + atribuition[0] + "\n");
//             if (
//                 leftValue == SymbolTable.TypeLAVariable.PONT_LITE
//                 && 
//                 rightValue != SymbolTable.TypeLAVariable.LITERAL
//                 )
//                 LASemanticUtils.addSemanticError(ctx.identificador().IDENT(0).getSymbol(),
//                         "atribuicao nao compativel para " + atribuition[0] + "\n");
//         }
//         return super.visitCmdAtribuicao(ctx);
//     }

//     @Override
//     public Void visitCmdLeia(CmdLeiaContext ctx){
//         // Obtemos o escopo atual através da variável currentScope 
//         SymbolTable currentScope = nestedScopes.getCurrentScope();

//         // Iteramos sobre os identificadores presentes no comando 
//         for (IdentificadorContext ident : ctx.identificador()) {
//             // Verificação semântica do tipo do identificador
//             LASemanticUtils.verifyType(currentScope, ident);
//         }
//         return super.visitCmdLeia(ctx);
//     }

//     @Override
//     public Void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx){
//         // Lógica para a regra "exp_aritmetica"
//         SymbolTable currentScope = nestedScopes.getCurrentScope();
//         LASemanticUtils.verifyType(currentScope, ctx);
//         return super.visitExp_aritmetica(ctx);
//     }

//     // Program entrypoint method
//     @Override
//     public Void visitPrograma(LAParser.ProgramaContext ctx) {
//         for (CmdContext ctxCmd : ctx.corpo().cmd()) {
//             if (ctxCmd.cmdRetorne() != null) {
//                 LASemanticUtils.addSemanticError(ctxCmd.cmdRetorne().getStart(),
//                         "comando retorne nao permitido nesse escopo\n");
//             }
//         }

//         for (Decl_local_globalContext ctxDec : ctx.declaracoes().decl_local_global()) {
//             if (ctxDec.declaracao_global() != null && ctxDec.declaracao_global().tipo_estendido() == null) {
//                 for (CmdContext ctxCmd : ctxDec.declaracao_global().cmd()) {
//                     if (ctxCmd.cmdRetorne() != null)
//                         LASemanticUtils.addSemanticError(ctxCmd.cmdRetorne().getStart(),
//                                 "comando retorne nao permitido nesse escopo\n");
//                 }
//             }
//         }

//         return super.visitPrograma(ctx);
//     }
    
//     @Override
//     public Void visitCorpo(LAParser.CorpoContext ctx) {
//         List<SymbolTable> scopes = nestedScopes.runNestedScopes();
//         if (scopes.size() > 1) {
//             nestedScopes.giveupScope();
//         }

//         return super.visitCorpo(ctx);
//     }
// }
