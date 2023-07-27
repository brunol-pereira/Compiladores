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
    public Boolean addTipoEscopo(String varIdent, String varTipo, TabelaDeSimbolos tabelaDeSimbolos) {
        // Switch-case para identificar o tipo da variável e adicioná-la à tabela de
        // símbolos conforme o tipo correspondente.
        if (varTipo.equals("inteiro")) {
            tabelaDeSimbolos.put(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.INTEIRO);
        } else if (varTipo.equals("real")) {
            tabelaDeSimbolos.put(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.REAL);
        } else if (varTipo.equals("logico")) {
            tabelaDeSimbolos.put(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LOGICO);
        } else if (varTipo.equals("literal")) {
            tabelaDeSimbolos.put(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LITERAL);
        } else if (varTipo.equals("^literal")) {
            tabelaDeSimbolos.put(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_LITERAL);
        } else if (varTipo.equals("^logico")) {
            tabelaDeSimbolos.put(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_LOGICO);
        } else if (varTipo.equals("^real")) {
            tabelaDeSimbolos.put(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_REAL);
        } else if (varTipo.equals("^inteiro")) {
            tabelaDeSimbolos.put(varIdent, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_INTEIRO);
        } else {
            return false; // Caso o tipo seja inválido, retorna falso.
        }
        return true; // Se tudo ocorrer corretamente, retorna verdadeiro.
    }

    // Método para visitar e realizar ações na declaração local do programa
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
    // Lógica para tratamento das declarações locais
        if (ctx.IDENT() != null) {
            // Verifica se existe um IDENT
            String identificador = ctx.IDENT().getText();
            TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

            if (ctx.tipo_basico() != null) {
                if (escopoAtual.existe(identificador)) {
                    LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                            "identificador " + identificador + " ja declarado anteriormente\n");
                } else {
                    String constantType = ctx.tipo_basico().getText();
                    if ("inteiro".equals(constantType)) {
                        escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.CONSTANTE, TipoLA.INTEIRO);
                    } else if ("literal".equals(constantType)) {
                        escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.CONSTANTE, TipoLA.LITERAL);
                    } else if ("real".equals(constantType)) {
                        escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.CONSTANTE, TipoLA.REAL);
                    } else if ("logico".equals(constantType)) {
                        escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.CONSTANTE, TipoLA.LOGICO);
                    }
                }
            } else {
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
                                if (!addTipoEscopo(varIdent, varTipo, fieldsTypes)) {
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (ctx.variavel().tipo().registro() == null) {
                for (IdentificadorContext ctxIdentVariable : ctx.variavel().identificador()) {
                    String varIdent = "";
                    for (TerminalNode ident : ctxIdentVariable.IDENT())
                        varIdent += ident.getText();
                    TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

                    if (ctxIdentVariable.dimensao() != null)
                        for (Exp_aritmeticaContext expDim : ctxIdentVariable.dimensao().exp_aritmetica())
                            LASemanticoUtils.verificarTipo(escopoAtual, expDim);

                    if (escopoAtual.existe(varIdent)) {
                        LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                "identificador " + varIdent + " ja declarado anteriormente\n");
                    } else {
                        String varTipo = ctx.variavel().tipo().getText();

                        if (!addTipoEscopo(varIdent, varTipo, escopoAtual)) {
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
                                if (!addTipoEscopo(registerFieldName, varTipo, registerFields)) {
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
                            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + parametroIdentificador + " ja declarado anteriormente\n");
                        } else {
                            if (addTipoEscopo(parametroIdentificador, varTipo, functionScope)) {
                                // Caso consiga definir os tipos para o escopo da função, reproduz para os parâmetros
                                addTipoEscopo(parametroIdentificador, varTipo, funcParameters);
                            } else {
                                // Caso não seja um dos tipo_estendido
                                if (globalScope.existe(varTipo) && globalScope.verificar(
                                        varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                                    if (functionScope.existe(parametroIdentificador)) {
                                        LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),"identificador " + parametroIdentificador + " ja declarado anteriormente\n");
                                    } else {
                                        EntradaTabelaDeSimbolos fields = globalScope.verificar(varTipo);
                                        TabelaDeSimbolos nestedTableType = fields.argsRegFunc;

                                        functionScope.put(parametroIdentificador,
                                                TabelaDeSimbolos.EstruturaLA.REGISTRO, TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType, varTipo);
                                        funcParameters.put(parametroIdentificador, TabelaDeSimbolos.EstruturaLA.REGISTRO, TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType, varTipo);
                                    }
                                }
                                if (!globalScope.existe(varTipo)) {
                                    LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),
                                            "tipo " + varTipo + " nao declarado\n");
                                    functionScope.put(parametroIdentificador,
                                            TabelaDeSimbolos.EstruturaLA.VARIAVEL, TabelaDeSimbolos.TipoLA.INVALIDO);
                                    funcParameters.put(parametroIdentificador, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TabelaDeSimbolos.TipoLA.INVALIDO);
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
                LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + identificador + " ja declarado anteriormente\n");
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
                            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + parametroIdentificador + " ja declarado anteriormente\n");
                        } else {
                            if (addTipoEscopo(parametroIdentificador, varTipo, procScope)) {
                                addTipoEscopo(parametroIdentificador, varTipo, procParameters);
                            } else {
                                if (globalScope.existe(varTipo) && globalScope.verificar(
                                        varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                                    if (procScope.existe(parametroIdentificador)) {
                                        LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),
                                                "identificador " + parametroIdentificador + " ja declarado anteriormente\n");
                                    } else {
                                        EntradaTabelaDeSimbolos fields = globalScope.verificar(varTipo);
                                        TabelaDeSimbolos nestedTableType = fields.argsRegFunc;

                                        procScope.put(parametroIdentificador,
                                                TabelaDeSimbolos.EstruturaLA.REGISTRO, TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType, varTipo);
                                        procParameters.put(parametroIdentificador, TabelaDeSimbolos.EstruturaLA.REGISTRO, TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType, varTipo);
                                    }
                                }

                                if (!globalScope.existe(varTipo)) {
                                    LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),
                                            "tipo " + varTipo + " nao declarado\n");
                                    procScope.put(parametroIdentificador, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TabelaDeSimbolos.TipoLA.INVALIDO);
                                    procParameters.put(parametroIdentificador, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TabelaDeSimbolos.TipoLA.INVALIDO);
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
            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + identificador + " nao declarado\n");
        } else {
            EntradaTabelaDeSimbolos funProc = escopoAtual.verificar(identificador);
            ArrayList<TabelaDeSimbolos.TipoLA> parameterTypes = new ArrayList<>();
            for (ExpressaoContext exp : ctx.expressao()) {
                parameterTypes.add(LASemanticoUtils.verificarTipo(escopoAtual, exp));
            }
            if (!funProc.argsRegFunc.validar(parameterTypes)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + identificador + "\n");
            }
        }

        return super.visitCmdChamada(ctx);
    }

    // Método para visitar e realizar ações na atribuição de valores a variáveis
    @Override
    public Void visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        // Lógica para tratamento de atribuições de valores a variáveis
        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        TipoLA leftValue = LASemanticoUtils.verificarTipo(escopoAtual, ctx.identificador());
        TipoLA rightValue = LASemanticoUtils.verificarTipo(escopoAtual, ctx.expressao());
        // Verifica atribuição para ponteiros
        String[] atribuition = ctx.getText().split("<-");
        if (!LASemanticoUtils.verificarTipo(leftValue, rightValue) && !atribuition[0].contains("^")) {
            // Esse erro informa que a atribuição não é compatível para o identificador
            // presente na atribuição.
            LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + ctx.identificador().getText() + "\n");
        }
        // Type.verificaring
        if (atribuition[0].contains("^")) {
            if (leftValue == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO
                    &&
                    rightValue != TabelaDeSimbolos.TipoLA.INTEIRO)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + atribuition[0] + "\n");
            if (leftValue == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO && rightValue != TabelaDeSimbolos.TipoLA.LOGICO)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + atribuition[0] + "\n");
            if (leftValue == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL && rightValue != TabelaDeSimbolos.TipoLA.REAL)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + atribuition[0] + "\n");
            if (leftValue == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL && rightValue != TabelaDeSimbolos.TipoLA.LITERAL)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + atribuition[0] + "\n");
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
                LASemanticoUtils.adicionarErroSemantico(ctxCmd.cmdRetorne().getStart(), "comando retorne nao permitido nesse escopo\n");
            }
        }

        for (Decl_local_globalContext ctxDec : ctx.declaracoes().decl_local_global()) {
            if (ctxDec.declaracao_global() != null && ctxDec.declaracao_global().tipo_estendido() == null) {
                for (CmdContext ctxCmd : ctxDec.declaracao_global().cmd()) {
                    if (ctxCmd.cmdRetorne() != null)
                        LASemanticoUtils.adicionarErroSemantico(ctxCmd.cmdRetorne().getStart(), "comando retorne nao permitido nesse escopo\n");
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
