package br.ufscar.dc.compiladores.t5;

import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.TipoLA;
import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.EstruturaLA;
import java.util.List;

public class LASemantico extends LAParserBaseVisitor<Void> {
    
    Escopos escoposAninhados = new Escopos();

    private TabelaDeSimbolos.EntradaTabelaDeSimbolos obterEntrada(String nome) {
        return LASemanticoUtils.obterEntrada(escoposAninhados, nome);
    }

    private boolean existeEmAlgumEscopo(String nome) {
        return obterEntrada(nome) != null;
    }

    private TipoLA verificarTipoEmAlgumEscopo(String nome) {
        TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = obterEntrada(nome);
        return entrada != null ? entrada.tipo : TipoLA.INVALIDO;
    }

    private void popularRegistro(TabelaDeSimbolos campos, LAParser.RegistroContext ctx) {
        for (LAParser.VariavelContext varCtx : ctx.variavel()) {
            LAParser.TipoContext tipoCtx = varCtx.tipo();
            TipoLA tipo = obterTipoLA(tipoCtx);
            String tipoStr = tipoCtx.getText();
            for (LAParser.IdentificadorContext identCtx : varCtx.identificador()) {
                String nomeVar = identCtx.getText().replaceAll("\\[.*?\\]", "");
                campos.adicionar(nomeVar, tipo, EstruturaLA.VARIAVEL);
                TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = campos.obterEntrada(nomeVar);
                entrada.tipoCustomizado = tipoStr;
                if (tipoCtx.registro() != null) {
                    popularRegistro(entrada.camposRegistro, tipoCtx.registro());
                } else if (tipo == TipoLA.REGISTRO || tipo == TipoLA.PONTEIRO) {
                    String baseType = tipoStr;
                    if (baseType.startsWith("^")) baseType = baseType.substring(1);
                    TabelaDeSimbolos.EntradaTabelaDeSimbolos tipoEntrada = LASemanticoUtils.obterEntrada(escoposAninhados, baseType);
                    if (tipoEntrada != null && tipoEntrada.estrutura == EstruturaLA.TIPO) {
                        entrada.camposRegistro = tipoEntrada.camposRegistro;
                    }
                }
            }
        }
    }

    private TipoLA obterTipoLA(LAParser.TipoContext ctx) {
        if (ctx.registro() != null) return TipoLA.REGISTRO;
        return obterTipoLA(ctx.getText());
    }

    private TipoLA obterTipoLA(String tipoStr) {
        if (tipoStr.equals("inteiro")) return TipoLA.INTEIRO;
        if (tipoStr.equals("real")) return TipoLA.REAL;
        if (tipoStr.equals("literal")) return TipoLA.LITERAL;
        if (tipoStr.equals("logico")) return TipoLA.LOGICO;
        if (tipoStr.startsWith("^")) return TipoLA.PONTEIRO;
        
        for (TabelaDeSimbolos ts : escoposAninhados.percorrerEscoposAninhados()) {
            if (ts.existe(tipoStr) && ts.obterEntrada(tipoStr).estrutura == EstruturaLA.TIPO) {
                return TipoLA.REGISTRO;
            }
        }
        return TipoLA.INVALIDO;
    }

    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        return super.visitPrograma(ctx);
    }

    @Override
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        String nome = ctx.IDENT().getText();
        EstruturaLA estrutura = ctx.getText().startsWith("procedimento") ? EstruturaLA.PROCEDIMENTO : EstruturaLA.FUNCAO;
        TipoLA tipoRetorno = TipoLA.INVALIDO;

        if (estrutura == EstruturaLA.FUNCAO && ctx.tipo_estendido() != null) {
            tipoRetorno = obterTipoLA(ctx.tipo_estendido().getText());
        }

        if (existeEmAlgumEscopo(nome)) {
            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + nome + " ja declarado anteriormente");
        } else {
            escoposAninhados.obterEscopoAtual().adicionar(nome, tipoRetorno, estrutura);
        }

        escoposAninhados.criarNovoEscopo();
        TabelaDeSimbolos.EntradaTabelaDeSimbolos funcStruct = obterEntrada(nome);

        if (ctx.parametros() != null) {
            for (LAParser.ParametroContext pCtx : ctx.parametros().parametro()) {
                TipoLA tipoParam = obterTipoLA(pCtx.tipo_estendido().getText());
                String tipoCustomStr = pCtx.tipo_estendido().getText();

                for (LAParser.IdentificadorContext idCtx : pCtx.identificador()) {
                    String nomeParam = idCtx.getText().replaceAll("\\[.*?\\]", "");
                    escoposAninhados.obterEscopoAtual().adicionar(nomeParam, tipoParam, EstruturaLA.VARIAVEL);
                    TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = escoposAninhados.obterEscopoAtual().obterEntrada(nomeParam);
                    entrada.tipoCustomizado = tipoCustomStr;

                    if (tipoParam == TipoLA.REGISTRO || tipoParam == TipoLA.PONTEIRO) {
                        String baseType = tipoCustomStr;
                        if (baseType.startsWith("^")) baseType = baseType.substring(1);
                        TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaTipo = LASemanticoUtils.obterEntrada(escoposAninhados, baseType);
                        if (entradaTipo != null && entradaTipo.estrutura == EstruturaLA.TIPO) {
                            entrada.camposRegistro = entradaTipo.camposRegistro;
                        }
                    }

                    if (funcStruct != null) {
                        funcStruct.tiposParametros.add(tipoParam);
                    }
                }
            }
        }

        TipoLA oldRet = escoposAninhados.getTipoRetornoEscopoAtual();
        escoposAninhados.setTipoRetornoEscopoAtual(tipoRetorno);

        super.visitDeclaracao_global(ctx);

        escoposAninhados.abandonarEscopo();
        escoposAninhados.setTipoRetornoEscopoAtual(oldRet);

        return null;
    }

    @Override
    public Void visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        String nome = ctx.IDENT().getText();
        if (!existeEmAlgumEscopo(nome)) {
            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + nome + " nao declarado");
        } else {
            TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = obterEntrada(nome);
            if (entrada != null && (entrada.estrutura == EstruturaLA.PROCEDIMENTO || entrada.estrutura == EstruturaLA.FUNCAO)) {
                List<LAParser.ExpressaoContext> args = ctx.expressao();
                if (entrada.tiposParametros.size() != args.size()) {
                    LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nome);
                } else {
                    for (int i = 0; i < args.size(); i++) {
                        TipoLA argTipo = LASemanticoUtils.verificarTipo(escoposAninhados, args.get(i));
                        TipoLA paramTipo = entrada.tiposParametros.get(i);
                        if (!LASemanticoUtils.tiposCompativeisParametros(paramTipo, argTipo)) {
                            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nome);
                            break;
                        }
                    }
                }
            }
        }
        return super.visitCmdChamada(ctx);
    }

    @Override
    public Void visitCmdRetorne(LAParser.CmdRetorneContext ctx) {
        if (escoposAninhados.getTipoRetornoEscopoAtual() == TipoLA.INVALIDO) {
            LASemanticoUtils.adicionarErroSemantico(ctx.start, "comando retorne nao permitido nesse escopo");
        }
        return super.visitCmdRetorne(ctx);
    }

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if (ctx.getText().startsWith("declare")) {
            LAParser.VariavelContext varCtx = ctx.variavel();
            LAParser.TipoContext tipoCtx = varCtx.tipo();
            TipoLA tipo = obterTipoLA(tipoCtx);
            String tipoStr = tipoCtx.getText();

            for (LAParser.IdentificadorContext identCtx : varCtx.identificador()) {
                String nomeVar = identCtx.getText().replaceAll("\\[.*?\\]", "");

                if (tipo == TipoLA.INVALIDO) {
                    LASemanticoUtils.adicionarErroSemantico(identCtx.start, "tipo " + tipoStr + " nao declarado");
                }

                if (existeEmAlgumEscopo(nomeVar)) {
                    LASemanticoUtils.adicionarErroSemantico(identCtx.start, "identificador " + nomeVar + " ja declarado anteriormente");
                } else {
                    escoposAninhados.obterEscopoAtual().adicionar(nomeVar, tipo, EstruturaLA.VARIAVEL);
                    TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = escoposAninhados.obterEscopoAtual().obterEntrada(nomeVar);
                    entrada.tipoCustomizado = tipoStr;

                    if (tipoCtx.registro() != null) {
                        popularRegistro(entrada.camposRegistro, tipoCtx.registro());
                    } else if (tipo == TipoLA.REGISTRO || tipo == TipoLA.PONTEIRO) {
                        String baseType = tipoStr;
                        if (baseType.startsWith("^")) baseType = baseType.substring(1);
                        TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaTipo = LASemanticoUtils.obterEntrada(escoposAninhados, baseType);
                        if (entradaTipo != null && entradaTipo.estrutura == EstruturaLA.TIPO) {
                            entrada.camposRegistro = entradaTipo.camposRegistro;
                        }
                    }
                }
            }
        } else if (ctx.getText().startsWith("tipo")) {
            String nomeTipo = ctx.IDENT().getText();
            LAParser.TipoContext tipoCtx = ctx.tipo();
            TipoLA tipo = obterTipoLA(tipoCtx);

            if (existeEmAlgumEscopo(nomeTipo)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + nomeTipo + " ja declarado anteriormente");
            } else {
                escoposAninhados.obterEscopoAtual().adicionar(nomeTipo, tipo, EstruturaLA.TIPO);
                TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = escoposAninhados.obterEscopoAtual().obterEntrada(nomeTipo);
                if (tipoCtx.registro() != null) {
                    popularRegistro(entrada.camposRegistro, tipoCtx.registro());
                }
            }
        } else if (ctx.getText().startsWith("constante")) {
            String nomeConst = ctx.IDENT().getText();
            TipoLA tipo = obterTipoLA(ctx.tipo_basico().getText());
            if (existeEmAlgumEscopo(nomeConst)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + nomeConst + " ja declarado anteriormente");
            } else {
                escoposAninhados.obterEscopoAtual().adicionar(nomeConst, tipo, EstruturaLA.CONSTANTE);
            }
        }
        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String nomeCompleto = ctx.identificador().getText();
        String nomeVar = nomeCompleto.replaceAll("\\[.*?\\]", "");
        
        if (!existeEmAlgumEscopo(nomeVar)) {
            LASemanticoUtils.adicionarErroSemantico(ctx.identificador().start, "identificador " + nomeCompleto + " nao declarado");
        } else {
            TipoLA tipoVariavel = verificarTipoEmAlgumEscopo(nomeVar);
            TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaVar = obterEntrada(nomeVar);
            String tipoCustomVar = entradaVar != null ? entradaVar.tipoCustomizado : "";

            if (ctx.getText().startsWith("^") && tipoCustomVar != null && tipoCustomVar.startsWith("^")) {
                String base = tipoCustomVar.substring(1);
                tipoVariavel = obterTipoLA(base);
            }

            TipoLA tipoExpressao = LASemanticoUtils.verificarTipo(escoposAninhados, ctx.expressao());
            boolean hasError = false;

            if (tipoVariavel == TipoLA.PONTEIRO && !ctx.getText().startsWith("^")) {
                String exprText = ctx.expressao().getText();
                boolean pointerMatch = false;
                if (exprText.startsWith("&")) {
                    String targetVar = exprText.substring(1).replaceAll("\\[.*?\\]", "");
                    TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaTarget = obterEntrada(targetVar);
                    if (entradaTarget != null && tipoCustomVar.equals("^" + entradaTarget.tipoCustomizado)) {
                        pointerMatch = true;
                    }
                } else {
                    String targetVar = exprText.replaceAll("\\[.*?\\]", "");
                    TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaTarget = obterEntrada(targetVar);
                    if (entradaTarget != null && tipoCustomVar.equals(entradaTarget.tipoCustomizado)) {
                        pointerMatch = true;
                    }
                }
                if (!pointerMatch) hasError = true;
            } else if (tipoVariavel == TipoLA.REGISTRO && tipoExpressao == TipoLA.REGISTRO) {
                String exprText = ctx.expressao().getText().replaceAll("\\[.*?\\]", "");
                TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaTarget = obterEntrada(exprText);
                if (entradaTarget != null && !tipoCustomVar.equals(entradaTarget.tipoCustomizado)) {
                    hasError = true;
                }
            } else {
                if (!LASemanticoUtils.tiposCompativeis(tipoVariavel, tipoExpressao)) {
                    hasError = true;
                }
            }

            if (hasError) {
                String textoErro = nomeCompleto;
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
            String nomeCompleto = identCtx.getText();
            String nomeVar = nomeCompleto.replaceAll("\\[.*?\\]", "");
            if (!existeEmAlgumEscopo(nomeVar)) {
                LASemanticoUtils.adicionarErroSemantico(identCtx.start, "identificador " + nomeCompleto + " nao declarado");
            }
        }
        return super.visitCmdLeia(ctx);
    }

    @Override
    public Void visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.identificador() != null) {
            String nomeCompleto = ctx.identificador().getText();
            String nomeVar = nomeCompleto.replaceAll("\\[.*?\\]", "");
            if (!existeEmAlgumEscopo(nomeVar)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().start, "identificador " + nomeCompleto + " nao declarado");
            }
        } else if (ctx.IDENT() != null && ctx.getText().contains("(")) {
            String nomeFunc = ctx.IDENT().getText();
            if (!existeEmAlgumEscopo(nomeFunc)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + nomeFunc + " nao declarado");
            } else {
                TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = obterEntrada(nomeFunc);
                List<LAParser.ExpressaoContext> args = ctx.expressao();
                if (entrada.tiposParametros.size() != args.size()) {
                    LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nomeFunc);
                } else {
                    for (int i = 0; i < args.size(); i++) {
                        TipoLA argTipo = LASemanticoUtils.verificarTipo(escoposAninhados, args.get(i));
                        TipoLA paramTipo = entrada.tiposParametros.get(i);
                        if (!LASemanticoUtils.tiposCompativeisParametros(paramTipo, argTipo)) {
                            LASemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nomeFunc);
                            break;
                        }
                    }
                }
            }
        }
        return super.visitParcela_unario(ctx);
    }

    @Override
    public Void visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.identificador() != null) {
            String nomeCompleto = ctx.identificador().getText();
            String nomeVar = nomeCompleto.replaceAll("\\[.*?\\]", "");
            if (!existeEmAlgumEscopo(nomeVar)) {
                LASemanticoUtils.adicionarErroSemantico(ctx.identificador().start, "identificador " + nomeCompleto + " nao declarado");
            }
        }
        return super.visitParcela_nao_unario(ctx);
    }
}