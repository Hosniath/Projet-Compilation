package generated.fr.ul.miage.expression; 
import java_cup.runtime.Symbol;
%%
/* options */
%line
%public
%cupsym Sym
%cup
%{
void erreur(){ System.out.println("Caractère inattendu"); System.exit(1);
}
%}
/* macros */
SEP = [ \t\r\n]
NUM = [0-9]+
ALPHANUM =[a-zA-Z][a-zA-Z0-9]*
COM = [$].*\n

%%
/* règles */
"+" { return new Symbol(Sym.ADD);}
 "*" { return new Symbol(Sym.MUL);} 
 "/" {return new Symbol(Sym.DIV);}
 "-" {return new Symbol(Sym.MOINS);}
 "(" { return new Symbol(Sym.PO);}
")" { return new Symbol(Sym.PF);}
"{" {return new Symbol(Sym.AO);}
"}" {return new Symbol(Sym.AF);}
"=" {return new Symbol(Sym.ASSIGN);}
"==" { return new Symbol(Sym.EGAL);}
">" { return new Symbol(Sym.SUP);}
"<" { return new Symbol(Sym.INF);}
"<=" { return new Symbol(Sym.INFEGAL);}
">=" { return new Symbol(Sym.SUPEGAL);}
"!=" {return new Symbol(Sym.DIFF);}
";"  {return new Symbol(Sym.PV);}
","  {return new Symbol(Sym.Vi);}
"int" {return new Symbol(Sym.INT);}
"if"  {return new Symbol(Sym.SI);}
"then"  {return new Symbol(Sym.ALORS);}
"otherwise" {return new Symbol(Sym.SINON);}
"while" {return new Symbol(Sym.TANTQUE);}
"do"  {return new Symbol(Sym.FAIT);}
"endloop" {return new Symbol(Sym.FIN);}
"function" {return new Symbol(Sym.FONCTION);}
"write" {return new Symbol(Sym.ECRIRE);}
"read" {return new Symbol(Sym.LIRE);}
"glob" {return new Symbol(Sym.GLOBAL);}
"loc"   {return new Symbol(Sym.LOCAL);}
"call"  {return new Symbol(Sym.APPEL);}
"return" {return new Symbol(Sym.RETOUR);}
"void"   {return new Symbol(Sym.VOID);}
"none"  {return new Symbol(Sym.RIEN);}
{NUM} { return new Symbol(Sym.NUM, Integer.valueOf(yytext()));} 
{ALPHANUM} {return new Symbol(Sym.IDF, String.valueOf(yytext()));}
{SEP} {;}
{COM}	{;}
<<EOF>> { return new Symbol(Sym.EOF);} 
. {erreur();}