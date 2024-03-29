grammar LA;

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



        

programa
    : declaracoes 'algoritmo' corpo 'fim_algoritmo'
    ;
    
declaracoes
    : decl_local_global*
    ;
    
decl_local_global
    : declaracao_local 
    | declaracao_global
    ;
    
declaracao_local
    : 'declare' variavel 
    | 'constante' IDENT ':' tipo_basico '=' valor_constante 
    | 'tipo' IDENT ':' tipo
    ;
    
variavel
    : identificador (',' identificador)* ':' tipo
    ;
    
identificador
    : IDENT ('.' IDENT)* dimensao
    ;
    
dimensao
    : ('[' exp_aritmetica ']')*
    ;
    
tipo
    : registro 
    | tipo_estendido
    ;
    
tipo_basico
    : 'literal' 
    | 'inteiro' 
    | 'real' 
    | 'logico'
    ;
    
tipo_basico_ident
    : tipo_basico 
    | IDENT
    ;
    
tipo_estendido
    : '^'? tipo_basico_ident
    ;
    
valor_constante
    : CADEIA 
    | NUM_INT 
    | NUM_REAL 
    | 'verdadeiro' 
    | 'falso'
    ;
    
registro
    : 'registro' variavel* 'fim_registro'
    ;

parametro
    : 'var'? identificador (',' identificador)* ':' tipo_estendido
    ;

parametros
    : parametro (',' parametro)*
    ;

declaracao_global
    : 'procedimento' IDENT '(' parametros? ')' declaracao_local* cmd* 'fim_procedimento' 
    | 'funcao' IDENT '(' parametros? ')' ':' tipo_estendido declaracao_local* cmd* 'fim_funcao'
    ;
    
corpo
    : declaracao_local* cmd*
    ;
    
cmd
    : cmdLeia 
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
    
cmdLeia
    : 'leia' '(' '^'? identificador (',' '^'? identificador)* ')'
    ;
    
cmdEscreva
    : 'escreva' '(' expressao (',' expressao)* ')'
    ;
    
cmdSe
    : 'se' expressao 'entao' cmdIf+=cmd* ('senao' cmdElse+=cmd*)? 'fim_se'
    ;
    
cmdCaso
    : 'caso' exp_aritmetica 'seja' selecao ('senao' cmd*)? 'fim_caso'
    ;
    
cmdPara
    : 'para' IDENT '<-' inicioExp=exp_aritmetica 'ate' fimExp=exp_aritmetica 'faca' cmd* 'fim_para'
    ;
    
cmdEnquanto
    : 'enquanto' expressao 'faca' cmd* 'fim_enquanto'
    ;
    
cmdFaca
    : 'faca' cmd* 'ate' expressao
    ;
    
cmdAtribuicao
    : '^'? identificador '<-' expressao
    ;
    
cmdChamada
    : IDENT '(' expressao (',' expressao)* ')'
    ;
    
cmdRetorne
    : 'retorne' expressao
    ;
    
selecao
    : item_selecao*
    ;
    
item_selecao
    : constantes ':' cmd*
    ;
    
constantes
    : numero_intervalo (',' numero_intervalo)*
    ;
    
numero_intervalo
    : op_inicio=op_unario? inicio=NUM_INT ('..' op_fim=op_unario? fim=NUM_INT)?
    ;
    
op_unario
    : '-'
    ;
    
exp_aritmetica
    : termo (op1 termo)*
    ;
    
termo
    : fator (op2 fator)*
    ;
    
fator
    : parcela (op3 parcela)*
    ;
    
op1
    : '+' | '-'
    ;
    
op2
    : '*' | '/'
    ;
    
op3
    : '%'
    ;
    
parcela
    : op_unario? parcela_unario | parcela_nao_unario
    ;
    
parcela_unario
    : '^'? identificador
	| IDENT '(' expressao (',' expressao)* ')'
	| NUM_INT
	| NUM_REAL
	| '(' expressao ')'
    ;
    
parcela_nao_unario
    : '&' identificador | CADEIA
    ;
    
exp_relacional
    : exp_aritmetica (op_relacional exp_aritmetica)?
    ;
    
op_relacional
    : '=' | '<>' | '>=' | '<=' | '>' | '<'
    ;
    
expressao
    : termo_logico (op_logico_1 termo_logico)*
    ;
    
termo_logico
    : fator_logico (op_logico_2 fator_logico)*
    ;
    
fator_logico
    : 'nao'? parcela_logica
    ;
    
parcela_logica
    : ( 'verdadeiro' | 'falso' ) | exp_relacional
    ;
    
op_logico_1
    : 'ou'
    ;
    
op_logico_2
    : 'e'
    ;