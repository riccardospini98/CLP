grammar SimpLanPlus ;

prog   : exp #singleExpProg
       | (dec)+ (stm)* (exp)? #regularProg
       ;

dec    : type ID ';' #varDec
       | type ID '(' ( param ( ',' param)* )? ')' '{' body '}' #funDec
       ;

param  : type ID
       ;

body   : (dec)* (stm)* (exp)?
	   ;

type   : 'int'
       | 'bool'
       | 'void'
       ;

stm    : ID '=' exp ';' #assign
       | ID '(' (exp (',' exp)* )? ')' ';' #funCallVoid
       | 'if' '(' exp ')' '{' (stm)+ '}' ('else' '{' (stm)+ '}')? #ifStm
	   ;

exp    : INTEGER #intVal | ('true' | 'false') #boolVal
       | ID #var
       | '!' exp #not
       | left=exp (mul='*' | div='/') right=exp #mulDivExp
       | left=exp (sum='+' | sub='-') right=exp #sumSubExp
       | left=exp (gt='>' | lt='<' | gte='>=' | lte='<=' | eq='==') right=exp #comparisonExp
       | left=exp (and='&&' | or='||') right=exp #boolExp
       | 'if' '(' guard=exp ')' '{' ifBranch  '}' 'else' '{' elseBranch '}' #ifThenElse
       | '(' exp ')' #baseExp
       | ID '(' (exp (',' exp)* )? ')' #funCallReturn
       ;
       ifBranch: (stm)* exp ;
       elseBranch: (stm)* exp;
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

//Numbers
fragment DIGIT  : '0'..'9';
INTEGER         : DIGIT+;

//IDs
fragment CHAR   : 'a'..'z' |'A'..'Z' ;
ID              : CHAR (CHAR | DIGIT)* ;

//ESCAPE SEQUENCES
WS              : (' '|'\t'|'\n'|'\r')-> skip;
LINECOMENTS     : '//' (~('\n'|'\r'))* -> skip;
BLOCKCOMENTS    : '/*'( ~('/'|'*')|'/'~'*'|'*'~'/'|BLOCKCOMENTS)* '*/' -> skip;

ERR             : .  -> channel(HIDDEN);