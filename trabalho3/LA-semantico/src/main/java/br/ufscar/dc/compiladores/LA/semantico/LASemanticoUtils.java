package br.ufscar.dc.compiladores.LA.semantico;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

public class LASemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    
    public static void adicionarErroSemantico(Token t, String mensagem) {
        // Obtém o número da linha do token onde ocorreu o erro
        int linha = t.getLine();
        //int coluna = t.getCharPositionInLine();
        // Adiciona o erro à lista de erros semânticos
        errosSemanticos.add(String.format("Erro %d: %s", linha, mensagem));
    }
    
    public static TabelaDeSimbolos.VarTipoLA verificarTipo(TabelaDeSimbolos tabelaDeSimbolos, LAParser.ExpressaoAritmeticaContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;
        // Itera sobre os termos aritméticos na expressão
        for (var ta : ctx.termoAritmetico()) {
            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabela, ta);
            // Verifica o tipo de cada termo aritmético
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLA.INVALIDO) 
             // Se houver tipos incompatíveis na expressão, adiciona um erro semântico
                adicionarErroSemantico(ctx.start, "Expressão " + ctx.getText() + " contém tipos incompatíveis");
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }

        return ret;
    }

    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LAParser.TermoAritmeticoContext ctx) {
        TabelaDeSimbolos.TipoLA ret = null;

        // Itera sobre os fatores aritméticos no termo
        for (var fa : ctx.fatorAritmetico()) {
            // Verifica o tipo de cada fator aritmético
            TabelaDeSimbolos.TipoLA aux = verificarTipo(tabela, fa);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != TabelaDeSimbolos.TipoLA.INVALIDO) {
                // Se houver tipos incompatíveis no termo, adiciona um erro semântico
                adicionarErroSemantico(ctx.start, "Termo " + ctx.getText() + " contém tipos incompatíveis");
                ret = TabelaDeSimbolos.TipoLA.INVALIDO;
            }
        }
        return ret;
    }

    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, LAParser.FatorAritmeticoContext ctx) {
        // Verifica o tipo do fator aritmético
        if (ctx.NUMINT() != null) {
            // Se for um número inteiro, retorna o tipo INTEIRO
            return TabelaDeSimbolos.TipoLA.INTEIRO;
        }
        if (ctx.NUMREAL() != null) {
            // Se for um número real, retorna o tipo REAL
            return TabelaDeSimbolos.TipoLA.REAL;
        }
        if (ctx.VARIAVEL() != null) {
            // Se for uma variável, verifica se ela foi declarada antes do uso
            String nomeVar = ctx.VARIAVEL().getText();
            if (!tabela.existe(nomeVar)) {
                // Se a variável não foi declarada, adiciona um erro semântico
                adicionarErroSemantico(ctx.VARIAVEL().getSymbol(), "Variável " + nomeVar + " não foi declarada antes do uso");
                return TabelaDeSimbolos.TipoLA.INVALIDO;
            }
            // Retorna o tipo da variável
            return verificarTipo(tabela, nomeVar);
        }
        // Caso não seja nenhum dos tipos acima, só pode ser uma expressão entre parêntesis
        // Verifica o tipo da expressão aritmética dentro dos parêntesis
        return verificarTipo(tabela, ctx.expressaoAritmetica());
    }
    
    public static TabelaDeSimbolos.TipoLA verificarTipo(TabelaDeSimbolos tabela, String nomeVar) {
        // Verifica o tipo de uma variável na tabela de símbolos
        return tabela.verificar(nomeVar);
    }
}