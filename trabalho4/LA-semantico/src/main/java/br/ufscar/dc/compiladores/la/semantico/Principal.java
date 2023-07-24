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
        }
        
        pw.close();

    }
}

// // Importação de bibliotecas
// package br.ufscar.dc.compiladores.LA;

// import java.io.FileNotFoundException;
// import java.io.PrintWriter;
// import java.io.IOException;

// import org.antlr.v4.runtime.CommonTokenStream;
// import org.antlr.v4.runtime.Token;
// import org.antlr.v4.runtime.CharStream;
// import org.antlr.v4.runtime.CharStreams;
// import org.antlr.v4.runtime.misc.ParseCancellationException;

// import br.ufscar.dc.compiladores.LA.LAParser.ProgramaContext;

// public class Principal {
//     public static void main(String args[]) {

//         try {
//             // Registra em arquivoSaida o caminho do arquivo de saída a partir do segundo argumento
//             String arquivoSaida = args[1];
//             // Registra em cs o arquivo de entrada a partirdo primeiro argumento
//             CharStream cs = CharStreams.fromFileName(args[0]);

//             // Permite escrita no arquivo de saída, criando um PrintWriter pw
//             try (PrintWriter pw = new PrintWriter(arquivoSaida)) { 
//                 try{
//                     // Inicializa o léxico
//                     LALexer lex = new LALexer(cs);
//                     Token t = null;
//                     boolean procede = true;
//                     while ((t = lex.nextToken()).getType() != Token.EOF && procede) {
//                         String nomeToken = LALexer.VOCABULARY.getDisplayName(t.getType());
                        
//                         // Verifica a existência de um símbolo não identicicado
//                         if(nomeToken.equals("ERRO")) { 
//                             procede = false;
//                             throw new ParseCancellationException("Linha " + t.getLine() + ": " + t.getText() + " - simbolo nao identificado");
                        
//                         // Verifica se falta fechar uma cadeira (fecha aspas)
//                         } else if(nomeToken.equals("CADEIA_NAO_FECHADA")) { 
//                             procede = false;
//                             throw new ParseCancellationException("Linha " + t.getLine() + ": cadeia literal nao fechada");
                        
//                         // Verifica se há um comentário não fechado
//                         } else if(nomeToken.equals("COMENTARIO_NAO_FECHADO")) { 
//                             procede = false;
//                             throw new ParseCancellationException("Linha " + t.getLine() + ": comentario nao fechado");
                            
//                         }
//                     }
//                     if(procede){
//                         // Reseta o léxico para reiniciar a análise
//                         lex.reset();

//                         // Cria o fluxo de tokens a partir do léxico 
//                         CommonTokenStream tokens = new CommonTokenStream(lex); 

//                         // Inicializa o parser semântico com os tokens
//                         LAParser parser = new LAParser(tokens);

//                         // Inicializa o listener sintático
//                         SyntaxErrorListener mcel = new SyntaxErrorListener();  
//                         parser.removeErrorListeners();

//                         // Adiciona o listener sintatico
//                         parser.addErrorListener(mcel);    

//                         // Executa a análise sintática, construindo a árvore de análise
//                         ProgramaContext programa = parser.programa();  
                        
//                         // Inicializa o Visitor Semântico, LAvisitor, para realizar a análise semântica
//                         LAvisitor semantic = new LAvisitor();

//                         // Executa a análise semântica
//                         semantic.visitPrograma(programa); 

//                         // Verifica se há erros semânticos
//                         if(!LASemanticUtils.semanticErrors.isEmpty()){

//                             //Percorre os erros semânticos e os imprime no arquivo
//                             for(String s: LASemanticUtils.semanticErrors){
//                                 pw.write(s);
//                             }
//                             pw.write("Fim da compilacao\n");
//                         }
//                     }
//                 }   catch (ParseCancellationException e){
//                     // Imprime a mensagem de erro no arquivo de saída
//                     pw.println(e.getMessage());

//                     // Imprime "Fim da compilacao" no arquivo de saída
//                     pw.println("Fim da compilacao");
//                 }
            
//             // Exceção quando o arquivo de saída não for encontrado
//             }catch(FileNotFoundException fnfe) { 
//                 System.err.println("O arquivo/diretório não existe:"+args[1]);
//             }
//         // Exceção quando não for possível ler o arquivo de entrada ou de saída
//         } catch (IOException e) { 
//             // Imprime o stackTrace para caso os argumentos não sejam passados corretamente
//             e.printStackTrace();
//         }
//     }
// }