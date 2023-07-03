
package br.ufscar.dc.compiladores.expr.parser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

public class Principal {
    public static void main(String args[]) throws IOException {
        CharStream cs = CharStreams.fromFileName(args[0]);

        LALexer lexer = new LALexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        LAParser parser = new LAParser(tokens);
        ProgramaContext arvore = parser.programa();

        LASemantico as = new LASemantico();
        as.visitPrograma(arvore);
        LASemanticoUtils.errosSemanticos.forEach((s) -> System.out.println(s));
    }
}

//Para caso der erro

// public class Principal {

//     public static void main(String args[]) throws IOException {
//         CharStream cs = CharStreams.fromFileName(args[0]);
//         ExpressoesLexer lexer = new ExpressoesLexer(cs);
//         CommonTokenStream tokens = new CommonTokenStream(lexer);
//         ExpressoesParser parser = new ExpressoesParser(tokens);
//         ProgramaContext arvore = parser.programa();
//         ExpressoesSemantico as = new AlgumaSemantico();
//          parser.programa();

//     }
// }