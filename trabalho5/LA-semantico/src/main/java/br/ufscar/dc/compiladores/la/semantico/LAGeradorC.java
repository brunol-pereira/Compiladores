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

public class LAGeneratorC extends LABaseVisitor<Void> {

    Escopo escopos = new Escopo();
    TabelaDeSimbolos tabelaDeSimbolos;

    public StringBuilder output;
    
    public LAGeneratorC() {
        output = new StringBuilder();
        this.tabelaDeSimbolos = new TabelaDeSimbolos();
    }

    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        output.append("#include <stdio.h>\n");
        output.append("#include <stdlib.h>\n");
        ctx.declaracoes().decl_local_global().forEach(dec -> visitDecl_local_global(dec));
        output.append("\n");
        output.append("int main() {\n");
        ctx.corpo().declaracao_local().forEach(decl -> visitDeclaracao_local(decl));
        ctx.corpo().cmd().forEach(cmd -> visitCmd(cmd));
        output.append("return 0;\n");
        output.append("}\n");
        return null;
    }

    public static String getCType(TabelaDeSimbolos.TipoLA val){
        String type = null;
                switch(val) {
                    case LITERAL:
                        type = "char";
                        break;
                    case INTEIRO: 
                        type = "int";
                        break;
                    case REAL: 
                        type = "float";
                        break;
                    default:
                        break;
                }
        return type;
    }

    public static String getCTypeSymbol(TabelaDeSimbolos.TipoLA val){
        String type = null;
                switch(val) {
                    case LITERAL:
                        type = "s";
                        break;
                    case INTEIRO: 
                        type = "d";
                        break;
                    case REAL: 
                        type = "f";
                        break;
                    default:
                        break;
                }
        return type;
    }

    Boolean addTipoEscopo(String varIdent, String varTipo, TabelaDeSimbolos tabelaDeSimbolos){
        switch (varTipo) {
            case "inteiro":
                output.append("        int " + varTipo + ";\n");
                tabelaDeSimbolos.put( varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.INTEIRO);
                break;
            case "literal":
                output.append("        char " + varTipo + "[80];\n");
                tabelaDeSimbolos.put( varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LITERAL);
                break;
            case "real":
                output.append("        float " + varTipo + ";\n");
                tabelaDeSimbolos.put( varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.REAL);
                break;
            case "logico":
                output.append("        boolean " + varTipo + ";\n");
                tabelaDeSimbolos.put( varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.LOGICO);
                break;
            case "^logico":
                output.append("        boolean* " + varTipo + ";\n");
                tabelaDeSimbolos.put( varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_LOGICO);
                break;
            case "^real":
                output.append("        float* " + varTipo + ";\n");
                tabelaDeSimbolos.put( varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_REAL);
                break;
            case "^literal":
                output.append("        char* " + varTipo + "[80];\n");
                tabelaDeSimbolos.put( varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_LITERAL);
                break;
            case "^inteiro":
                output.append("        int* " + varTipo + ";\n");
                tabelaDeSimbolos.put( varTipo, TabelaDeSimbolos.EstruturaLA.VARIAVEL, TipoLA.PONTEIRO_INTEIRO);
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
                output.append("#define " + identificador + " " + ctx.valor_constante().getText());
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
                        break;
                }
                
            } else {
                var fieldsType = new TabelaDeSimbolos();
                escopoAtual.put(identificador, TabelaDeSimbolos.EstruturaLA.TIPO, null, fieldsType);

                output.append("    typedef struct {\n");
                for (VariavelContext variable : ctx.tipo().registro().variavel()) {
                    for (IdentificadorContext ctxIdentVariable : variable.identificador()) {
                        String varIdent = ctxIdentVariable.getText();
                        String varTipo = variable.tipo().getText();
                        addTipoEscopo(null, null, null)(varIdent, varTipo, fieldsType);
                        
                        
                    }
                }
                output.append("    } " + identificador + ";\n");
                
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
                        TabelaDeSimbolos fieldsType = entrada.argsRegFunc;
                        escopoAtual.put(varIdent,
                                            TabelaDeSimbolos.EstruturaLA.REGISTRO, null, fieldsType);
                        output.append("    " + varTipo + " " + ctxIdentVariable.getText() + ";\n");
                    }
                    
                }
            }
            else{
                output.append("    struct {\n");

                ArrayList<String> registroidentificadores = new ArrayList<>();
                for (var ctxIdentReg : ctx.variavel().identificador()) {
                    String identificadorNome = ctxIdentReg.getText();
                    TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
                    TabelaDeSimbolos fields = new TabelaDeSimbolos();
                    escopoAtual.put(identificadorNome, TabelaDeSimbolos.EstruturaLA.REGISTRO, null,
                            fields);
                    registroidentificadores.add(ctxIdentReg.getText());
                }

                boolean lock = false;
                for (VariavelContext ctxVariableRegister : ctx.variavel().tipo().registro().variavel()) {
                    for (IdentificadorContext ctxVariableRegisterIdent : ctxVariableRegister.identificador()) {
                        lock = false;
                        String registerFieldName = ctxVariableRegisterIdent.getText();
                        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();

                        for (String registroidentificadores : registroidentificadores) {
                            EntradaTabelaDeSimbolos entrada = escopoAtual.verificar(registroidentificadores);
                            TabelaDeSimbolos registerFields = entrada.argsRegFunc;

                            String varTipo = ctxVariableRegister.tipo().getText();
                            if(!lock){
                                addTipoEscopo(registerFieldName, varTipo, registerFields);
                            }
                            
                        }
                        lock = true;
                    }
                }
                output.append("    }"); 
                for(String registroidentificadores : registroidentificadores){
                    output.append(registroidentificadores);
                }
                output.append(";\n");
            }
        }
        return null;
    }

    @Override
    public Void visitDeclaracao_global(Declaracao_globalContext ctx){
        String identificador = ctx.IDENT().getText();

        List<TabelaDeSimbolos> Escopo = escopos.percorrerEscopoAninhados();
        if (Escopo.size() > 1) {
            escopos.obterEscopoAtual();
        }
        TabelaDeSimbolos escopoGeral = escopos.obterEscopoAtual();
       

        if(ctx.tipo_estendido() != null){

            escopos.createNewScope();
            TabelaDeSimbolos escopoFunc = escopos.obterEscopoAtual();
            escopoFunc.setGlobal(escopoGeral);

            var returnTipo = ctx.tipo_estendido().getText();

            addTipoEscopo(identificador, returnTipo, escopoFunc);
            output.append("(");

            boolean ParametroInicial = true;
            for(LAParser.ParametroContext declaredParameter: ctx.parametros().parametro()){
                String varTipo =  declaredParameter.tipo_estendido().getText();

                for(LAParser.IdentificadorContext ident: declaredParameter.identificador()){
                    String parameteridentificador = ident.getText();

                    if(!ParametroInicial){
                        output.append(",");
                    }
                    switch (varTipo) {
                        case "inteiro":
                            escopoFunc.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.INTEIRO);
                            output.append("int " + parameteridentificador);
                            break;
                        case "literal":
                            escopoFunc.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.LITERAL);
                            output.append("char* " + parameteridentificador);
                            break;
                        case "real":
                            escopoFunc.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.REAL);
                            output.append("float " + parameteridentificador);
                            break;
                        case "logico":
                            escopoFunc.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.LOGICO);
                            output.append("boolean " + parameteridentificador);
                            break;
                        case "^logico":
                            escopoFunc.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO);
                            output.append("boolean* " + parameteridentificador);
                            break;
                        case "^real":
                            escopoFunc.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_REAL);
                            output.append("float* " + parameteridentificador);
                            break;
                        case "^literal":
                            escopoFunc.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL);
                            output.append("boolean* " + parameteridentificador);
                        case "^inteiro":
                            escopoFunc.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO);
                            output.append("int* " + parameteridentificador);
                        default:
                            if (escopoGeral.exists(varTipo) && escopoGeral.verificar(
                                        varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO){
                                EntradaTabelaDeSimbolos fields = escopoGeral.verificar(varTipo);
                                TabelaDeSimbolos nestedTableType = fields.argsRegFunc;

                                escopoFunc.put(parameteridentificador,
                                        TabelaDeSimbolos.EstruturaLA.REGISTRO,
                                        TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType,
                                        varTipo);
                            }
                            break;
                    }
                    ParametroInicial = false;

                }
            }
            output.append(") {\n");
            

        }else{
            escopos.createNewScope();
            TabelaDeSimbolos procScope = escopos.obterEscopoAtual();
            procScope.setGlobal(escopoGeral);

            output.append("void "+ identificador + "(");
            boolean ParametroInicial = true;

            for(LAParser.ParametroContext declaredParameter: ctx.parametros().parametro()){
                String varTipo =  declaredParameter.tipo_estendido().getText();

                for(LAParser.IdentificadorContext ident: declaredParameter.identificador()){
                    String parameteridentificador = ident.getText();

                    if(!ParametroInicial){
                        output.append(",");
                    }
                    switch (varTipo) {
                        case "inteiro":
                            procScope.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.INTEIRO);
                            output.append("int " + parameteridentificador);
                            break;
                        case "literal":
                            procScope.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.LITERAL);
                            output.append("char* " + parameteridentificador);
                            break;
                        case "real":
                            procScope.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.REAL);
                            output.append("float " + parameteridentificador);
                            break;
                        case "logico":
                            procScope.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.LOGICO);
                            output.append("boolean " + parameteridentificador);
                            break;
                        case "^logico":
                            procScope.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_LOGICO);
                            output.append("boolean* " + parameteridentificador);
                            break;
                        case "^real":
                            procScope.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_REAL);
                            output.append("float* " + parameteridentificador);
                            break;
                        case "^literal":
                            procScope.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_LITERAL);
                            output.append("boolean* " + parameteridentificador);
                        case "^inteiro":
                            procScope.put(parameteridentificador,
                                    TabelaDeSimbolos.EstruturaLA.VARIAVEL,
                                    TabelaDeSimbolos.TipoLA.PONTEIRO_INTEIRO);
                            output.append("int* " + parameteridentificador);
                        default:
                            if (escopoGeral.exists(varTipo) && escopoGeral.verificar(
                                        varTipo).estrutura == TabelaDeSimbolos.EstruturaLA.TIPO){
                                EntradaTabelaDeSimbolos fields = escopoGeral.verificar(varTipo);
                                TabelaDeSimbolos nestedTableType = fields.argsRegFunc;

                                procScope.put(parameteridentificador,
                                        TabelaDeSimbolos.EstruturaLA.REGISTRO,
                                        TabelaDeSimbolos.TipoLA.REGISTRO, nestedTableType,
                                        varTipo);
                            }
                            break;
                    }
                    ParametroInicial = false;
                }
            }
            output.append(") {\n");
            


        }
        ctx.cmd().forEach(cmd->visitCmd(cmd));
        output.append("}\n");

        return null;
    }

    @Override
    public Void visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        output.append("    " + ctx.getText() + ";\n");

        return null;
    }

    @Override
    public Void visitCmdAtribuicao(CmdAtribuicaoContext ctx){
        var escopoAtual = escopos.obterEscopoAtual();

        String[] atribuition = ctx.getText().split("<-");

        try{
            if(atribuition[0].contains("^")){
                output.append("*");
            }
            TabelaDeSimbolos.TipoLA varTipo = LASemanticUtils.verifyType(escopoAtual, ctx.identificador());
            

            if(varTipo == TabelaDeSimbolos.TipoLA.LITERAL){
                output.append("strcpy(");
                visitIdentificador(ctx.identificador());
                output.append(","+ctx.expressao().getText()+");\n");
            }
            else{
                visitIdentificador(ctx.identificador());
                output.append(" = ");
                output.append(ctx.expressao().getText());
                output.append(";\n");
            }
        } catch(Exception e) {
            output.append(e.getMessage());
        }
        
        return null;
    }

    @Override
    public Void visitCmdLeia(CmdLeiaContext ctx){
        var escopoAtual = escopos.obterEscopoAtual();
        for(LAParser.IdentificadorContext id: ctx.identificador()) {
            TabelaDeSimbolos.TipoLA varTipo = escopoAtual.verificar(id.getText()).varTipo;
            if(varTipo != TabelaDeSimbolos.TipoLA.LITERAL){
                output.append("scanf(\"%");
                output.append(getCTypeSymbol(varTipo));
                output.append("\", &");
                output.append(id.getText());
                output.append(");\n");
            } else {
                output.append("gets(");
                visitIdentificador(id);
                output.append(");\n");
            }
        }
        
        return null;
    }

    @Override
    public Void visitCmdEscreva(CmdEscrevaContext ctx) { 
        for(LAParser.ExpressaoContext exp: ctx.expressao()) {
            TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
            String cType = getCTypeSymbol(LASemanticUtils.verifyType(escopoAtual, exp));
            if(escopoAtual.exists(exp.getText())){
                TabelaDeSimbolos.TipoLA varTipo = escopoAtual.verificar(exp.getText()).varTipo;
                cType = getCTypeSymbol(varTipo);
            }
            output.append("printf(\"%");
            output.append(cType);
            output.append("\", ");
            output.append(exp.getText());
            output.append(");\n");
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
    public Void visitCmdRetorne(CmdRetorneContext ctx) {/
        output.append("return ");
        visitExpressao(ctx.expressao());
        output.append(";\n");
        return null;
    }

    @Override
    public Void visitCmdSe(CmdSeContext ctx) {
        output.append("if(");
        visitExpressao(ctx.expressao());
        output.append(") {\n");
        for(CmdContext cmd : ctx.cmd()) {
            visitCmd(cmd);
        }
        output.append("}\n");
        if(ctx.getText().contains("senao")){
            output.append("else {\n");
            for(CmdContext cmd : ctx.cmdElse) {
                visitCmd(cmd);
            }
            output.append("}\n");
        }
        
        return null;
    }

    @Override
    public Void visitExpressao(ExpressaoContext ctx) {
        if(ctx.termo_logico() != null){
            visitTermo_logico(ctx.termo_logico(0));

            for(int i = 1; i < ctx.termo_logico().size(); i++){
                LAParser.Termo_logicoContext termo = ctx.termo_logico(i);
                output.append(" || ");
                visitTermo_logico(termo);
            }
        }

        return null;
    }

    @Override
    public Void visitTermo_logico(Termo_logicoContext ctx) {
        visitFator_logico(ctx.fator_logico(0));

        for(int i = 1; i < ctx.fator_logico().size(); i++){
            LAParser.Fator_logicoContext fator = ctx.fator_logico(i);
            output.append(" && ");
            visitFator_logico(fator);
        }
        
        return null;
    }

    @Override
    public Void visitFator_logico(Fator_logicoContext ctx) {
        if(ctx.getText().startsWith("nao")){
            output.append("!");
        }
        visitParcela_logica(ctx.parcela_logica());
        
        return null;
    }

    @Override
    public Void visitParcela_logica(Parcela_logicaContext ctx) {
        if(ctx.exp_relacional() != null){
            visitExp_relacional(ctx.exp_relacional());
        } else{
            if(ctx.getText() == "verdadeiro"){
                output.append("true");
            } else {
                output.append("false");
            }
        }
        
        return null;
    }

    @Override
    public Void visitExp_relacional(Exp_relacionalContext ctx) {
         visitExp_aritmetica(ctx.exp_aritmetica(0));
        for(int i = 1; i < ctx.exp_aritmetica().size(); i++){
            LAParser.Exp_aritmeticaContext termo = ctx.exp_aritmetica(i);
            if(ctx.op_relacional().getText().equals("=")){
                output.append(" == ");
            } else{
                output.append(ctx.op_relacional().getText());
            }
            visitExp_aritmetica(termo);
        }
        
        return null;
    }

    @Override
    public Void visitExp_aritmetica(Exp_aritmeticaContext ctx) {
        visitTermo(ctx.termo(0));

        for(int i = 1; i < ctx.termo().size(); i++){
            LAParser.TermoContext termo = ctx.termo(i);
            output.append(ctx.op1(i-1).getText());
            visitTermo(termo);
        }
        return null;
    }

    @Override
    public Void visitTermo(TermoContext ctx) {
       visitFator(ctx.fator(0));

        for(int i = 1; i < ctx.fator().size(); i++){
            LAParser.FatorContext fator = ctx.fator(i);
            output.append(ctx.op2(i-1).getText());
            visitFator(fator);
        }
        return null;
    }

    @Override
    public Void visitFator(FatorContext ctx) {
        visitParcela(ctx.parcela(0));

        for(int i = 1; i < ctx.parcela().size(); i++){
            LAParser.ParcelaContext parcela = ctx.parcela(i);
            output.append(ctx.op3(i-1).getText());
            visitParcela(parcela);
        }
        return null;
    }

    @Override
    public Void visitParcela(ParcelaContext ctx) {
        if(ctx.parcela_unario() != null){
            if(ctx.op_unario() != null){
                output.append(ctx.op_unario().getText());
            }
            visitParcela_unario(ctx.parcela_unario());
        } else{
            visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
        
        return null;
    }

    @Override
    public Void visitParcela_unario(Parcela_unarioContext ctx) {
        if(ctx.IDENT() != null){
            output.append(ctx.IDENT().getText());
            output.append("(");
            for(int i = 0; i < ctx.expressao().size(); i++){
                visitExpressao(ctx.expressao(i));
                if(i < ctx.expressao().size()-1){
                    output.append(", ");
                }
            }
        } else if(ctx.AP() != null){
            output.append("(");
            ctx.expressao().forEach( exp -> visitExpressao(exp));
            output.append(")");
        }
        else {
            output.append(ctx.getText());
        }
        
        return null;
    }

    @Override
    public Void visitParcela_nao_unario(Parcela_nao_unarioContext ctx) {
        output.append(ctx.getText());
        return null;
    }

    @Override
    public Void visitCmdCaso(CmdCasoContext ctx) {
        output.append("switch(");
        visit(ctx.exp_aritmetica());
        output.append("){\n");
        visit(ctx.selecao());

        if (ctx.getText().contains("senao")) {
            output.append("    default:\n");
            ctx.cmd().forEach(cmd -> visitCmd(cmd));
            output.append("    }\n");
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
        for(int i = Integer.parseInt(first); i <= Integer.parseInt(last); i++){
            output.append("case " + i + ":\n");
            ctx.cmd().forEach(var -> visitCmd(var));
            output.append("break;\n");
        }
        return null;
    }

    @Override
    public Void visitCmdPara(CmdParaContext ctx) {
        String id = ctx.IDENT().getText();
        output.append("for(" + id + " = ");
        visitExp_aritmetica(ctx.exp_aritmetica(0));
        output.append("; " + id + " <= ");
        visitExp_aritmetica(ctx.exp_aritmetica(1));
        output.append("; " + id + "++){\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        output.append("}\n");
        return null;
    }

    @Override
    public Void visitCmdEnquanto(CmdEnquantoContext ctx) {
        output.append("while(");
        visitExpressao(ctx.expressao());
        output.append("){\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        output.append("}\n");
        return null;
    }

    @Override
    public Void visitCmdFaca(CmdFacaContext ctx) {
        output.append("do{\n");
        ctx.cmd().forEach(var -> visitCmd(var));
        output.append("} while(");
        visitExpressao(ctx.expressao());
        output.append(");\n");
        return null;
    }

    @Override
    public Void visitIdentificador(IdentificadorContext ctx) {
        output.append(" ");
        int i = 0;
        for(TerminalNode id : ctx.IDENT()){
            if(i++ > 0)
                output.append(".");
            output.append(id.getText());
        }
        visitDimensao(ctx.dimensao());
        return null;
    }
}