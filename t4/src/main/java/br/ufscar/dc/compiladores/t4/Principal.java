package br.ufscar.dc.compiladores.t4;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
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

            LAParser.ProgramaContext arvore = parser.programa();

            LASemantico semantico = new LASemantico();
            semantico.visitPrograma(arvore);
            
            for(String erro : LASemanticoUtils.errosSemanticos){
                out.println(erro);
            }

            out.println("Fim da compilacao");

        } catch (IOException ex) {
            System.err.println("Erro na manipulação dos arquivos: " + ex.getMessage());
        }
    }
}