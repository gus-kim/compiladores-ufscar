package br.ufscar.dc.compiladores.t5;

import org.antlr.v4.runtime.Token;
import java.util.ArrayList;
import java.util.List;

import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.TipoLA;

public class LASemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();

    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }

    public static TabelaDeSimbolos.EntradaTabelaDeSimbolos obterEntrada(Escopos escopos, String nome) {
        String nomeLimpo = nome.replaceAll("\\[.*?\\]", "");
        if (nomeLimpo.contains(".")) {
            String[] partes = nomeLimpo.split("\\.");
            for (TabelaDeSimbolos ts : escopos.percorrerEscoposAninhados()) {
                if (ts.existe(partes[0])) {
                    TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = ts.obterEntrada(partes[0]);
                    for (int i = 1; i < partes.length; i++) {
                        if (entrada.camposRegistro != null && entrada.camposRegistro.existe(partes[i])) {
                            entrada = entrada.camposRegistro.obterEntrada(partes[i]);
                        } else {
                            return null;
                        }
                    }
                    return entrada;
                }
            }
            return null;
        } else {
            for (TabelaDeSimbolos ts : escopos.percorrerEscoposAninhados()) {
                if (ts.existe(nomeLimpo)) return ts.obterEntrada(nomeLimpo);
            }
            return null;
        }
    }

    // Regra normal para Atribuições (ex: real <- inteiro é válido)
    public static boolean tiposCompativeis(TipoLA tipoVariavel, TipoLA tipoExpressao) {
        if (tipoExpressao == TipoLA.INVALIDO) return false;
        if (tipoVariavel == tipoExpressao) return true;
        if (tipoVariavel == TipoLA.REAL && tipoExpressao == TipoLA.INTEIRO) return true;
        if (tipoVariavel == TipoLA.PONTEIRO && (tipoExpressao == TipoLA.ENDERECO || tipoExpressao == TipoLA.PONTEIRO)) return true;
        return false;
    }

    // Regra estrita para Parâmetros (ex: real -> inteiro é INVALIDO)
    public static boolean tiposCompativeisParametros(TipoLA tipoParametro, TipoLA tipoExpressao) {
        if (tipoExpressao == TipoLA.INVALIDO) return false;
        if (tipoParametro == tipoExpressao) return true;
        if (tipoParametro == TipoLA.PONTEIRO && (tipoExpressao == TipoLA.ENDERECO || tipoExpressao == TipoLA.PONTEIRO)) return true;
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
        if (ctx.getText().startsWith("&")) {
            return TipoLA.ENDERECO;
        }
        if (ctx.NUM_INT() != null) return TipoLA.INTEIRO;
        if (ctx.NUM_REAL() != null) return TipoLA.REAL;
        
        if (ctx.identificador() != null) {
            String nomeCompleto = ctx.identificador().getText();
            String nomeVar = nomeCompleto.replaceAll("\\[.*?\\]", "");
            TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = obterEntrada(escopos, nomeVar);
            if (entrada != null) {
                TipoLA tipo = entrada.tipo;
                if (ctx.getText().startsWith("^")) {
                    String tipoCustom = entrada.tipoCustomizado;
                    if (tipoCustom != null && tipoCustom.startsWith("^")) {
                        String base = tipoCustom.substring(1);
                        if (base.equals("inteiro")) tipo = TipoLA.INTEIRO;
                        else if (base.equals("real")) tipo = TipoLA.REAL;
                        else if (base.equals("logico")) tipo = TipoLA.LOGICO;
                        else if (base.equals("literal")) tipo = TipoLA.LITERAL;
                    }
                }
                
                if (ctx.getText().contains("(") && ctx.expressao() != null) {
                    List<LAParser.ExpressaoContext> args = ctx.expressao();
                    if (entrada.tiposParametros.size() != args.size()) {
                        adicionarErroSemantico(ctx.identificador().start, "incompatibilidade de parametros na chamada de " + nomeVar);
                    } else {
                        for (int i = 0; i < args.size(); i++) {
                            TipoLA argTipo = verificarTipo(escopos, args.get(i));
                            TipoLA paramTipo = entrada.tiposParametros.get(i);
                            if (!tiposCompativeisParametros(paramTipo, argTipo)) {
                                adicionarErroSemantico(ctx.identificador().start, "incompatibilidade de parametros na chamada de " + nomeVar);
                                break;
                            }
                        }
                    }
                }
                
                return tipo;
            }
            return TipoLA.INVALIDO;
        }
        
        if (ctx.IDENT() != null) {
            String nome = ctx.IDENT().getText();
            TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = obterEntrada(escopos, nome);
            if (entrada != null) {
                if (ctx.getText().contains("(")) {
                    List<LAParser.ExpressaoContext> args = ctx.expressao();
                    if (entrada.tiposParametros.size() != args.size()) {
                        adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nome);
                    } else {
                        for (int i = 0; i < args.size(); i++) {
                            TipoLA argTipo = verificarTipo(escopos, args.get(i));
                            TipoLA paramTipo = entrada.tiposParametros.get(i);
                            if (!tiposCompativeisParametros(paramTipo, argTipo)) {
                                adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nome);
                                break;
                            }
                        }
                    }
                }
                return entrada.tipo;
            }
            return TipoLA.INVALIDO;
        }
        if (ctx.expressao() != null && !ctx.expressao().isEmpty()) {
            return verificarTipo(escopos, ctx.expressao().get(0));
        }
        return TipoLA.INVALIDO;
    }

    public static TipoLA verificarTipo(Escopos escopos, LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return TipoLA.LITERAL;
        if (ctx.identificador() != null) {
            String nome = ctx.identificador().getText();
            TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = obterEntrada(escopos, nome);
            if (entrada != null) return entrada.tipo;
        }
        return TipoLA.INVALIDO;
    }
}