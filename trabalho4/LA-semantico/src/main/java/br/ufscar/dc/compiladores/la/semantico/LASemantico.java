// Pacote e importações necessárias
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

// Classe principal que estende o visitor do ANTLR
public class LASemantico extends LABaseVisitor<Void> {

    // Variáveis para manter informações sobre os escopos e a tabela de símbolos
    Escopo escopos = new Escopo(); // Objeto para gerenciar escopos
    TabelaDeSimbolos tabelaDeSimbolos; // Tabela de símbolos

    // Método para adicionar uma variável na tabela de símbolos com seu tipo.
    Boolean addTipoEscopo(String varIdent, String varTipo, TabelaDeSimbolos tabelaDeSimbolos) {
        // Implementação para adicionar o tipo da variável na tabela de símbolos
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
    // Implementação para visitar a declaração local e adicionar variáveis e tipos na tabela de símbolos
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
                    TabelaDeSimbolos TiposCampos = new TabelaDeSimbolos();
                    escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.TIPO, null, TiposCampos);
                    for (VariavelContext variable : ctx.tipo().registro().variavel()) {
                        for (IdentificadorContext ctxIdentVariable : variable.identificador()) {
                            String varIdent = ctxIdentVariable.getText();
                            if (TiposCampos.existe(varIdent)) {
                                LASemanticoUtils.adicionarErroSemantico(ctxIdentVariable.IDENT(0).getSymbol(),
                                        "identificador " + varIdent + " ja declarado anteriormente\n");
                            } else {
                                String varTipo = variable.tipo().getText();
                                if (!addTipoEscopo(varIdent, varTipo, TiposCampos)) {
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
                                    EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(varTipo);
                                    TabelaDeSimbolos tipoCampos = entrada.argsRegFunc;
                                    escopoAtual.put(varIdent,
                                            TabelaDeSimbolos.EstruturaLA.REGISTRO, null, tipoCampos);
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
                ArrayList<String> registroIdentificadores = new ArrayList<>();
                for (IdentificadorContext ctxIdentReg : ctx.variavel().identificador()) {
                    String identificadorNome = ctxIdentReg.getText();
                    TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

                    if (escopoAtual.existe(identificadorNome)) {
                        LASemanticoUtils.adicionarErroSemantico(ctxIdentReg.IDENT(0).getSymbol(),
                                "identificador " + identificadorNome + " ja declarado anteriormente\n");
                    } else {
                        TabelaDeSimbolos campos = new TabelaDeSimbolos();
                        escopoAtual.put(identificadorNome, TabelaDeSimbolos.EstruturaLA.REGISTRO, null,
                                campos);
                        registroIdentificadores.add(identificadorNome);
                    }
                }

                for (VariavelContext ctxVariableRegister : ctx.variavel().tipo().registro().variavel()) {
                    for (IdentificadorContext ctxVariableRegisterIdent : ctxVariableRegister.identificador()) {
                        String nomeDoCampo = ctxVariableRegisterIdent.getText();
                        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

                        for (String registroIdentificador : registroIdentificadores) {
                            EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(registroIdentificador);
                            TabelaDeSimbolos registrarCampos = entrada.argsRegFunc;

                            if (registrarCampos.existe(nomeDoCampo)) {
                                LASemanticoUtils.adicionarErroSemantico(ctxVariableRegisterIdent.IDENT(0).getSymbol(),
                                        "identificador " + nomeDoCampo + " ja declarado anteriormente\n");
                            } else {
                                String varTipo = ctxVariableRegister.tipo().getText();
                                if (!addTipoEscopo(nomeDoCampo, varTipo, registrarCampos)) {
                                    if (!escopoAtual.existe(varTipo)) {
                                        LASemanticoUtils.adicionarErroSemantico(
                                                ctxVariableRegisterIdent.IDENT(0).getSymbol(),
                                                "tipo " + varTipo + " nao declarado\n");
                                        escopoAtual.put(nomeDoCampo,
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
        // Implementação para visitar a declaração global e adicionar funções e procedimentos na tabela de símbolos
        String identificador = ctx.IDENT().getText();

        // Obtendo os escopos
        List<TabelaDeSimbolos> escopo = escopos.percorrerEscopoAninhados();
        if (escopo.size() > 1) {
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

                for (LAParser.ParametroContext parametroDeclarado : ctx.parametros().parametro()) {
                    String varTipo = parametroDeclarado.tipo_estendido().getText();

                    for (LAParser.IdentificadorContext ident : parametroDeclarado.identificador()) {
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
                                        EntradaTabelaDeSimbolos campos = globalScope.verificar(varTipo);
                                        TabelaDeSimbolos tipoTabela = campos.argsRegFunc;

                                        functionScope.put(parametroIdentificador,
                                                TabelaDeSimbolos.EstruturaLA.REGISTRO, TabelaDeSimbolos.TipoLA.REGISTRO, tipoTabela, varTipo);
                                        funcParameters.put(parametroIdentificador, TabelaDeSimbolos.EstruturaLA.REGISTRO, TabelaDeSimbolos.TipoLA.REGISTRO, tipoTabela, varTipo);
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
            TabelaDeSimbolos buscaEscopo = escopos.obterEscopoAtual();
            buscaEscopo.setGlobal(globalScope); // Adiciona um escopo global

            if (globalScope.existe(identificador)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + identificador + " ja declarado anteriormente\n");
            } else {
                TabelaDeSimbolos buscaParametro = new TabelaDeSimbolos();
                globalScope.put(identificador, TabelaDeSimbolos.EstruturaLA.PROCEDIMENTO, null, buscaParametro);

                for (LAParser.ParametroContext parametroDeclarado : ctx.parametros().parametro()) {
                    String varTipo = parametroDeclarado.tipo_estendido().getText();

                    for (LAParser.IdentificadorContext ident : parametroDeclarado.identificador()) {
                         // Depois de declarar o tipo do parâmetro, podemos declarar múltiplos parâmetros do mesmo tipo
                        String parametroIdentificador = ident.getText();

                        if (buscaEscopo.existe(parametroIdentificador)) {
                            // Outro parâmetro com mesmo nome que já tenha sido definido
                            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + parametroIdentificador + " ja declarado anteriormente\n");
                        } else {
                            if (addTipoEscopo(parametroIdentificador, varTipo, buscaEscopo)) {
                                addTipoEscopo(parametroIdentificador, varTipo, buscaParametro);
                            } else {
                                if (globalScope.existe(varTipo) && globalScope.verificar(
                                        varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                                    if (buscaEscopo.existe(parametroIdentificador)) {
                                        LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),
                                                "identificador " + parametroIdentificador + " ja declarado anteriormente\n");
                                    } else {
                                        EntradaTabelaDeSimbolos campos = globalScope.verificar(varTipo);
                                        TabelaDeSimbolos tipoTabela = campos.argsRegFunc;

                                        buscaEscopo.put(parametroIdentificador,
                                                TabelaDeSimbolos.EstruturaLA.REGISTRO, TabelaDeSimbolos.TipoLA.REGISTRO, tipoTabela, varTipo);
                                        buscaParametro.put(parametroIdentificador, TabelaDeSimbolos.EstruturaLA.REGISTRO, TabelaDeSimbolos.TipoLA.REGISTRO, tipoTabela, varTipo);
                                    }
                                }

                                if (!globalScope.existe(varTipo)) {
                                    LASemanticoUtils.adicionarErroSemantico(ident.IDENT(0).getSymbol(),
                                            "tipo " + varTipo + " nao declarado\n");
                                    buscaEscopo.put(parametroIdentificador, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TabelaDeSimbolos.TipoLA.INVALIDO);
                                    buscaParametro.put(parametroIdentificador, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TabelaDeSimbolos.TipoLA.INVALIDO);
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
        // Implementação para verificar se a chamada de função/procedimento está correta e os tipos dos parâmetros

        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        String identificador = ctx.IDENT().getText();

        if (!escopoAtual.existe(identificador)) {
            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + identificador + " nao declarado\n");
        } else {
            ArrayList<TabelaDeSimbolos.TipoLA> tiposParametro = new ArrayList<>();
            for (ExpressaoContext exp : ctx.expressao()) {
                tiposParametro.add(LASemanticoUtils.verificarTipo(escopoAtual, exp));
            }
        }

        return super.visitCmdChamada(ctx);
    }

    // Método para visitar e realizar ações na atribuição de valores a variáveis
    @Override
    public Void visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        // Implementação para verificar se a atribuição está correta e os tipos dos operandos são compatíveis
        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        TipoLA esquerda = LASemanticoUtils.verificarTipo(escopoAtual, ctx.identificador());
        TipoLA direita = LASemanticoUtils.verificarTipo(escopoAtual, ctx.expressao());
        // Verifica atribuição para ponteiros
        String[] atribuicao = ctx.getText().split("<-");
        if (!LASemanticoUtils.verificarTipo(esquerda, direita) && !atribuicao[0].contains("^")) {
            // Esse erro informa que a atribuição não é compatível para o identificador
            // presente na atribuição.
            LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + ctx.identificador().getText() + "\n");
        }
        // Type.verificaring
        if (atribuicao[0].contains("^")) {
            if (esquerda == TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO
                    &&
                    direita != TabelaDeSimbolos.TipoLA.INTEIRO)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + atribuicao[0] + "\n");
            if (esquerda == TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO && direita != TabelaDeSimbolos.TipoLA.LOGICO)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + atribuicao[0] + "\n");
            if (esquerda == TabelaDeSimbolos.TipoLA.PONTEIRO_REAL && direita != TabelaDeSimbolos.TipoLA.REAL)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + atribuicao[0] + "\n");
            if (esquerda == TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL && direita != TabelaDeSimbolos.TipoLA.LITERAL)
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(), "atribuicao nao compativel para " + atribuicao[0] + "\n");
        }
        return super.visitCmdAtribuicao(ctx);
    }

    // Método para visitar e realizar ações na leitura de valores para variáveis
    @Override
    public Void visitCmdLeia(CmdLeiaContext ctx) {
        // Implementação para verificar se a leitura está correta e os tipos das variáveis são compatíveis

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
        // Implementação para verificar se a expressão aritmética está correta e os tipos dos operandos são compatíveis
        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        LASemanticoUtils.verificarTipo(escopoAtual, ctx);
        return super.visitExp_aritmetica(ctx);
    }

    // Método para visitar o programa
    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        // Implementação para verificar se o programa principal está correto
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
        // Implementação para visitar o corpo do programa e definir escopo
        List<TabelaDeSimbolos> escopo = escopos.percorrerEscopoAninhados();
        if (escopo.size() > 1) {
            escopos.obterEscopoAtual();
        }

        return super.visitCorpo(ctx);
    }
}
