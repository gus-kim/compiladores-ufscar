package br.ufscar.dc.compiladores.t1;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.PrintWriter;
import java.io.IOException;

public class Principal {
    public static void main(String[] args) {
        // Validação obrigatória dos dois argumentos (Entrada e Saída)
        if (args.length < 2) {
            System.out.println("Uso: java -jar t1.jar <caminho_entrada> <caminho_saida>");
            return;
        }

        try (PrintWriter pw = new PrintWriter(args[1], "UTF-8")) {
            CharStream cs = CharStreams.fromFileName(args[0]);
            LALexer lexer = new LALexer(cs);

            Token t = null;
            // Varre o arquivo até o fim (EOF) ou até encontrar um erro léxico
            while ((t = lexer.nextToken()).getType() != Token.EOF) {
                String nomeToken = LALexer.VOCABULARY.getDisplayName(t.getType());
                String texto = t.getText();

                // Interrompe a execução e reporta erros específicos
                if (nomeToken.equals("ERRO_COMENTARIO")) {
                    pw.println("Linha " + t.getLine() + ": comentario nao fechado");
                    break;
                } else if (nomeToken.equals("ERRO_CADEIA")) {
                    pw.println("Linha " + t.getLine() + ": cadeia nao fechada");
                    break;
                } else if (nomeToken.equals("ERRO_SIMBOLO")) {
                    pw.println("Linha " + t.getLine() + ": " + texto + " - simbolo nao identificado");
                    break;
                } 
                // Formatação correta para Identificadores e Cadeias
                else if (nomeToken.equals("IDENT")) {
                    pw.println("<'" + texto + "', IDENT>");
                } else if (nomeToken.equals("CADEIA")) {
                    pw.println("<" + texto + ", CADEIA>");
                } 
                // Formatação para palavras-chave e símbolos normais
                else {
                    pw.println("<'" + texto + "', '" + texto + "'>");
                }
            }
        } catch (IOException ex) {
            System.err.println("Erro na leitura/escrita do arquivo: " + ex.getMessage());
        }
    }
}