package br.ufscar.dc.compiladores.t3;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Principal {
    public static void main(String[] args) {
        // Exige os dois argumentos obrigatórios passados por linha de comando
        if (args.length < 2) {
            System.err.println("Por favor, forneça os arquivos de entrada e saída.");
            return;
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(args[1]))) {
            CharStream cs = CharStreams.fromFileName(args[0]);
            LALexer lexer = new LALexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LAParser parser = new LAParser(tokens);

            // No T3, o analisador não deverá interromper sua execução ao encontrar um erro
            // Então removemos a trava do T2 e deixamos ele ler tudo até o final.

            // 1. O Parser lê o código e monta a árvore sintática
            LAParser.ProgramaContext arvore = parser.programa();

            // 2. Chama o Analisador Semântico (O Detetive que criamos)
            LASemantico semantico = new LASemantico();
            semantico.visitPrograma(arvore);
            
            // 3. Imprime os erros semânticos encontrados guardados na nossa classe Utils
            for(String erro : LASemanticoUtils.errosSemanticos){
                out.println(erro);
            }

            // 4. Ao final, imprimimos a mensagem obrigatória
            out.println("Fim da compilacao");

        } catch (IOException ex) {
            System.err.println("Erro na manipulação dos arquivos: " + ex.getMessage());
        }
    }
}