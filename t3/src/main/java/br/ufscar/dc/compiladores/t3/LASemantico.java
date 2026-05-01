package br.ufscar.dc.compiladores.t3;

import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.TipoLA;

public class LASemantico extends LAParserBaseVisitor<Void> {
    
    Escopos escoposAninhados = new Escopos();

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if (ctx.getText().startsWith("declare")) {
            for (LAParser.IdentificadorContext identCtx : ctx.variavel().identificador()) {
                String nomeVar = identCtx.getText();
                String tipoStr = ctx.variavel().tipo().getText();
                TipoLA tipo = obterTipoLA(tipoStr);

                if (tipo == TipoLA.INVALIDO) {
                    LASemanticoUtils.adicionarErroSemantico(identCtx.start, "tipo " + tipoStr + " nao declarado");
                }

                if (escoposAninhados.obterEscopoAtual().existe(nomeVar)) {
                    LASemanticoUtils.adicionarErroSemantico(identCtx.start, "identificador " + nomeVar + " ja declarado anteriormente");
                } else {
                    escoposAninhados.obterEscopoAtual().adicionar(nomeVar, tipo);
                }
            }
        }
        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String nomeVar = ctx.identificador().getText();
        
        if (!existeEmAlgumEscopo(nomeVar)) {
            LASemanticoUtils.adicionarErroSemantico(ctx.identificador().start, "identificador " + nomeVar + " nao declarado");
        } else {
            TipoLA tipoVariavel = verificarTipoEmAlgumEscopo(nomeVar);
            TipoLA tipoExpressao = LASemanticoUtils.verificarTipo(escoposAninhados, ctx.expressao());
            
            
            // Se a conta for inválida, a atribuição falha imediatamente (Regra 4 do Professor).
            if (!LASemanticoUtils.tiposCompativeis(tipoVariavel, tipoExpressao)) {
                // Checa se é ponteiro sendo desreferenciado (ex: ^variavel)
                String textoErro = ctx.identificador().getText();
                if (ctx.getText().startsWith("^")) {
                    textoErro = "^" + textoErro;
                }
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().start, "atribuicao nao compativel para " + textoErro);
            }
        }
        return super.visitCmdAtribuicao(ctx);
    }

    @Override
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        for (LAParser.IdentificadorContext identCtx : ctx.identificador()) {
            String nomeVar = identCtx.getText();
            if (!existeEmAlgumEscopo(nomeVar)) {
                LASemanticoUtils.adicionarErroSemantico(identCtx.start, "identificador " + nomeVar + " nao declarado");
            }
        }
        return super.visitCmdLeia(ctx);
    }

    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.identificador() != null) {
            String nomeVar = ctx.identificador().getText();
            if (!existeEmAlgumEscopo(nomeVar)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().start, "identificador " + nomeVar + " nao declarado");
            }
        }
        return super.visitParcela_unario(ctx);
    }

    @Override
    public Void visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.identificador() != null) {
            String nomeVar = ctx.identificador().getText();
            if (!existeEmAlgumEscopo(nomeVar)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().start, "identificador " + nomeVar + " nao declarado");
            }
        }
        return super.visitParcela_nao_unario(ctx);
    }

    private boolean existeEmAlgumEscopo(String nome) {
        for (TabelaDeSimbolos ts : escoposAninhados.percorrerEscoposAninhados()) {
            if (ts.existe(nome)) return true;
        }
        return false;
    }

    private TipoLA verificarTipoEmAlgumEscopo(String nome) {
        for (TabelaDeSimbolos ts : escoposAninhados.percorrerEscoposAninhados()) {
            if (ts.existe(nome)) return ts.verificar(nome);
        }
        return TipoLA.INVALIDO;
    }

    private TipoLA obterTipoLA(String tipoStr) {
        if (tipoStr.equals("inteiro")) return TipoLA.INTEIRO;
        if (tipoStr.equals("real")) return TipoLA.REAL;
        if (tipoStr.equals("literal")) return TipoLA.LITERAL;
        if (tipoStr.equals("logico")) return TipoLA.LOGICO;
        if (tipoStr.startsWith("^")) return TipoLA.PONTEIRO;
        return TipoLA.INVALIDO;
    }
}