package br.ufscar.dc.compiladores.LA.semantico;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;



public class Principal {
    public static void main(String args[]) throws IOException {

        PrintWriter pw = null;

        // Altera a saída do programa de acordo com a quantidade de argumentos
        if (args.length == 1) {
            //Um argumento
            pw = new PrintWriter(System.out);
            pw.println();
        } else if (args.length == 2) {
            //Dois argumentos
            try {
                pw = new PrintWriter(new File(args[1]));
            } catch (Exception e) {
                System.out.println("Falha ao abrir o arquivo");
                e.printStackTrace();
            }
        } else {
            return; //Finaliza o programa
        }


        //Depurar léxico
        
        CharStream cs = CharStreams.fromFileName(args[0]);
        LALexer lex = new LALexer(cs);
        boolean lexError = false;

        Token t = null;
        while ((t = lex.nextToken()).getType() != Token.EOF) {
            String nomeToken = LALexer.VOCABULARY.getDisplayName(t.getType());
            
            // Mensagem de erro para qualquer simbolo não identificado. 
            // ERRO comentário não fechado
            if(nomeToken.equals("COMENTARIO_NAO_FECHADO")) {
                pw.println("Linha "+t.getLine()+": comentario nao fechado");
                lexError = true;
                break;
            }
            // ERRO cadeia não fechada
            else if(nomeToken.equals("CADEIA_NAO_FECHADA")) {
                pw.println("Linha "+t.getLine()+": cadeia literal nao fechada");
                lexError = true;
                break;
            }
            // ERRO - simbolo não identificado 
            else if(nomeToken.equals("ERRO")) {
                pw.println("Linha "+t.getLine()+": "+t.getText()+" - simbolo nao identificado");
                lexError = true;
                break;
            }
            
        }

        //Depurar sintático

        if (lexError == false) {
            lex.reset();
    
            CommonTokenStream tokens = new CommonTokenStream(lex);
            LAParser parser = new LAParser(tokens);
    
            // Adicionando nosso ErrorListener customizado
            parser.removeErrorListeners();
            MyCustomErrorListener mcel = new MyCustomErrorListener(pw);
            parser.addErrorListener(mcel);
    
            parser.programa();
        }

        //Depurar semântico

        cs = CharStreams.fromFileName(args[0]);
        lex = new LALexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lex);

        LAParser parser = new LAParser(tokens);
        parser.removeErrorListeners();
        ProgramaContext arvore = parser.programa();

        LASemantico as = new LASemantico();
        as.visitPrograma(arvore);
        LASemanticoUtils.errosSemanticos.forEach((s) -> writer.println(s));
        
        pw.println("Fim da compilacao");
        pw.close();
    }
}

