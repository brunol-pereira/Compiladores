package br.ufscar.dc.compiladores.alguma.sintatico;

import static java.lang.System.exit;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import java.io.File;
import java.io.PrintWriter;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class Principal {
    public static void main(String args[]) throws IOException {
        

        String arquivoSaida = args[1];
        PrintWriter pw = new PrintWriter(arquivoSaida);

        CharStream cs = CharStreams.fromFileName(args[0]);
        AlgumaLexer lex = new AlgumaLexer(cs);

        //Depura o Léxico
        Token t = null;

        while ((t = lex.nextToken()).getType() != Token.EOF) {
            String nomeToken = AlgumaLexer.VOCABULARY.getDisplayName(t.getType());
            
            // ERRO - comentário não fechado
            if(nomeToken.equals("COMENTARIO_NAO_FECHADO")) {
                pw.println("Linha "+t.getLine()+": comentario nao fechado\n");
                pw.println("Fim da compilacao\n");
                pw.close();
                exit(0);
            }
            
            // ERRO - cadeia não fechada
            else if(nomeToken.equals("CADEIA_NAO_FECHADA")) {
                pw.println("Linha "+t.getLine()+": cadeia literal nao fechada\n");
                pw.println("Fim da compilacao\n");
                pw.close();
                exit(0);
            }
            // ERRO - simbolo não identificado 
            else if(nomeToken.equals("ERRO")) {
                pw.println("Linha "+t.getLine()+": "+t.getText()+" - simbolo nao identificado\n");
                pw.println("Fim da compilacao\n");
                pw.close();
                exit(0);
            }
        }


        lex.reset();
        pw = new PrintWriter(arquivoSaida);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        AlgumaParser parser = new AlgumaParser(tokens);
        MyCustomErrorListener mcel = new MyCustomErrorListener(pw);
        parser.removeErrorListener();
        parser.addErrorListener(mcel);

        parser.programa();
    }
}
