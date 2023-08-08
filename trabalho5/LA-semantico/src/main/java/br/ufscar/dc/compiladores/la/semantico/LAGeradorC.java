// Importação das bibliotecas e classes necessárias
package br.ufscar.dc.compiladores.la.semantico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import br.ufscar.dc.compiladores.la.semantico.EntradaTabelaDeSimbolos;
import br.ufscar.dc.compiladores.la.semantico.Escopo;
import br.ufscar.dc.compiladores.la.semantico.LASemantico;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdAtribuicaoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdCasoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdEnquantoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdEscrevaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdFacaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdLeiaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdParaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdRetorneContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.CmdSeContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Declaracao_globalContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Exp_aritmeticaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Exp_relacionalContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.ExpressaoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.FatorContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Fator_logicoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.IdentificadorContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Item_selecaoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.ParcelaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Parcela_logicaContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Parcela_nao_unarioContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Parcela_unarioContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.SelecaoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.TermoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.Termo_logicoContext;
import br.ufscar.dc.compiladores.la.semantico.LAParser.VariavelContext;
import br.ufscar.dc.compiladores.la.semantico.TabelaDeSimbolos.TipoLA;

public class LAGeradorC extends LABaseVisitor<Void> {

    Escopo escopos = new Escopo();
    TabelaDeSimbolos tabelaDeSimbolos;

    public StringBuilder saida;

    public LAGeradorC() {
        saida = new StringBuilder();
        this.tabelaDeSimbolos = new TabelaDeSimbolos();
    }

    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        ctx.declaracoes().decl_local_global().forEach(dec -> visitDecl_local_global(dec));
        saida.append("\n");
        saida.append("int main() {\n");
        ctx.corpo().declaracao_local().forEach(decl -> visitDeclaracao_local(decl));
        ctx.corpo().cmd().forEach(cmd -> visitCmd(cmd));
        saida.append("return 0;\n");
        saida.append("}\n");
        return null;
    }

    public static String pegarTipoC(TabelaDeSimbolos.TipoLA val) {
        String tipo = null;
        switch (val) {
            case LITERAL:
                tipo = "char";
                break;
            case INTEIRO:
                tipo = "int";
                break;
            case REAL:
                tipo = "float";
                break;
            default:
                break;
        }
        return tipo;
    }

    public static String pegarTipo(TabelaDeSimbolos.TipoLA val) {
        String tipo = null;
        switch (val) {
            case LITERAL:
                tipo = "s";
                break;
            case INTEIRO:
                tipo = "d";
                break;
            case REAL:
                tipo = "f";
                break;
            default:
                break;
        }
        return tipo;
    }

    Boolean addTipoEscopo(String varIdent, String varTipo, TabelaDeSimbolos tabelaDeSimbolos) {
        switch (varTipo) {
            case "inteiro":
                saida.append("      int " + varTipo + ";\n");
                tabelaDeSimbolos.put(varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.INTEIRO);
                break;
            case "literal":
                saida.append("      char " + varTipo + "[80];\n");
                tabelaDeSimbolos.put(varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LITERAL);
                break;
            case "real":
                saida.append("      float " + varTipo + ";\n");
                tabelaDeSimbolos.put(varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.REAL);
                break;
            case "logico":
                saida.append("      boolean " + varTipo + ";\n");
                tabelaDeSimbolos.put(varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LOGICO);
                break;
            case "^logico":
                saida.append("      boolean* " + varTipo + ";\n");
                tabelaDeSimbolos.put(varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_LOGICO);
                break;
            case "^real":
                saida.append("      float* " + varTipo + ";\n");
                tabelaDeSimbolos.put(varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_REAL);
                break;
            case "^literal":
                saida.append("      char* " + varTipo + "[80];\n");
                tabelaDeSimbolos.put(varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_LITERAL);
                break;
            case "^inteiro":
                saida.append("      int* " + varTipo + ";\n");
                tabelaDeSimbolos.put(varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_INTEIRO);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if(ctx.IDENT() != null){
            String identificador = ctx.IDENT().getText();
            TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

            if (ctx.tipo_basico() != null) { 
                saida.append("#define " + identificador + " " + ctx.valor_constante().getText());
                String tipoConstante = ctx.tipo_basico().getText();
                switch (tipoConstante) {
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
                        break;
                }
                
            } else {
                var tipoCampos = new TabelaDeSimbolos();
                escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.TIPO, null, tipoCampos);

                saida.append("    typedef struct {\n");
                for (VariavelContext variable : ctx.tipo().registro().variavel()) {
                    for (IdentificadorContext ctxIdentVariable : variable.identificador()) {
                        String varIdent = ctxIdentVariable.getText();
                        String varTipo = variable.tipo().getText();
                        addTipoEscopo(varIdent, varTipo, tipoCampos);
                        
                        
                    }
                }
                saida.append("   } " + identificador + ";\n");
                
            }
        }else{
            if(ctx.variavel().tipo().registro() == null){
                for (IdentificadorContext ctxIdentVariable : ctx.variavel().identificador()) {
                    String varIdent = "";
                    for (TerminalNode ident : ctxIdentVariable.IDENT())
                        varIdent += ident.getText();
                    TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
                    String varTipo = ctx.variavel().tipo().getText();
                    
                    if(!addTipoEscopo(varIdent, varTipo, escopoAtual)){
                        EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(varTipo);
                        TabelaDeSimbolos tipoCampos = entrada.argsRegFunc;
                        escopoAtual.put(varIdent,TabelaDeSimbolos.EstruturaLA.REGISTRO, null, tipoCampos);
                        saida.append("    " + varTipo + " " + ctxIdentVariable.getText() + ";\n");
                    }
                    
                }
            }
            else{
                saida.append("    struct {\n");

                ArrayList<String> registroidentificadores = new ArrayList<>();
                for (var ctxIdentReg : ctx.variavel().identificador()) {
                    String identificadorNome = ctxIdentReg.getText();
                    TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
                    TabelaDeSimbolos campos = new TabelaDeSimbolos();
                    escopoAtual.put(identificadorNome, TabelaDeSimbolos.EstruturaLA.REGISTRO, null,
                            campos);
                    registroidentificadores.add(ctxIdentReg.getText());
                }

                boolean fechado = false;
                for (VariavelContext ctxVariableRegister : ctx.variavel().tipo().registro().variavel()) {
                    for (IdentificadorContext ctxVariableRegisterIdent : ctxVariableRegister.identificador()) {
                        fechado = false;
                        String nomeDoCampo = ctxVariableRegisterIdent.getText();
                        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

                        for (String registroidentificador : registroidentificadores) {
                            EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(registroidentificador);
                            TabelaDeSimbolos registrarCampos = entrada.argsRegFunc;

                            String varTipo = ctxVariableRegister.tipo().getText();
                            if(!fechado){
                                addTipoEscopo(nomeDoCampo, varTipo, registrarCampos);
                            }
                            
                        }
                        fechado = true;
                    }
                }
                saida.append("    }"); 
                for(String registroidentificador : registroidentificadores){
                    saida.append(registroidentificador);
                }
                saida.append(";\n");
            }
        }
        return null;
    }

    @Override
    public Void visitDeclaracao_global(Declaracao_globalContext ctx) {
        String identificador = ctx.IDENT().getText();

        List<TabelaDeSimbolos> Escopo = escopos.percorrerEscopoAninhados();
        if (Escopo.size() > 1) {
            escopos.obterEscopoAtual();
        }
        TabelaDeSimbolos escopoGeral = escopos.obterEscopoAtual();

        if (ctx.tipo_estendido() != null) {

            escopos.criarNovoEscopo();
            TabelaDeSimbolos escopoFunc = escopos.obterEscopoAtual();
            escopoFunc.setGlobal(escopoGeral);

            var returnTipo = ctx.tipo_estendido().getText();

            addTipoEscopo(identificador, returnTipo, escopoFunc);
            saida.append("(");

            boolean ParametroInicial = true;
            for (LAParser.ParametroContext parametroDeclarado : ctx.parametros().parametro()) {
                String varTipo = parametroDeclarado.tipo_estendido().getText();

                for (LAParser.IdentificadorContext ident : parametroDeclarado.identificador()) {
                    String parametroIdentificador = ident.getText();

                    if (!ParametroInicial) {
                        saida.append(",");
                    }
                    switch (varTipo) {
                        case "inteiro":
                            escopoFunc.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.INTEIRO);
                            saida.append("int " + parametroIdentificador);
                            break;
                        case "literal":
                            escopoFunc.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.LITERAL);
                            saida.append("char* " + parametroIdentificador);
                            break;
                        case "real":
                            escopoFunc.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.REAL);
                            saida.append("float " + parametroIdentificador);
                            break;
                        case "logico":
                            escopoFunc.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.LOGICO);
                            saida.append("boolean " + parametroIdentificador);
                            break;
                        case "^logico":
                            escopoFunc.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO);
                            saida.append("boolean* " + parametroIdentificador);
                            break;
                        case "^real":
                            escopoFunc.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_REAL);
                            saida.append("float* " + parametroIdentificador);
                            break;
                        case "^literal":
                            escopoFunc.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL);
                            saida.append("boolean* " + parametroIdentificador);
                        case "^inteiro":
                            escopoFunc.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO);
                            saida.append("int* " + parametroIdentificador);
                        default:
                            if (escopoGeral.existe(varTipo) && escopoGeral.verificar(
                                    varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                                EntradaTabelaDeSimbolos campos = escopoGeral.verificar(varTipo);
                                TabelaDeSimbolos tipoTabela = campos.argsRegFunc;

                                escopoFunc.put(parametroIdentificador,
                                        TabelaDeSimbolos.EstruturaLA.REGISTRO,
                                        TabelaDeSimbolos.TipoLA.REGISTRO, tipoTabela,
                                        varTipo);
                            }
                            break;
                    }
                    ParametroInicial = false;

                }
            }
            saida.append(") {\n");

        } else {
            escopos.criarNovoEscopo();
            TabelaDeSimbolos escopoFuncao = escopos.obterEscopoAtual();
            escopoFuncao.setGlobal(escopoGeral);

            saida.append("void " + identificador + "(");
            boolean ParametroInicial = true;

            for (LAParser.ParametroContext parametroDeclarado : ctx.parametros().parametro()) {
                String varTipo = parametroDeclarado.tipo_estendido().getText();

                for (LAParser.IdentificadorContext ident : parametroDeclarado.identificador()) {
                    String parametroIdentificador = ident.getText();

                    if (!ParametroInicial) {
                        saida.append(",");
                    }
                    switch (varTipo) {
                        case "inteiro":
                            escopoFuncao.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.INTEIRO);
                            saida.append("int " + parametroIdentificador);
                            break;
                        case "literal":
                            escopoFuncao.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.LITERAL);
                            saida.append("char* " + parametroIdentificador);
                            break;
                        case "real":
                            escopoFuncao.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.REAL);
                            saida.append("float " + parametroIdentificador);
                            break;
                        case "logico":
                            escopoFuncao.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.LOGICO);
                            saida.append("boolean " + parametroIdentificador);
                            break;
                        case "^logico":
                            escopoFuncao.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO);
                            saida.append("boolean* " + parametroIdentificador);
                            break;
                        case "^real":
                            escopoFuncao.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_REAL);
                            saida.append("float* " + parametroIdentificador);
                            break;
                        case "^literal":
                            escopoFuncao.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL);
                            saida.append("boolean* " + parametroIdentificador);
                        case "^inteiro":
                            escopoFuncao.put(parametroIdentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO);
                            saida.append("int* " + parametroIdentificador);
                        default:
                            if (escopoGeral.existe(varTipo) && escopoGeral.verificar(
                                    varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO) {
                                EntradaTabelaDeSimbolos campos = escopoGeral.verificar(varTipo);
                                TabelaDeSimbolos tipoTabela = campos.argsRegFunc;

                                escopoFuncao.put(parametroIdentificador,
                                        TabelaDeSimbolos.EstruturaLA.REGISTRO,
                                        TabelaDeSimbolos.TipoLA.REGISTRO, tipoTabela,
                                        varTipo);
                            }
                            break;
                    }
                    ParametroInicial = false;
                }
            }
            saida.append(") {\n");

        }
        ctx.cmd().forEach(cmd -> visitCmd(cmd));
        saida.append("}\n");

        return null;
    }

    @Override
    public Void visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        saida.append("    " + ctx.getText() + ";\n");

        return null;
    }

    @Override
    public Void visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        var escopoAtual = escopos.obterEscopoAtual();

        String[] atribuicao = ctx.getText().split("<-");

        try {
            if (atribuicao[0].contains("^")) {
                saida.append("*");
            }
            TabelaDeSimbolos.TipoLA varTipo = LASemanticoUtils.verificarTipo(escopoAtual, ctx.identificador());

            if (varTipo == TabelaDeSimbolos.TipoLA.LITERAL) {
                saida.append("strcpy(");
                visitIdentificador(ctx.identificador());
                saida.append("," + ctx.expressao().getText() + ");\n");
            } else {
                visitIdentificador(ctx.identificador());
                saida.append(" = ");
                saida.append(ctx.expressao().getText());
                saida.append(";\n");
            }
        } catch (Exception e) {
            saida.append(e.getMessage());
        }

        return null;
    }

    @Override
    public Void visitCmdLeia(CmdLeiaContext ctx) {
        var escopoAtual = escopos.obterEscopoAtual();
        for (LAParser.IdentificadorContext id : ctx.identificador()) {
            TabelaDeSimbolos.TipoLA varTipo = escopoAtual.verificar(id.getText()).varTipo;
            if (varTipo != TabelaDeSimbolos.TipoLA.LITERAL) {
                saida.append("scanf(\"%");
                saida.append(pegarTipo(varTipo));
                saida.append("\", &");
                saida.append(id.getText());
                saida.append(");\n");
            } else {
                saida.append("gets(");
                visitIdentificador(id);
                saida.append(");\n");
            }
        }

        return null;
    }

    @Override
    public Void visitCmdEscreva(CmdEscrevaContext ctx) {
        for (LAParser.ExpressaoContext exp : ctx.expressao()) {
            TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
            String cType = pegarTipo(LASemanticoUtils.verificarTipo(escopoAtual, exp));
            if (escopoAtual.existe(exp.getText())) {
                TabelaDeSimbolos.TipoLA varTipo = escopoAtual.verificar(exp.getText()).varTipo;
                cType = pegarTipo(varTipo);
            }
            saida.append("printf(\"%");
            saida.append(cType);
            saida.append("\", ");
            saida.append(exp.getText());
            saida.append(");\n");
        }
        return null;
    }

    @Override
    public Void visitCorpo(LAParser.CorpoContext ctx) {
        List<TabelaDeSimbolos> Escopo = escopos.percorrerEscopoAninhados();
        if (Escopo.size() > 1) {
            escopos.obterEscopoAtual();
        }

        return super.visitCorpo(ctx);
    }

    @Override
    public Void visitCmdRetorne(CmdRetorneContext ctx) {
        saida.append("return ");
        visitExpressao(ctx.expressao());
        saida.append(";\n");
        return null;
    }

    @Override
    public Void visitCmdSe(CmdSeContext ctx) {//transcrição do comando if else
        saida.append("if(");
        visitExpressao(ctx.expressao());
        saida.append(") {\n");
        for(CmdContext cmd : ctx.cmd()) {
            visitCmd(cmd);
        }
        saida.append("}\n");
        if(ctx.getText().contains("senao")){
            saida.append("else {\n");
            for(CmdContext cmd : ctx.cmdElse) {
                visitCmd(cmd);
            }
            saida.append("}\n");
        }
        
        return null;
    }

    @Override
    public Void visitExpressao(ExpressaoContext ctx) {
        if (ctx.termo_logico() != null) {
            visitTermo_logico(ctx.termo_logico(0));

            for (int i = 1; i < ctx.termo_logico().size(); i++) {
                LAParser.Termo_logicoContext termo = ctx.termo_logico(i);
                saida.append(" || ");
                visitTermo_logico(termo);
            }
        }

        return null;
    }

    @Override
    public Void visitTermo_logico(Termo_logicoContext ctx) {
        visitFator_logico(ctx.fator_logico(0));

        for (int i = 1; i < ctx.fator_logico().size(); i++) {
            LAParser.Fator_logicoContext fator = ctx.fator_logico(i);
            saida.append(" && ");
            visitFator_logico(fator);
        }

        return null;
    }

    @Override
    public Void visitFator_logico(Fator_logicoContext ctx) {
        if (ctx.getText().startsWith("nao")) {
            saida.append("!");
        }
        visitParcela_logica(ctx.parcela_logica());

        return null;
    }

    @Override
    public Void visitParcela_logica(Parcela_logicaContext ctx) {
        if (ctx.exp_relacional() != null) {
            visitExp_relacional(ctx.exp_relacional());
        } else {
            if (ctx.getText() == "verdadeiro") {
                saida.append("true");
            } else {
                saida.append("false");
            }
        }

        return null;
    }

    @Override
    public Void visitExp_relacional(Exp_relacionalContext ctx) {
        visitExp_aritmetica(ctx.exp_aritmetica(0));
        for (int i = 1; i < ctx.exp_aritmetica().size(); i++) {
            LAParser.Exp_aritmeticaContext termo = ctx.exp_aritmetica(i);
            if (ctx.op_relacional().getText().equals("=")) {
                saida.append(" == ");
            } else {
                saida.append(ctx.op_relacional().getText());
            }
            visitExp_aritmetica(termo);
        }

        return null;
    }

    @Override
    public Void visitExp_aritmetica(Exp_aritmeticaContext ctx) {
        visitTermo(ctx.termo(0));

        for (int i = 1; i < ctx.termo().size(); i++) {
            LAParser.TermoContext termo = ctx.termo(i);
            saida.append(ctx.op1(i - 1).getText());
            visitTermo(termo);
        }
        return null;
    }

    @Override
    public Void visitTermo(TermoContext ctx) {
        visitFator(ctx.fator(0));

        for (int i = 1; i < ctx.fator().size(); i++) {
            LAParser.FatorContext fator = ctx.fator(i);
            saida.append(ctx.op2(i - 1).getText());
            visitFator(fator);
        }
        return null;
    }

    @Override
    public Void visitFator(FatorContext ctx) {
        visitParcela(ctx.parcela(0));

        for (int i = 1; i < ctx.parcela().size(); i++) {
            LAParser.ParcelaContext parcela = ctx.parcela(i);
            saida.append(ctx.op3(i - 1).getText());
            visitParcela(parcela);
        }
        return null;
    }

    @Override
    public Void visitParcela(ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) {
            if (ctx.op_unario() != null) {
                saida.append(ctx.op_unario().getText());
            }
            visitParcela_unario(ctx.parcela_unario());
        } else {
            visitParcela_nao_unario(ctx.parcela_nao_unario());
        }

        return null;
    }

    @Override
    public Void visitParcela_unario(Parcela_unarioContext ctx) {
        if (ctx.IDENT() != null) {
            saida.append(ctx.IDENT().getText());
            saida.append("(");
            for (int i = 0; i < ctx.expressao().size(); i++) {
                visitExpressao(ctx.expressao(i));
                if (i < ctx.expressao().size() - 1) {
                    saida.append(", ");
                }
            }
        } else if (ctx.ABREPAR() != null) {
            saida.append("(");
            ctx.expressao().forEach(exp -> visitExpressao(exp));
            saida.append(")");
        } else {
            saida.append(ctx.getText());
        }

        return null;
    }

    @Override
    public Void visitParcela_nao_unario(Parcela_nao_unarioContext ctx) {
        saida.append(ctx.getText());
        return null;
    }

    @Override
    public Void visitCmdCaso(CmdCasoContext ctx) {
        saida.append("switch(");
        visit(ctx.exp_aritmetica());
        saida.append("){\n");
        visit(ctx.selecao());

        if (ctx.getText().contains("senao")) {
            saida.append("    default:\n");
            ctx.cmd().forEach(cmd -> visitCmd(cmd));
            saida.append("    }\n");
        }

        return null;
    }

    @Override
    public Void visitSelecao(SelecaoContext ctx) {
        ctx.item_selecao().forEach(var -> visitItem_selecao(var));
        return null;
    }

    @Override
    public Void visitItem_selecao(Item_selecaoContext ctx) {
        ArrayList<String> intervalo = new ArrayList<>(Arrays.asList(ctx.constantes().getText().split("\\.\\.")));
        String first = intervalo.size() > 0 ? intervalo.get(0) : ctx.constantes().getText();
        String last = intervalo.size() > 1 ? intervalo.get(1) : intervalo.get(0);
        for (int i = Integer.parseInt(first); i <= Integer.parseInt(last); i++) {
            saida.append("case " + i + ":\n");
            ctx.cmd().forEach(var -> visitCmd(var));
            saida.append("break;\n");
        }
        return null;
    }

    @Override
    public Void visitCmdPara(CmdParaContext ctx) {
        String id = ctx.IDENT().getText();
        saida.append("for(" + id + " = ");
        visitExp_aritmetica(ctx.exp_aritmetica(0));
        saida.append("; " + id + " <= ");
        visitExp_aritmetica(ctx.exp_aritmetica(1));
        saida.append("; " + id + "++){\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        saida.append("}\n");
        return null;
    }

    @Override
    public Void visitCmdEnquanto(CmdEnquantoContext ctx) {
        saida.append("while(");
        visitExpressao(ctx.expressao());
        saida.append("){\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        saida.append("}\n");
        return null;
    }

    @Override
    public Void visitCmdFaca(CmdFacaContext ctx) {
        saida.append("do{\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        saida.append("} while(");
        visitExpressao(ctx.expressao());
        saida.append(");\n");
        return null;
    }

    @Override
    public Void visitIdentificador(IdentificadorContext ctx) {
        saida.append(" ");
        int i = 0;
        for (TerminalNode id : ctx.IDENT()) {
            if (i++ > 0)
                saida.append(".");
            saida.append(id.getText());
        }
        visitDimensao(ctx.dimensao());
        return null;
    }
}