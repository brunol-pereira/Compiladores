lexer grammar AlgumaLexer;

// Máquina de estados que verifica o comentário corretamente, em que se inicia com o '{' aceita qualquer 
// caracter dentro deste, sem ter quebra de linha e finaliza com '}'.
COMENTARIO       : '{' ~('\n')*? '}' {skip();};

// Palavras chaves.
ALGORITMO        : 'algoritmo';
FIM_ALGORITMO    : 'fim_algoritmo';
DECLARE          : 'declare';
CONSTANTE        : 'constante';
LITERAL          : 'literal';
INTEIRO          : 'inteiro';
REAL             : 'real';
LOGICO           : 'logico';
TRUE             : 'verdadeiro';
FALSE            : 'falso';
AND              : 'e';
OR               : 'ou';
NOT              : 'nao';
IF               : 'se';
THEN             : 'entao';
ELSE             : 'senao';
ENDIF            : 'fim_se';
CASO             : 'caso';
SEJA             : 'seja';
FIM_CASO         : 'fim_caso';
PARA             : 'para';
ATE              : 'ate';
FACA             : 'faca';
FIM_PARA         : 'fim_para';
WHILE            : 'enquanto';
ENDWHILE         : 'fim_enquanto';
TIPO             : 'tipo';
REGISTRO         : 'registro';
FIM_REGISTRO     : 'fim_registro';
PROCEDIMENTO     : 'procedimento';
VAR              : 'var';
FIM_PROCEDIMENTO : 'fim_procedimento';
FUNCAO           : 'funcao';
RETORNE          : 'retorne';
FIM_FUNCAO       : 'fim_funcao';
LEIA             : 'leia';
ESCREVA          : 'escreva';

// Intervalo de valores.
INTERVALO        : '..';

// Operadores Relacionais.
MENOR            : '<';
MENORIGUAL       : '<=';
MAIOR            : '>';
MAIORIGUAL       : '>=';
IGUAL            : '=';
DIFERENTE        : '<>';

// Delimitadores.
DELIM            : ':';
ABREPAR          : '(';
FECHAPAR         : ')';
ABRECHAVE        : '[';
FECHACHAVE       : ']';
VIRGULA          : ',';
ASPAS            : '"';

// Operadores aritméticos.
DIVISAO          : '/';
MOD              : '%';
SOMA             : '+';
SUBTRACAO        : '-';
MULTIPLICACAO    : '*';

// Operadores de manipulação de memória.
ATRIBUICAO       : '<-';
PONTEIRO         : '^';
ENDERECO         : '&';
PONTO            : '.';

// Números.
NUM_INT          : ('0'..'9')+;
NUM_REAL         : ('0'..'9')+ ('.' ('0'..'9')+)?;

// Identificadores.
// Identificadores começam com qualquer letra, maiúscula 
// ou minuscula, seguida de qualquer letra, ou digito, ou '_' 
IDENT            : ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

// Cadeia de string.
// Cadeias de strings são iniciadas por '"', seguidos por quaisquer caracter, sem 
// ser o caracter '\n' e finalizados por '"'.
CADEIA           : '"' ( ~('\n') )*? '"';

// Erro de cadeia de string
// Verifica qualquer cadeia que não foi fechada. Deve vir abaixo da CADEIA
// pois senão pode gerar conflito de nunca encontrar a cadeia.
CADEIA_N_FECHADA : '"' ( ~('\n'|'"') )*? '\n';

// Espaço em branco.
WS               : ( ' ' | '\t' | '\r' | '\n' ) {skip();};

// Erro de comentário não fechado.
COMENT_N_FECHADO : '{' ~('\n'|'}')*? '\n';

// Caso não for identificado nenhuma regra acima, gera um erro.
ERRO             : .;