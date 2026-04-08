package br.ufscar.dc.compiladores.t2;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Principal {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Por favor, forneça os arquivos de entrada e saída.");
            return;
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(args[1]))) {
            CharStream cs = CharStreams.fromFileName(args[0]);
            LALexer lexer = new LALexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LAParser parser = new LAParser(tokens);

            // Remove os tratadores de erro padrão que cospem no terminal
            parser.removeErrorListeners();

            // Adiciona o nosso tratador de erros customizado
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    Token t = (Token) offendingSymbol;
                    String texto = t.getText();
                    String nomeToken = LALexer.VOCABULARY.getSymbolicName(t.getType());

                    // Tratamento de Erros Léxicos (T1)
                    if (nomeToken != null) {
                        if (nomeToken.equals("ERRO_CADEIA")) {
                            out.println("Linha " + line + ": cadeia literal nao fechada");
                        } else if (nomeToken.equals("ERRO_COMENTARIO")) {
                            out.println("Linha " + line + ": comentario nao fechado");
                        } else if (nomeToken.equals("ERRO_SIMBOLO")) {
                            out.println("Linha " + line + ": " + texto + " - simbolo nao identificado");
                        } else {
                            // Erro Sintático
                            if (texto.equals("<EOF>")) texto = "EOF";
                            out.println("Linha " + line + ": erro sintatico proximo a " + texto);
                        }
                    } else {
                        if (texto.equals("<EOF>")) texto = "EOF";
                        out.println("Linha " + line + ": erro sintatico proximo a " + texto);
                    }

                    // Imprime o fim e interrompe a análise lançando uma exceção
                    out.println("Fim da compilacao");
                    throw new ParseCancellationException("Erro encontrado, parando o parser.");
                }
            });

            // Bloco try-catch para lidar com a nossa exceção de interrupção
            try {
                parser.programa();
                // Se rodou tudo sem erros e sem lançar a exceção:
                out.println("Fim da compilacao");
            } catch (ParseCancellationException e) {
                // Cai aqui quando o erro é encontrado e o arquivo é salvo em segurança.
            }

        } catch (IOException ex) {
            System.err.println("Erro na manipulação dos arquivos: " + ex.getMessage());
        }
    }
}