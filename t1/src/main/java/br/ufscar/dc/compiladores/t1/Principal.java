package br.ufscar.dc.compiladores.t1; // Adicionado para alinhar com sua estrutura de pastas

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Classe Principal Adaptada:
 * - Nomenclaturas de erro sincronizadas com seu Lexer funcional.
 * - Mensagens de erro limpas (sem a palavra "literal").
 * - Estrutura de pacotes compatível com ambiente UFSCar/Maven.
 */
public class Principal {
    public static void main(String[] args) {
        // Validação básica de argumentos
        if (args.length < 2) {
            return;
        }

        // Uso de FileWriter dentro do PrintWriter para garantir a escrita correta no disco
        try (PrintWriter pw = new PrintWriter(new FileWriter(args[1]))) {
            CharStream cs = CharStreams.fromFileName(args[0]);
            LALexer lexer = new LALexer(cs);

            Token t;
            while ((t = lexer.nextToken()).getType() != Token.EOF) {
                String nomeToken = LALexer.VOCABULARY.getSymbolicName(t.getType());
                String textoToken = t.getText();

                // --- TRATAMENTO DE ERROS (Sincronizado com seu Lexer) ---
                
                if (nomeToken.equals("ERRO_SIMBOLO")) {
                    pw.println("Linha " + t.getLine() + ": " + textoToken + " - simbolo nao identificado");
                    break; 
                } 
                else if (nomeToken.equals("ERRO_CADEIA")) { // Nome que roda na sua máquina
                    pw.println("Linha " + t.getLine() + ": cadeia literal nao fechada");
                    break;
                } 
                else if (nomeToken.equals("ERRO_COMENTARIO")) { // Nome que roda na sua máquina
                    pw.println("Linha " + t.getLine() + ": comentario nao fechado");
                    break;
                }

                // --- FORMATAÇÃO DE TOKENS REGULARES ---

                if (nomeToken.equals("IDENT") || nomeToken.equals("CADEIA") ||
                    nomeToken.equals("NUM_INT") || nomeToken.equals("NUM_REAL")) {
                    // Saída para tipos variáveis: <'valor',TIPO>
                    pw.println("<'" + textoToken + "'," + nomeToken + ">");
                } else {
                    // Saída para palavras-chave e símbolos: <'valor','valor'>
                    pw.println("<'" + textoToken + "','" + textoToken + "'>");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao manipular arquivos: " + e.getMessage());
        }
    }
}