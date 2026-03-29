lexer grammar LALexer;

@header {
package br.ufscar.dc.compiladores.t1;
}

// Palavras-chave exigidas pela linguagem LA
T_ALGORITMO : 'algoritmo' ;
T_DECLARE   : 'declare' ;
T_FIM_ALGORITMO : 'fim_algoritmo' ;
T_LITERAL   : 'literal' ;
T_INTEIRO   : 'inteiro' ;
T_REAL      : 'real' ;
T_LOGICO    : 'logico' ;
T_TIPO      : 'tipo' ;
T_REGISTRO  : 'registro' ;
T_FIM_REGISTRO : 'fim_registro' ;
T_CONSTANTE : 'constante' ;
T_LEIA      : 'leia' ;
T_ESCREVA   : 'escreva' ;
T_SE        : 'se' ;
T_ENTAO     : 'entao' ;
T_SENAO     : 'senao' ;
T_FIM_SE    : 'fim_se' ;
T_CASO      : 'caso' ;
T_SEJA      : 'seja' ;
T_FIM_CASO  : 'fim_caso' ;
T_PARA      : 'para' ;
T_ATE       : 'ate' ;
T_FACA      : 'faca' ;
T_FIM_PARA  : 'fim_para' ;
T_ENQUANTO  : 'enquanto' ;
T_FIM_ENQUANTO : 'fim_enquanto' ;
T_PROCEDIMENTO : 'procedimento' ;
T_FIM_PROCEDIMENTO : 'fim_procedimento' ;
T_FUNCAO    : 'funcao' ;
T_FIM_FUNCAO : 'fim_funcao' ;
T_VERDADEIRO : 'verdadeiro' ;
T_FALSO     : 'falso' ;
T_NAO       : 'nao' ;
T_E         : 'e' ;
T_OU        : 'ou' ;
T_RETORNE   : 'retorne' ;

// Símbolos e Operadores
T_MAIS : '+' ;
T_MENOS : '-' ;
T_MULT : '*' ;
T_DIV : '/' ;
T_ATRIB : '<-' ;
T_IGUAL : '=' ;
T_DIF : '<>' ;
T_MENOR_IGUAL : '<=' ;
T_MAIOR_IGUAL : '>=' ;
T_MENOR : '<' ;
T_MAIOR : '>' ;
T_ABRE_PAR : '(' ;
T_FECHA_PAR : ')' ;
T_ABRE_COL : '[' ;
T_FECHA_COL : ']' ;
T_VIRGULA : ',' ;
T_DOIS_PONTOS : ':' ;
T_PONTO : '.' ;
T_PONTO_PONTO : '..' ;
T_CIRCUNFLEXO : '^' ;
T_E_COMERCIAL : '&' ;

// Regras Complexas e Tratamento de Erros
IDENT : [a-zA-Z_] [a-zA-Z0-9_]* ;
CADEIA : '"' ~[\r\n]*? '"' ;
ERRO_CADEIA : '"' ~[\r\n]* ;

// Comentários válidos e erros de fechamento
COMENTARIO : '{' ~[\r\n]*? '}' -> skip ;
ERRO_COMENTARIO : '{' ~[\r\n]* ;

// Ignorar espaços em branco
WS : [ \t\r\n]+ -> skip ;

// Erro léxico genérico (Símbolo não identificado)
ERRO_SIMBOLO : . ;