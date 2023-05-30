package br.ufscar.dc.compiladores.alguma.lexico;

import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Principal {

    public static void main(String[] args) {
        String arquivoSaida = args[1];
        try(PrintWriter pw = new PrintWriter(arquivoSaida)) {
            try {
                CharStream cs = CharStreams.fromFileName(args[0]);
                AlgumaLexer lex = new AlgumaLexer(cs);
    
                Token t = null;
                while ((t = lex.nextToken()).getType() != Token.EOF) {
                    String nomeToken = AlgumaLexer.VOCABULARY.getDisplayName(t.getType());
                    System.out.print("<" + AlgumaLexer.VOCABULARY.getDisplayName(t.getType())+","+ t.getText()+">");
                    
                    // ERRO comentário não fechado
                    if(nomeToken.equals("COMENTARIO_NAO_FECHADO")) {
                        pw.println("Linha "+t.getLine()+": comentario nao fechado");
                        break;
                    }
                    
                    // ERRO cadeia não fechada
                    else if(nomeToken.equals("CADEIA_NAO_FECHADA")) {
                        pw.println("Linha "+t.getLine()+": cadeia literal nao fechada");
                        break;
                    }
                    // ERRO - simbolo não identificado 
                    else if(nomeToken.equals("ERRO")) {
                        pw.println("Linha "+t.getLine()+": "+t.getText()+" - simbolo nao identificado");
                        break;
                    }
                    else {
                        pw.println("<'" + t.getText() + "'," + nomeToken  + ">");
                    }
                }
            } catch (IOException ex) {
            }
        } catch(FileNotFoundException fnfe) {
            System.err.println("O arquivo/diretório não existe:"+args[1]);
        }
    }
}