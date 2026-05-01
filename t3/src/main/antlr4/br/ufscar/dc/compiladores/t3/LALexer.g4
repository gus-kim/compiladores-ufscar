lexer grammar LALexer;

// =============================================================================
// OPERADORES E DELIMITADORES
// Definição dos símbolos gráficos da linguagem LA.
// =============================================================================


// Delimitadores de estrutura e pontuação
PONTO_VIRGULA : ';' ;
VIRGULA       : ',' ;
DOIS_PONTOS   : ':' ;
PONTO_PONTO   : '..' ;
PONTO         : '.' ;
ABRE_PAR      : '(' ;
FECHA_PAR     : ')' ;
ABRE_COL      : '[' ;
FECHA_COL     : ']' ;

// Operadores Matemáticos e Lógicos
MAIS          : '+' ;
MENOS         : '-' ;
VEZES         : '*' ;
DIVISAO       : '/' ;
CIRCUNFLEXO   : '^' ;
PORCENTAGEM   : '%' ;
IGUAL         : '=' ;

// Operadores Relacionais e Atribuição
ATRIBUICAO    : '<-' ;
MENOR_IGUAL   : '<=' ;
MAIOR_IGUAL   : '>=' ;
DIFERENTE     : '<>' ;
MENOR         : '<' ;
MAIOR         : '>' ;

// Outros símbolos
ENDERECO      : '&' ;

// =============================================================================
// PALAVRAS-RESERVADAS (KEYWORDS)
// Termos fixos da sintaxe da linguagem.
// =============================================================================


// Estrutura Principal e Declarações
ALGORITMO         : 'algoritmo' ;
FIM_ALGORITMO     : 'fim_algoritmo' ;
DECLARE           : 'declare' ;
CONSTANTE         : 'constante' ;
VAR               : 'var' ;
TIPO              : 'tipo' ;
REGISTRO          : 'registro' ;
FIM_REGISTRO      : 'fim_registro' ;

// Tipos Primitivos
LITERAL_KW        : 'literal' ;
INTEIRO_KW        : 'inteiro' ;
REAL_KW           : 'real' ;
LOGICO_KW         : 'logico' ;

// Sub-rotinas (Procedimentos e Funções)
PROCEDIMENTO      : 'procedimento' ;
FIM_PROCEDIMENTO  : 'fim_procedimento' ;
FUNCAO            : 'funcao' ;
FIM_FUNCAO        : 'fim_funcao' ;
RETORNE           : 'retorne' ;

// Controle de Fluxo e Repetição
SE                : 'se' ;
ENTAO             : 'entao' ;
SENAO             : 'senao' ;
FIM_SE            : 'fim_se' ;
CASO              : 'caso' ;
SEJA              : 'seja' ;
FIM_CASO          : 'fim_caso' ;
PARA              : 'para' ;
ATE               : 'ate' ;
FACA              : 'faca' ;
FIM_PARA          : 'fim_para' ;
ENQUANTO          : 'enquanto' ;
FIM_ENQUANTO      : 'fim_enquanto' ;

// Entrada e Saída
LEIA              : 'leia' ;
ESCREVA           : 'escreva' ;

// Operadores Lógicos Literais e Booleanos
VERDADEIRO        : 'verdadeiro' ;
FALSO             : 'falso' ;
NAO               : 'nao' ;
E                 : 'e' ;
OU                : 'ou' ;

// =============================================================================
// TOKENS DINÂMICOS E LITERAIS
// Regras para identificadores, números e strings.
// =============================================================================


// Identificadores: Devem começar com letra ou underscore
IDENT : [a-zA-Z_] [a-zA-Z0-9_]* ;

// Valores numéricos (Inteiros e Reais)
NUM_INT  : [0-9]+ ;
NUM_REAL : [0-9]+ '.' [0-9]+ ;

// Cadeia de caracteres (Strings entre aspas duplas)
CADEIA : '"' ( ~["\r\n] )* '"' ;

// =============================================================================
// REGRAS DE DESCARTE E TRATAMENTO DE ERROS
// =============================================================================


// 1. Comentário correto (deve vir antes do erro)
COMENTARIO : '{' ~[}\r\n]* '}' -> skip ;

// 2. Erro de comentário (se chegou aqui, é porque não achou o '}')
ERRO_COMENTARIO : '{' ~[}\r\n]* ;

// 3. Cadeia correta
// (Já definida acima no seu código como CADEIA)

// 4. Erro de cadeia
ERRO_CADEIA : '"' ~["\r\n]* ;

// 5. Espaços
WS : [ \t\r\n]+ -> skip ;

// 6. Qualquer outra coisa é símbolo inválido
ERRO_SIMBOLO : . ;