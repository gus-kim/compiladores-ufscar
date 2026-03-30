package br.ufscar.dc.compiladores.t1;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.IOException;
import java.io.PrintWriter;

public class Principal {

    public static void main(String[] args) {
        if (args.length < 2) return;

        String arquivoEntrada = args[0];
        String arquivoSaida = args[1];

        try (PrintWriter escritor = new PrintWriter(arquivoSaida)) {
            CharStream input = CharStreams.fromFileName(arquivoEntrada);
            LALexer analisador = new LALexer(input);
            
            boolean erroDetectado = false;

            while (!erroDetectado) {
                Token objetoToken = analisador.nextToken();
                String tipoToken = LALexer.VOCABULARY.getSymbolicName(objetoToken.getType());
                String lexema = objetoToken.getText();

                // Final do arquivo
                if (objetoToken.getType() == Token.EOF) break;

                // Verificação de Erros Lexicais
                if (tipoToken.startsWith("ERRO")) {
                    escritor.println(formatarMensagemErro(tipoToken, objetoToken, lexema));
                    erroDetectado = true; // Interrompe a análise conforme o padrão do T1
                } else {
                    // Formatação de Tokens válidos
                    escritor.println(formatarTokenValido(tipoToken, lexema));
                }
            }
        } catch (IOException ex) {
            System.err.println("Falha na leitura/escrita: " + ex.getMessage());
        }
    }

    private static String formatarMensagemErro(String tipo, Token t, String texto) {
        int linha = t.getLine();
        switch (tipo) {
            case "ERRO_SIMBOLO":
                return "Linha " + linha + ": " + texto + " - simbolo nao identificado";
            case "ERRO_CADEIA":
                return "Linha " + linha + ": cadeia literal nao fechada";
            case "ERRO_COMENTARIO":
                return "Linha " + linha + ": comentario nao fechado";
            default:
                return "Linha " + linha + ": erro desconhecido";
        }
    }

    private static String formatarTokenValido(String tipo, String texto) {
        // Categorias que exibem o nome do tipo (IDENT, CADEIA, NUMeros)
        if (tipo.equals("IDENT") || tipo.equals("CADEIA") || tipo.startsWith("NUM_")) {
            return "<'" + texto + "'," + tipo + ">";
        }
        // Palavras reservadas e operadores exibem o próprio texto duas vezes
        return "<'" + texto + "','" + texto + "'>";
    }
}