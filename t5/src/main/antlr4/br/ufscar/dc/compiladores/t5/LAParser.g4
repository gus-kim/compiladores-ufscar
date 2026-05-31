parser grammar LAParser;

// Importa os tokens do T1
options {
    tokenVocab=LALexer;
}

// =========================================================================
// REGRA INICIAL
// =========================================================================
programa : declaracoes ALGORITMO corpo FIM_ALGORITMO EOF ;

// =========================================================================
// DECLARAÇÕES
// =========================================================================
declaracoes : decl_local_global* ;

decl_local_global : declaracao_local | declaracao_global ;

declaracao_local : DECLARE variavel
                 | CONSTANTE IDENT DOIS_PONTOS tipo_basico IGUAL valor_constante
                 | TIPO IDENT DOIS_PONTOS tipo 
                 ;

variavel : identificador (VIRGULA identificador)* DOIS_PONTOS tipo ;

identificador : IDENT (PONTO IDENT)* dimensao ;

dimensao : (ABRE_COL exp_aritmetica FECHA_COL)* ;

tipo : registro | tipo_estendido ;

tipo_basico : LITERAL_KW | INTEIRO_KW | REAL_KW | LOGICO_KW ;

tipo_basico_ident : tipo_basico | IDENT ;

tipo_estendido : CIRCUNFLEXO? tipo_basico_ident ;

valor_constante : CADEIA | NUM_INT | NUM_REAL | VERDADEIRO | FALSO ;

registro : REGISTRO variavel* FIM_REGISTRO ;

declaracao_global : PROCEDIMENTO IDENT ABRE_PAR parametros? FECHA_PAR declaracao_local* cmd* FIM_PROCEDIMENTO
                  | FUNCAO IDENT ABRE_PAR parametros? FECHA_PAR DOIS_PONTOS tipo_estendido declaracao_local* cmd* FIM_FUNCAO 
                  ;

parametro : VAR? identificador (VIRGULA identificador)* DOIS_PONTOS tipo_estendido ;

parametros : parametro (VIRGULA parametro)* ;

// =========================================================================
// CORPO E COMANDOS
// =========================================================================
corpo : declaracao_local* cmd* ;

cmd : cmdLeia 
    | cmdEscreva 
    | cmdSe 
    | cmdCaso 
    | cmdPara 
    | cmdEnquanto 
    | cmdFaca 
    | cmdAtribuicao 
    | cmdChamada 
    | cmdRetorne 
    ;

cmdLeia : LEIA ABRE_PAR CIRCUNFLEXO? identificador (VIRGULA CIRCUNFLEXO? identificador)* FECHA_PAR ;

cmdEscreva : ESCREVA ABRE_PAR expressao (VIRGULA expressao)* FECHA_PAR ;

cmdSe : SE expressao ENTAO cmd* (SENAO cmd*)? FIM_SE ;

cmdCaso : CASO exp_aritmetica SEJA selecao (SENAO cmd*)? FIM_CASO ;

cmdPara : PARA IDENT ATRIBUICAO exp_aritmetica ATE exp_aritmetica FACA cmd* FIM_PARA ;

cmdEnquanto : ENQUANTO expressao FACA cmd* FIM_ENQUANTO ;

cmdFaca : FACA cmd* ATE expressao ;

cmdAtribuicao : CIRCUNFLEXO? identificador ATRIBUICAO expressao ;

cmdChamada : IDENT ABRE_PAR expressao (VIRGULA expressao)* FECHA_PAR ;

cmdRetorne : RETORNE expressao ;

selecao : item_selecao* ;

item_selecao : constantes DOIS_PONTOS cmd* ;

constantes : numero_intervalo (VIRGULA numero_intervalo)* ;

numero_intervalo : op_unario? NUM_INT (PONTO_PONTO op_unario? NUM_INT)? ;

op_unario : MENOS | MAIS ;

// =========================================================================
// EXPRESSÕES
// =========================================================================
exp_aritmetica : termo (op1 termo)* ;

termo : fator (op2 fator)* ;

fator : parcela (op3 parcela)* ;

op1 : MAIS | MENOS ;

op2 : VEZES | DIVISAO ;

op3 : PORCENTAGEM ;

parcela : op_unario? parcela_unario | parcela_nao_unario ;

parcela_unario : CIRCUNFLEXO? identificador 
               | IDENT ABRE_PAR expressao (VIRGULA expressao)* FECHA_PAR 
               | NUM_INT 
               | NUM_REAL 
               | ABRE_PAR expressao FECHA_PAR 
               ;

parcela_nao_unario : ENDERECO identificador | CADEIA ;

exp_relacional : exp_aritmetica (op_relacional exp_aritmetica)? ;

op_relacional : IGUAL | DIFERENTE | MAIOR_IGUAL | MENOR_IGUAL | MAIOR | MENOR ;

expressao : termo_logico (op_logico_1 termo_logico)* ;

termo_logico : fator_logico (op_logico_2 fator_logico)* ;

fator_logico : NAO? parcela_logica ;

parcela_logica : VERDADEIRO | FALSO | exp_relacional ;

op_logico_1 : OU ;

op_logico_2 : E ;