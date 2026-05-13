package br.ufscar.dc.compiladores.t4;

import java.util.LinkedList;
import java.util.List;
import br.ufscar.dc.compiladores.t4.TabelaDeSimbolos.TipoLA;

public class Escopos {
    private LinkedList<TabelaDeSimbolos> pilhaDeTabelas;
    
    private TipoLA tipoRetornoEscopoAtual = TipoLA.INVALIDO;

    public Escopos() {
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo(); // Cria o escopo global
    }

    public void criarNovoEscopo() {
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }

    public TabelaDeSimbolos obterEscopoAtual() {
        return pilhaDeTabelas.peek();
    }

    public List<TabelaDeSimbolos> percorrerEscoposAninhados() {
        return pilhaDeTabelas;
    }

    public void abandonarEscopo() {
        pilhaDeTabelas.pop();
    }

    public TipoLA getTipoRetornoEscopoAtual() {
        return tipoRetornoEscopoAtual;
    }

    public void setTipoRetornoEscopoAtual(TipoLA tipoRetornoEscopoAtual) {
        this.tipoRetornoEscopoAtual = tipoRetornoEscopoAtual;
    }
}