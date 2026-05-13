package br.ufscar.dc.compiladores.t4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {
    
    public enum TipoLA {
        INTEIRO, REAL, LITERAL, LOGICO, REGISTRO, PONTEIRO, INVALIDO, ENDERECO
    }

    public enum EstruturaLA {
        VARIAVEL, CONSTANTE, TIPO, PROCEDIMENTO, FUNCAO
    }

    public class EntradaTabelaDeSimbolos {
        public String nome;
        public TipoLA tipo;
        public EstruturaLA estrutura;
        
        public TabelaDeSimbolos camposRegistro; 
        
        public ArrayList<TipoLA> tiposParametros; 
        
        public String tipoCustomizado; 

        private EntradaTabelaDeSimbolos(String nome, TipoLA tipo, EstruturaLA estrutura) {
            this.nome = nome;
            this.tipo = tipo;
            this.estrutura = estrutura;
            this.tiposParametros = new ArrayList<>();
            this.camposRegistro = new TabelaDeSimbolos();
            this.tipoCustomizado = "";
        }
    }

    private final Map<String, EntradaTabelaDeSimbolos> tabela;

    public TabelaDeSimbolos() {
        this.tabela = new HashMap<>();
    }

    public void adicionar(String nome, TipoLA tipo, EstruturaLA estrutura) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipo, estrutura));
    }

    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }

    public TipoLA verificar(String nome) {
        return tabela.get(nome).tipo;
    }

    public EntradaTabelaDeSimbolos obterEntrada(String nome) {
        return tabela.get(nome);
    }
}