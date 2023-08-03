package br.ufscar.dc.compiladores.la.semantico;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import br.ufscar.dc.compiladores.la.semantico.LAParser.ProgramaContext;

public class Principal {
    public static void main(String args[]) throws IOException {

        PrintWriter pw = null;

        // Verificando o número de argumentos fornecidos
        if (args.length == 1) {
            // Se houver um argumento, utiliza a saída padrão para impressão
            pw = new PrintWriter(System.out);
            pw.println();
        } else if (args.length == 2) {
            // Se houver dois argumentos, tenta abrir o arquivo fornecido como segundo argumento
            try {
                pw = new PrintWriter(new File(args[1]));
            } catch (Exception e) {
                System.out.println("Falha ao abrir o arquivo");
                e.printStackTrace();
            }
        } else {
            // Caso contrário, retorna sem fazer nada
            return;
        }


        // Depurar léxico
        
        // Carregar o arquivo de entrada
        CharStream cs = CharStreams.fromFileName(args[0]);
        LALexer lex = new LALexer(cs);
        boolean lexError = false;

        Token t = null;
        while ((t = lex.nextToken()).getType() != Token.EOF) {
            String nomeToken = LALexer.VOCABULARY.getDisplayName(t.getType());
            
            // Verificando se há erros léxicos
            // Mensagem de erro para qualquer simbolo não identificado.
 
            // ERRO comentário não fechado
            if(nomeToken.equals("COMENTARIO_NAO_FECHADO")) {
                pw.println("Linha "+t.getLine()+": comentario nao fechado");
                lexError = true;
                break;
            }
            // Erro para "cadeia não fechada"
            else if(nomeToken.equals("CADEIA_NAO_FECHADA")) {
                pw.println("Linha "+t.getLine()+": cadeia literal nao fechada");
                lexError = true;
                break;
            }
            // Erro para "simbolo não identificado" 
            else if(nomeToken.equals("ERRO")) {
                pw.println("Linha "+t.getLine()+": "+t.getText()+" - simbolo nao identificado");
                lexError = true;
                break;
            }
            
        }

        // Se não houver erros léxicos, prossegue com a análise sintática e semântica
        
        // Depurar sintático
        if (lexError == false) {
            lex.reset();
    
            CommonTokenStream tokens = new CommonTokenStream(lex);
            LAParser parser = new LAParser(tokens);
    
            // Configurando nosso ErrorListener customizado
            MyCustomErrorListener mcel = new MyCustomErrorListener(pw);
            parser.removeErrorListeners();
            parser.addErrorListener(mcel);

            // Realizar a análise sintática
            parser.programa();
        }

        // Depurar semântico
        
        // Realizar a análise semântica
        cs = CharStreams.fromFileName(args[0]);
        lex = new LALexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        LAParser parser = new LAParser(tokens);

        parser.removeErrorListeners();

        // Obtendo a árvore sintática
        ProgramaContext arvore = parser.programa();
        LASemantico as = new LASemantico();

        // Realiza a visita semântica
        as.visitPrograma(arvore);

        // Verificar se há erros semânticos
        if(!LASemanticoUtils.errosSemanticos.isEmpty()){

            // Imprimir os erros semânticos no arquivo
            for(String s: LASemanticoUtils.errosSemanticos){
                pw.write(s);
            }
            pw.write("Fim da compilacao\n");
        } else{
            var geradorC = new LAGeradorC();
            geradorC.visitPrograma(arvore);
            var saida = geradorC.saida;
            pw.write(saida.toString());
        }
        pw.write("Fim da compilacao\n");
        
        pw.close();

    }
}

