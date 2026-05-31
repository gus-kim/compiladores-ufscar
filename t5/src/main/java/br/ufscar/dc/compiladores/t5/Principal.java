package br.ufscar.dc.compiladores.t5;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.File;
import java.io.PrintWriter;

public class Principal {
    public static void main(String args[]) {
        try {
            if (args.length < 2) return;

            CharStream cs = CharStreams.fromFileName(args[0]);
            LALexer lexer = new LALexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LAParser parser = new LAParser(tokens);
            LAParser.ProgramaContext arvore = parser.programa();
            
            LASemanticoUtils.errosSemanticos.clear();
            
            LASemantico semantico = new LASemantico();
            semantico.visitPrograma(arvore);

            try (PrintWriter pw = new PrintWriter(new File(args[1]))) {
                if (!LASemanticoUtils.errosSemanticos.isEmpty()) {
                    for (String erro : LASemanticoUtils.errosSemanticos) {
                        pw.println(erro);
                    }
                    pw.println("Fim da compilacao");
                } else {
                    LAGeradorC gerador = new LAGeradorC();
                    gerador.visitPrograma(arvore);
                    pw.print(gerador.saida.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}