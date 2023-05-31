lexer grammar AlgumaLexer;

/* -------------------- PALAVRAS CHAVE ------------*/

ALGORITMO        
        : 'algoritmo';
FIM_ALGORITMO
        : 'fim_algoritmo';
DECLARE
        : 'declare';
CONSTANTE
        : 'constante';

LOGICO  
        : 'logico';
TRUE  
        : 'verdadeiro';
FALSE  
        : 'falso';

LITERAL
        : 'literal';
INTEIRO    
        : 'inteiro';
REAL
        : 'real';

AND              
        : 'e';
OR               
        : 'ou';
NOT              
        : 'nao';

IF               
        : 'se';
ENDIF            
        : 'fim_se';
THEN             
        : 'entao';
ELSE             
        : 'senao';
CASO             
        : 'caso';
FIM_CASO         
        : 'fim_caso';
SEJA             
        : 'seja';
PARA             
        : 'para';
FIM_PARA         
        : 'fim_para';
ATE              
        : 'ate';
FACA             
        : 'faca';
WHILE            
        : 'enquanto';
ENDWHILE         
        : 'fim_enquanto';
        
REGISTRO         
        : 'registro';
FIM_REGISTRO     
        : 'fim_registro';
PROCEDIMENTO     
        : 'procedimento';
FIM_PROCEDIMENTO 
        : 'fim_procedimento';
FUNCAO           
        : 'funcao';
FIM_FUNCAO       
        : 'fim_funcao';
RETORNE          
        : 'retorne';
ESCREVA          
        : 'escreva';

TIPO             
        : 'tipo';
VAR              
        : 'var';
LEIA             
        : 'leia';

/* -------------------- DELIMITADORES ------------*/
DELIM            
        : ':';
ABREPAR          
        : '(';
FECHAPAR         
        : ')';
ABRECHAVE        
        : '[';
FECHACHAVE       
        : ']';
VIRGULA          
        : ',';
ASPAS            
        : '"';

/* -------------------- OPERADORES RELACIONAIS ------------*/
MENOR            
        : '<';
MENORIGUAL       
        : '<=';
MAIOR            
        : '>';
MAIORIGUAL       
        : '>=';
IGUAL            
        : '=';
DIFERENTE        
        : '<>';


/* -------------------- INTERVALO DE VALORES ------------*/
INTERVALO        
        : '..';

/* -------------------- OPERADORES ARITMÉTICOS ------------*/
SOMA             
        : '+';
SUBTRACAO        
        : '-';
MULTIPLICACAO    
        : '*';
DIVISAO          
        : '/';
MOD              
        : '%';

/* -------------------- MANIPULÇÃO DE MEMÓRIA ------------*/
ATRIBUICAO       
        : '<-';
PONTEIRO         
        : '^';
ENDERECO         
        : '&';
PONTO            
        : '.';

/* -------------------- NÚMEROS ------------*/
NUM_INT          
        : ('0'..'9')+;
NUM_REAL         
        : ('0'..'9')+ ('.' ('0'..'9')+)?;

/* -------------------- IDENTIFICADORES ------------*/
// LETRA(maiuscula ou minuscula) + LETRA OU NÚMERO
IDENT            
        : ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

/* -------------------- CADEIA DE STRING ------------*/
// Cadeias de string = "..."
CADEIA           
        : '"' ( ~('\n') )*? '"';

// Cadeia de string não fechada
CADEIA_NAO_FECHADA 
        : '"' ( ~('\n'|'"') )*? '\n';

// Espaço em branco.
WS               
        : ( ' ' | '\t' | '\r' | '\n' ) {skip();};

/* -------------------- COMENTARIOS ------------*/
//Identificar comentário Ex: {ALGUMA COISA}
COMENTARIO       
        : '{' ~('\n')*? '}' {skip();};

// Comentário não fechado.
COMENTARIO_NAO_FECHADO 
        : '{' ~('\n'|'}')*? '\n';


// Gera um erro caso não identificar nenhum caso acima
ERRO             
        : .;