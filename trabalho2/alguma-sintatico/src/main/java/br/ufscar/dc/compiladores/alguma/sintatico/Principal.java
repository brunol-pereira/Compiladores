package br.ufscar.dc.compiladores.alguma.sintatico;

import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import java.io.File;
import java.io.PrintWriter;

public class Principal {
    public static void main(String args[]) throws IOException {
        PrintWriter writer = null;

        // Altera a saída do programa de acordo com a quantidade de argumentos
        switch (args.length) {
            case 1:
                // Opção para entrada de um único argumento de entrada e saída na saída padrão
                writer = new PrintWriter(System.out);
                writer.println();
                break;
            case 2:
                // Opção padrão para dois argumentos, saída em arquivo
                try {
                    writer = new PrintWriter(new File(args[1]));
                } catch (Exception e) {
                    System.out.println("Falha ao abrir o arquivo");
                    e.printStackTrace();
                }
                break;
            default:
                // Número inválido de argumentos
                System.out.println("Número inválido de argumentos!");
                System.out.println("Recebeu " + args.length + " argumentos, esperava no mínimo 1:");
                System.out.println("<caminho para o código fonte LA> [caminho para arquivo de saída]");
                return; // Termina o programa prematuramente
        }

        // Análise Léxica
        CharStream cs = CharStreams.fromFileName(args[0]);
        AlgumaLexer lex = new AlgumaLexer(cs);
        Boolean erroLexico = false;

        Token t = null;
        while ((t = lex.nextToken()).getType() != Token.EOF) {
            String nomeToken = AlgumaLexer.VOCABULARY.getDisplayName(t.getType());

            // ERRO comentário não fechado
            if (nomeToken.equals("COMENTARIO_NAO_FECHADO")) {
                writer.println("Linha " + t.getLine() + ": comentario nao fechado");
                erroLexico = true;
                break;
            }

            // ERRO cadeia não fechada
            else if (nomeToken.equals("CADEIA_NAO_FECHADA")) {
                writer.println("Linha " + t.getLine() + ": cadeia literal nao fechada");
                erroLexico = true;
                break;
            }
            // ERRO - simbolo não identificado
            else if (nomeToken.equals("ERRO")) {
                writer.println("Linha " + t.getLine() + ": " + t.getText() + " - simbolo nao identificado");
                erroLexico = true;
                break;
            }
        }

        // Análise Sintática
        if (!erroLexico) {
            cs = CharStreams.fromFileName(args[0]);
            AlgumaLexer lexer = new AlgumaLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AlgumaParser parser = new AlgumaParser(tokens);

            MyCustomErrorListener mcel = new MyCustomErrorListener(writer);
            parser.addErrorListener(mcel);

            parser.programa();
        }

        // writer.println("Fim da compilacao");
        // writer.close();
    }
}
