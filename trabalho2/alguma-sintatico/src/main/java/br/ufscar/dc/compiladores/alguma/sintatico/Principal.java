package br.ufscar.dc.compiladores.alguma.sintatico;

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
    public static void main(String args[]){

        try{
            String arquivoSaida = args[1];
            CharStream cs = CharStreams.fromFileName(args[0]);
            
            try(PrintWriter pw = new PrintWriter(arquivoSaida)) {

                try{
                    AlgumaLexer lex = new AlgumaLexer(cs);

                    //Depura o Léxico
                    Token t = null;
                    boolean lexError = false;

                    while ((t = lex.nextToken()).getType() != Token.EOF) {
                        String nomeToken = AlgumaLexer.VOCABULARY.getDisplayName(t.getType());
                        
                        // ERRO - comentário não fechado
                        if(nomeToken.equals("COMENTARIO_NAO_FECHADO")) {
                            throw new ParseCancellationException("Linha "+t.getLine()+": comentario nao fechado");
                            lexError = true;
                        }
                        
                        // ERRO - cadeia não fechada
                        else if(nomeToken.equals("CADEIA_NAO_FECHADA")) {
                            throw new ParseCancellationException("Linha "+t.getLine()+": cadeia literal nao fechada");
                            lexError = true;
                        }
                        // ERRO - simbolo não identificado 
                        else if(nomeToken.equals("ERRO")) {
                            throw new ParseCancellationException("Linha "+t.getLine()+": "+t.getText()+" - simbolo nao identificado");
                            lexError = true;
                        }
                    }

                    if(lexError = true){

                        cs = CharStreams.fromFileName(args[0]);
                        lex = new AlgumaLexer(cs);

                        CommonTokenStream tokens = new CommonTokenStream(lex);
                        AlgumaParser parser = new AlgumaParser(tokens);
                        MyCustomErrorListener mcel = new MyCustomErrorListener();
                        parser.removeErrorListener();
                        parser.addErrorListener(mcel);

                        parser.programa();
                    }

                } catch (ParseCancellationException ex){
                    pw.println(e.getMessage());
                    pw.println("Fim da compilacao");
                }
            } catch(FileNotFoundException fnfe) {
                System.err.println("O arquivo/diretório não existe:"+args[1]);
            }
        } catch (IOException ex) {
            e.printStackTrace();
        }
    }
}
