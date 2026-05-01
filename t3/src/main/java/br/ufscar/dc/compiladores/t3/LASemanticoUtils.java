package br.ufscar.dc.compiladores.t3;

import org.antlr.v4.runtime.Token;
import java.util.ArrayList;
import java.util.List;
import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.TipoLA;

public class LASemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();

    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }

    public static boolean tiposCompativeis(TipoLA tipoVariavel, TipoLA tipoExpressao) {
        // Se a expressão for inválida (ex: soma de literal com inteiro), NUNCA é compatível
        if (tipoExpressao == TipoLA.INVALIDO) return false;
        
        if (tipoVariavel == tipoExpressao) return true;
        if (tipoVariavel == TipoLA.REAL && tipoExpressao == TipoLA.INTEIRO) return true;
        return false;
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.ExpressaoContext ctx) {
        TipoLA ret = null;
        for (LAParser.Termo_logicoContext tl : ctx.termo_logico()) {
            TipoLA aux = verificarTipo(escopos, tl);
            if (ret == null) ret = aux;
            else if (ret != aux && aux != TipoLA.INVALIDO) ret = TipoLA.INVALIDO;
        }
        return ret;
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.Termo_logicoContext ctx) {
        TipoLA ret = null;
        for (LAParser.Fator_logicoContext fl : ctx.fator_logico()) {
            TipoLA aux = verificarTipo(escopos, fl);
            if (ret == null) ret = aux;
            else if (ret != aux && aux != TipoLA.INVALIDO) ret = TipoLA.INVALIDO;
        }
        return ret;
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.Fator_logicoContext ctx) {
        return verificarTipo(escopos, ctx.parcela_logica());
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.Parcela_logicaContext ctx) {
        if (ctx.exp_relacional() != null) return verificarTipo(escopos, ctx.exp_relacional());
        return TipoLA.LOGICO; 
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.Exp_relacionalContext ctx) {
        TipoLA ret = null;
        if (ctx.exp_aritmetica().size() == 1) {
            for (LAParser.Exp_aritmeticaContext ea : ctx.exp_aritmetica()) {
                TipoLA aux = verificarTipo(escopos, ea);
                if (ret == null) ret = aux;
                else if (ret != aux && aux != TipoLA.INVALIDO) ret = TipoLA.INVALIDO;
            }
        } else {
            for (LAParser.Exp_aritmeticaContext ea : ctx.exp_aritmetica()) {
                verificarTipo(escopos, ea); 
            }
            return TipoLA.LOGICO; 
        }
        return ret;
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.Exp_aritmeticaContext ctx) {
        TipoLA ret = null;
        for (LAParser.TermoContext te : ctx.termo()) {
            TipoLA aux = verificarTipo(escopos, te);
            if (ret == null) ret = aux;
            else if (ret != aux && aux != TipoLA.INVALIDO) {
                if ((ret == TipoLA.INTEIRO && aux == TipoLA.REAL) || (ret == TipoLA.REAL && aux == TipoLA.INTEIRO)) {
                    ret = TipoLA.REAL;
                } else ret = TipoLA.INVALIDO;
            }
        }
        return ret;
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.TermoContext ctx) {
        TipoLA ret = null;
        for (LAParser.FatorContext fa : ctx.fator()) {
            TipoLA aux = verificarTipo(escopos, fa);
            if (ret == null) ret = aux;
            else if (ret != aux && aux != TipoLA.INVALIDO) {
                if ((ret == TipoLA.INTEIRO && aux == TipoLA.REAL) || (ret == TipoLA.REAL && aux == TipoLA.INTEIRO)) {
                    ret = TipoLA.REAL;
                } else ret = TipoLA.INVALIDO;
            }
        }
        return ret;
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.FatorContext ctx) {
        TipoLA ret = null;
        for (LAParser.ParcelaContext pa : ctx.parcela()) {
            TipoLA aux = verificarTipo(escopos, pa);
            if (ret == null) ret = aux;
            else if (ret != aux && aux != TipoLA.INVALIDO) {
                if ((ret == TipoLA.INTEIRO && aux == TipoLA.REAL) || (ret == TipoLA.REAL && aux == TipoLA.INTEIRO)) {
                    ret = TipoLA.REAL;
                } else ret = TipoLA.INVALIDO;
            }
        }
        return ret;
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) return verificarTipo(escopos, ctx.parcela_unario());
        return verificarTipo(escopos, ctx.parcela_nao_unario());
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null) return TipoLA.INTEIRO;
        if (ctx.NUM_REAL() != null) return TipoLA.REAL;
        if (ctx.identificador() != null) {
            String nome = ctx.identificador().getText();
            TipoLA tipo = TipoLA.INVALIDO;
            for (TabelaDeSimbolos ts : escopos.percorrerEscoposAninhados()) {
                if (ts.existe(nome)) {
                    tipo = ts.verificar(nome);
                    break;
                }
            }
            // Se tiver um '&' comercial, é um endereço sendo atribuído
            if (ctx.getText().startsWith("&")) {
                return TipoLA.PONTEIRO; // Simplificamos para manter a compatibilidade
            }
            return tipo;
        }
        if (ctx.IDENT() != null) {
             String nome = ctx.IDENT().getText();
             for (TabelaDeSimbolos ts : escopos.percorrerEscoposAninhados()) {
                if (ts.existe(nome)) return ts.verificar(nome);
            }
            return TipoLA.INVALIDO;
        }
        if (ctx.expressao() != null) {
            return verificarTipo(escopos, ctx.expressao().get(0));
        }
        return TipoLA.INVALIDO;
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return TipoLA.LITERAL;
        if (ctx.identificador() != null) {
            String nome = ctx.identificador().getText();
             for (TabelaDeSimbolos ts : escopos.percorrerEscoposAninhados()) {
                if (ts.existe(nome)) return ts.verificar(nome);
            }
        }
        return TipoLA.INVALIDO;
    }
}