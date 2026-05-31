package br.ufscar.dc.compiladores.t5;

import org.antlr.v4.runtime.tree.ParseTree;
import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.TipoLA;
import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.EstruturaLA;

public class LAGeradorC extends LAParserBaseVisitor<Void> {
    
    StringBuilder saida;
    Escopos escoposAninhados;

    public LAGeradorC() {
        saida = new StringBuilder();
        escoposAninhados = new Escopos();
    }

    private TipoLA obterTipoLA(String tipoStr) {
        if (tipoStr.equals("inteiro")) return TipoLA.INTEIRO;
        if (tipoStr.equals("real")) return TipoLA.REAL;
        if (tipoStr.equals("literal")) return TipoLA.LITERAL;
        if (tipoStr.equals("logico")) return TipoLA.LOGICO;
        if (tipoStr.startsWith("^")) return TipoLA.PONTEIRO;
        return TipoLA.REGISTRO;
    }

    private void popularRegistro(TabelaDeSimbolos campos, LAParser.RegistroContext ctx) {
        for (LAParser.VariavelContext varCtx : ctx.variavel()) {
            String tipoStr = varCtx.tipo().getText();
            TipoLA tipo = obterTipoLA(tipoStr);
            for (LAParser.IdentificadorContext identCtx : varCtx.identificador()) {
                String nomeVar = identCtx.getText().replaceAll("\\[.*?\\]", "");
                campos.adicionar(nomeVar, tipo, EstruturaLA.VARIAVEL);
                if (varCtx.tipo().registro() != null) {
                    popularRegistro(campos.obterEntrada(nomeVar).camposRegistro, varCtx.tipo().registro());
                }
            }
        }
    }

    class TradutorExpressao extends LAParserBaseVisitor<String> {
        @Override
        public String visitExpressao(LAParser.ExpressaoContext ctx) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ctx.termo_logico().size(); i++) {
                if (i > 0) sb.append(" || ");
                sb.append(visit(ctx.termo_logico(i)));
            }
            return sb.toString();
        }

        @Override
        public String visitTermo_logico(LAParser.Termo_logicoContext ctx) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ctx.fator_logico().size(); i++) {
                if (i > 0) sb.append(" && ");
                sb.append(visit(ctx.fator_logico(i)));
            }
            return sb.toString();
        }

        @Override
        public String visitFator_logico(LAParser.Fator_logicoContext ctx) {
            String prefix = ctx.getText().startsWith("nao") ? "!" : "";
            return prefix + visit(ctx.parcela_logica());
        }

        @Override
        public String visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
            if (ctx.exp_relacional() != null) {
                return visit(ctx.exp_relacional());
            } else if (ctx.getText().equals("verdadeiro")) {
                return "1";
            } else {
                return "0";
            }
        }

        @Override
        public String visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
            if (ctx.exp_aritmetica().size() == 1) {
                return visit(ctx.exp_aritmetica(0));
            } else {
                String left = visit(ctx.exp_aritmetica(0));
                String right = visit(ctx.exp_aritmetica(1));
                String op = ctx.op_relacional().getText();
                if (op.equals("=")) op = "==";
                else if (op.equals("<>")) op = "!=";
                
                TipoLA tipoLeft = LASemanticoUtils.verificarTipo(escoposAninhados, ctx.exp_aritmetica(0));
                if (tipoLeft == TipoLA.LITERAL) {
                    if (op.equals("==")) return "strcmp(" + left + ", " + right + ") == 0";
                    if (op.equals("!=")) return "strcmp(" + left + ", " + right + ") != 0";
                }
                return left + " " + op + " " + right;
            }
        }

        @Override
        public String visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
            StringBuilder sb = new StringBuilder();
            sb.append(visit(ctx.termo(0)));
            for (int i = 0; i < ctx.op1().size(); i++) {
                sb.append(" ").append(ctx.op1(i).getText()).append(" ");
                sb.append(visit(ctx.termo(i + 1)));
            }
            return sb.toString();
        }

        @Override
        public String visitTermo(LAParser.TermoContext ctx) {
            StringBuilder sb = new StringBuilder();
            sb.append(visit(ctx.fator(0)));
            for (int i = 0; i < ctx.op2().size(); i++) {
                sb.append(" ").append(ctx.op2(i).getText()).append(" ");
                sb.append(visit(ctx.fator(i + 1)));
            }
            return sb.toString();
        }

        @Override
        public String visitFator(LAParser.FatorContext ctx) {
            StringBuilder sb = new StringBuilder();
            sb.append(visit(ctx.parcela(0)));
            for (int i = 0; i < ctx.op3().size(); i++) {
                sb.append(" ").append(ctx.op3(i).getText()).append(" ");
                sb.append(visit(ctx.parcela(i + 1)));
            }
            return sb.toString();
        }

        @Override
        public String visitParcela(LAParser.ParcelaContext ctx) {
            if (ctx.parcela_unario() != null) {
                return visit(ctx.parcela_unario());
            } else {
                return visit(ctx.parcela_nao_unario());
            }
        }

        @Override
        public String visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
            if (ctx.IDENT() != null) {
                if (ctx.expressao() != null && !ctx.expressao().isEmpty()) {
                    StringBuilder args = new StringBuilder("(");
                    for (int i = 0; i < ctx.expressao().size(); i++) {
                        if (i > 0) args.append(", ");
                        args.append(visit(ctx.expressao(i)));
                    }
                    args.append(")");
                    return ctx.IDENT().getText() + args.toString();
                }
                return ctx.IDENT().getText();
            } else if (ctx.identificador() != null) {
                return ctx.identificador().getText().replace("^", "*"); 
            } else if (ctx.NUM_INT() != null) {
                return ctx.NUM_INT().getText();
            } else if (ctx.NUM_REAL() != null) {
                return ctx.NUM_REAL().getText();
            } else if (ctx.expressao() != null && !ctx.expressao().isEmpty()) {
                return "(" + visit(ctx.expressao(0)) + ")";
            }
            return ctx.getText();
        }

        @Override
        public String visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
            if (ctx.CADEIA() != null) {
                return ctx.CADEIA().getText();
            } else if (ctx.identificador() != null) {
                return "&" + ctx.identificador().getText(); 
            }
            return ctx.getText();
        }
    }

    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        saida.append("#include <string.h>\n\n");
        
        if (ctx.declaracoes() != null) {
            visit(ctx.declaracoes());
        }
        
        saida.append("int main() {\n");
        
        if (ctx.corpo() != null) {
            visit(ctx.corpo());
        }
        
        saida.append("\n    return 0;\n");
        saida.append("}\n");
        return null;
    }

    @Override
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        String nome = ctx.IDENT().getText();
        if (ctx.getText().startsWith("procedimento")) {
            saida.append("void ").append(nome).append("(");
        } else {
            String tipoRetorno = ctx.tipo_estendido().getText();
            String tipoC = tipoRetorno.equals("inteiro") ? "int" :
                           tipoRetorno.equals("real") ? "float" :
                           tipoRetorno.equals("logico") ? "int" : "char*";
            saida.append(tipoC).append(" ").append(nome).append("(");
        }

        escoposAninhados.criarNovoEscopo();

        if (ctx.parametros() != null) {
            for (int i = 0; i < ctx.parametros().parametro().size(); i++) {
                LAParser.ParametroContext pCtx = ctx.parametros().parametro(i);
                String tipoLA = pCtx.tipo_estendido().getText();
                String tipoC = tipoLA.equals("inteiro") ? "int" :
                               tipoLA.equals("real") ? "float" :
                               tipoLA.equals("logico") ? "int" : "char*";
                TipoLA tipoTabela = obterTipoLA(tipoLA.startsWith("^") ? tipoLA.substring(1) : tipoLA);

                for (int j = 0; j < pCtx.identificador().size(); j++) {
                    if (i > 0 || j > 0) saida.append(", ");
                    if (tipoLA.equals("literal") && !tipoC.contains("*")) saida.append("char* ");
                    else saida.append(tipoC).append(" ");
                    
                    String nomeParam = pCtx.identificador(j).getText();
                    saida.append(nomeParam);
                    escoposAninhados.obterEscopoAtual().adicionar(nomeParam.replaceAll("\\[.*?\\]", ""), tipoTabela, EstruturaLA.VARIAVEL);
                }
            }
        }
        saida.append(") {\n");
        
        for (LAParser.Declaracao_localContext decl : ctx.declaracao_local()) {
            visit(decl);
        }
        for (LAParser.CmdContext cmd : ctx.cmd()) {
            visit(cmd);
        }
        saida.append("}\n\n");
        
        escoposAninhados.abandonarEscopo();
        return null;
    }

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if (ctx.getText().startsWith("tipo")) {
            String nomeTipo = ctx.IDENT().getText();
            saida.append("typedef struct {\n");
            
            escoposAninhados.obterEscopoAtual().adicionar(nomeTipo, TipoLA.REGISTRO, EstruturaLA.TIPO);
            TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaTipo = escoposAninhados.obterEscopoAtual().obterEntrada(nomeTipo);
            if (ctx.tipo().registro() != null) {
                popularRegistro(entradaTipo.camposRegistro, ctx.tipo().registro());
            }

            for (LAParser.VariavelContext varCtx : ctx.tipo().registro().variavel()) {
                String tipoLA = varCtx.tipo().getText();
                String tipoC = tipoLA.equals("inteiro") ? "int" :
                               tipoLA.equals("real") ? "float" :
                               tipoLA.equals("logico") ? "int" : "char";
                for (LAParser.IdentificadorContext idCtx : varCtx.identificador()) {
                    String nomeVar = idCtx.getText();
                    if (tipoLA.equals("literal")) {
                        if (nomeVar.contains("[")) {
                            String base = nomeVar.replaceAll("\\[.*?\\]", "");
                            String dim = nomeVar.substring(nomeVar.indexOf("["), nomeVar.indexOf("]") + 1);
                            saida.append("    char ").append(base).append(dim).append("[80];\n");
                        } else {
                            saida.append("    char ").append(nomeVar).append("[80];\n");
                        }
                    } else {
                        saida.append("    ").append(tipoC).append(" ").append(nomeVar).append(";\n");
                    }
                }
            }
            saida.append("} ").append(nomeTipo).append(";\n");
        } else if (ctx.getText().startsWith("constante")) {
            String nome = ctx.IDENT().getText();
            String tipoLA = ctx.tipo_basico().getText();
            String tipoC = tipoLA.equals("inteiro") ? "int" :
                           tipoLA.equals("real") ? "float" : "char*";
            TipoLA tipoTabela = obterTipoLA(tipoLA);
            escoposAninhados.obterEscopoAtual().adicionar(nome, tipoTabela, EstruturaLA.CONSTANTE);
            
            String val = ctx.valor_constante().getText();
            saida.append("    const ").append(tipoC).append(" ").append(nome).append(" = ").append(val).append(";\n");
        } else if (ctx.getText().startsWith("declare")) {
            for (LAParser.IdentificadorContext idCtx : ctx.variavel().identificador()) {
                String nomeOriginal = idCtx.getText();
                String nomeLimpo = nomeOriginal.replaceAll("\\[.*?\\]", "");
                
                if (ctx.variavel().tipo().registro() != null) {
                    saida.append("    struct {\n");
                    for (LAParser.VariavelContext varCtx : ctx.variavel().tipo().registro().variavel()) {
                        String tipoCampoLA = varCtx.tipo().getText();
                        String tipoCampoC = tipoCampoLA.equals("inteiro") ? "int" :
                                            tipoCampoLA.equals("real") ? "float" :
                                            tipoCampoLA.equals("logico") ? "int" :
                                            tipoCampoLA.equals("literal") ? "char" : tipoCampoLA;
                        for (LAParser.IdentificadorContext idCampoCtx : varCtx.identificador()) {
                            if (tipoCampoLA.equals("literal")) {
                                saida.append("        ").append(tipoCampoC).append(" ").append(idCampoCtx.getText()).append("[80];\n");
                            } else {
                                saida.append("        ").append(tipoCampoC).append(" ").append(idCampoCtx.getText()).append(";\n");
                            }
                        }
                    }
                    saida.append("    } ").append(nomeOriginal).append(";\n");
                    
                    escoposAninhados.obterEscopoAtual().adicionar(nomeLimpo, TipoLA.REGISTRO, EstruturaLA.VARIAVEL);
                    TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = escoposAninhados.obterEscopoAtual().obterEntrada(nomeLimpo);
                    popularRegistro(entrada.camposRegistro, ctx.variavel().tipo().registro());
                    
                } else {
                    String tipoLA = ctx.variavel().tipo().getText();
                    boolean isPonteiro = tipoLA.startsWith("^");
                    String baseType = isPonteiro ? tipoLA.substring(1) : tipoLA;
                    String tipoC = baseType.equals("inteiro") ? "int" :
                                   baseType.equals("real") ? "float" :
                                   baseType.equals("logico") ? "int" :
                                   baseType.equals("literal") ? "char" : baseType;
                                   
                    if (isPonteiro) tipoC += "*";

                    TipoLA tipoTabela = obterTipoLA(baseType);
                    escoposAninhados.obterEscopoAtual().adicionar(nomeLimpo, tipoTabela, EstruturaLA.VARIAVEL);
                    TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = escoposAninhados.obterEscopoAtual().obterEntrada(nomeLimpo);

                    if (tipoTabela == TipoLA.REGISTRO) {
                        TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaOriginal = LASemanticoUtils.obterEntrada(escoposAninhados, baseType);
                        if (entradaOriginal != null && entradaOriginal.estrutura == EstruturaLA.TIPO) {
                            entrada.camposRegistro = entradaOriginal.camposRegistro;
                        }
                    }
                    
                    if (baseType.equals("literal") && !isPonteiro) {
                        if (nomeOriginal.contains("[")) {
                            String dim = nomeOriginal.substring(nomeOriginal.indexOf("["), nomeOriginal.indexOf("]") + 1);
                            saida.append("    char ").append(nomeLimpo).append(dim).append("[80];\n");
                        } else {
                            saida.append("    char ").append(nomeOriginal).append("[80];\n");
                        }
                    } else {
                        saida.append("    ").append(tipoC).append(" ").append(nomeOriginal).append(";\n");
                    }
                }
            }
        }
        return null; 
    }

    @Override
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        for (LAParser.IdentificadorContext idCtx : ctx.identificador()) {
            String nomeOriginal = idCtx.getText();
            String nomeLimpo = nomeOriginal.replaceAll("\\[.*?\\]", "");
            TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = LASemanticoUtils.obterEntrada(escoposAninhados, nomeLimpo);
            
            if (entrada != null) {
                if (entrada.tipo == TipoLA.INTEIRO) {
                    saida.append("    scanf(\"%d\", &").append(nomeOriginal).append(");\n");
                } else if (entrada.tipo == TipoLA.REAL) {
                    saida.append("    scanf(\"%f\", &").append(nomeOriginal).append(");\n");
                } else if (entrada.tipo == TipoLA.LITERAL) {
                    saida.append("    gets(").append(nomeOriginal).append(");\n");
                } else {
                    saida.append("    scanf(\"%d\", &").append(nomeOriginal).append(");\n");
                }
            } else {
                saida.append("    scanf(\"%d\", &").append(nomeOriginal).append(");\n");
            }
        }
        return null;
    }

    @Override
    public Void visitCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        for (LAParser.ExpressaoContext expCtx : ctx.expressao()) {
            String expr = new TradutorExpressao().visit(expCtx);
            TipoLA tipo = LASemanticoUtils.verificarTipo(escoposAninhados, expCtx);

            if (tipo == TipoLA.INTEIRO || tipo == TipoLA.LOGICO) {
                saida.append("    printf(\"%d\", ").append(expr).append(");\n");
            } else if (tipo == TipoLA.REAL) {
                saida.append("    printf(\"%f\", ").append(expr).append(");\n");
            } else if (tipo == TipoLA.LITERAL) {
                saida.append("    printf(\"%s\", ").append(expr).append(");\n");
            } else {
                if (expr.contains(".")) {
                    saida.append("    printf(\"%f\", ").append(expr).append(");\n");
                } else {
                    saida.append("    printf(\"%d\", ").append(expr).append(");\n");
                }
            }
        }
        return null;
    }
    
    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String var = ctx.identificador().getText();
        String varLimpa = var.replaceAll("\\[.*?\\]", "");
        String expr = new TradutorExpressao().visit(ctx.expressao());
        TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = LASemanticoUtils.obterEntrada(escoposAninhados, varLimpa);
        
        if (entrada != null && entrada.tipo == TipoLA.LITERAL) {
            saida.append("    strcpy(").append(var).append(", ").append(expr).append(");\n");
        } else {
            if (ctx.getText().startsWith("^")) {
                saida.append("    *").append(var).append(" = ").append(expr).append(";\n");
            } else {
                saida.append("    ").append(var).append(" = ").append(expr).append(";\n");
            }
        }
        return null;
    }

    @Override
    public Void visitCmdSe(LAParser.CmdSeContext ctx) {
        String expr = new TradutorExpressao().visit(ctx.expressao());
        saida.append("    if (").append(expr).append(") {\n");
        
        for (ParseTree child : ctx.children) {
            if (child.getText().equals("senao")) {
                saida.append("    } else {\n");
            } else if (child instanceof LAParser.CmdContext) {
                visit(child);
            }
        }
        saida.append("    }\n");
        return null;
    }

    @Override
    public Void visitCmdCaso(LAParser.CmdCasoContext ctx) {
        String expr = new TradutorExpressao().visit(ctx.exp_aritmetica());
        saida.append("    switch (").append(expr).append(") {\n");
        
        for (LAParser.Item_selecaoContext item : ctx.selecao().item_selecao()) {
            for (LAParser.Numero_intervaloContext ni : item.constantes().numero_intervalo()) {
                if (ni.getText().contains("..")) {
                    String[] partes = ni.getText().split("\\.\\.");
                    saida.append("        case ").append(partes[0]).append(" ... ").append(partes[1]).append(":\n");
                } else {
                    saida.append("        case ").append(ni.getText()).append(":\n");
                }
            }
            for (LAParser.CmdContext cmd : item.cmd()) {
                visit(cmd);
            }
            saida.append("        break;\n");
        }
        if (ctx.cmd() != null && !ctx.cmd().isEmpty()) {
            saida.append("        default:\n");
            for (LAParser.CmdContext cmd : ctx.cmd()) {
                visit(cmd);
            }
        }
        saida.append("    }\n");
        return null;
    }

    @Override
    public Void visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        String expr = new TradutorExpressao().visit(ctx.expressao());
        saida.append("    while (").append(expr).append(") {\n");
        for (ParseTree child : ctx.children) {
            if (child instanceof LAParser.CmdContext) {
                visit(child);
            }
        }
        saida.append("    }\n");
        return null;
    }

    @Override
    public Void visitCmdPara(LAParser.CmdParaContext ctx) {
        String var = ctx.IDENT().getText();
        String inicio = new TradutorExpressao().visit(ctx.exp_aritmetica(0));
        String fim = new TradutorExpressao().visit(ctx.exp_aritmetica(1));
        
        saida.append("    for (").append(var).append(" = ").append(inicio).append("; ")
             .append(var).append(" <= ").append(fim).append("; ")
             .append(var).append("++) {\n");
             
        for (ParseTree child : ctx.children) {
            if (child instanceof LAParser.CmdContext) {
                visit(child);
            }
        }
        saida.append("    }\n");
        return null;
    }

    @Override
    public Void visitCmdFaca(LAParser.CmdFacaContext ctx) {
        saida.append("    do {\n");
        for (ParseTree child : ctx.children) {
            if (child instanceof LAParser.CmdContext) {
                visit(child);
            }
        }
        String expr = new TradutorExpressao().visit(ctx.expressao());
        saida.append("    } while (").append(expr).append(");\n");
        return null;
    }

    @Override
    public Void visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        saida.append("    ").append(ctx.IDENT().getText()).append("(");
        for (int i = 0; i < ctx.expressao().size(); i++) {
            if (i > 0) saida.append(", ");
            saida.append(new TradutorExpressao().visit(ctx.expressao(i)));
        }
        saida.append(");\n");
        return null;
    }

    @Override
    public Void visitCmdRetorne(LAParser.CmdRetorneContext ctx) {
        saida.append("    return ").append(new TradutorExpressao().visit(ctx.expressao())).append(";\n");
        return null;
    }
}