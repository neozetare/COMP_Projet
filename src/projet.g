// Grammaire du langage PROJET
// COMP L3
// Anne Grazon, Veronique Masson
// il convient d'y inserer les appels a {PtGen.pt(k);}
// relancer Antlr apres chaque modification et raffraichir le projet Eclipse le cas echeant

// attention l'analyse est poursuivie apres erreur si l'on supprime la clause rulecatch

grammar projet;

options {
  language=Java; k=1;
 }

@header {
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileInputStream;
}


// partie syntaxique :  description de la grammaire //
// les non-terminaux doivent commencer par une minuscule


@members {


// variables globales et methodes utiles a placer ici

}
// la directive rulecatch permet d'interrompre l'analyse a la premiere erreur de syntaxe
@rulecatch {
catch (RecognitionException e) {reportError (e) ; throw e ; }}


unite  :   unitprog {PtGen.pt(10);} EOF
      |    unitmodule  EOF
  ;

unitprog
  : 'programme' ident ':'
     declarations
     corps { System.out.println("succes, arret de la compilation "); }
  ;

unitmodule
  : 'module' ident ':'
     declarations
  ;

declarations
  : partiedef? partieref? consts? vars? decprocs?
  ;

partiedef
  : 'def' ident  (',' ident )* ptvg
  ;

partieref: 'ref'  specif (',' specif)* ptvg
  ;

specif  : ident  ( 'fixe' '(' type  ( ',' type  )* ')' )?
                 ( 'mod'  '(' type  ( ',' type  )* ')' )?
  ;

consts  : 'const' ( ident  '=' valeur  ptvg {PtGen.pt(310);} )+
  ;

vars  : 'var' ( type ident ( ',' {PtGen.pt(340);}  ident  )* ptvg {PtGen.pt(340);} )+ {PtGen.pt(341);}
  ;

type  : 'ent' {PtGen.pt(370);}
  |     'bool' {PtGen.pt(380);}
  ;

decprocs: (decproc ptvg)+
  ;

decproc :  'proc'  ident  parfixe? parmod? consts? vars? corps
  ;

ptvg  : ';'
  |
  ;

corps : 'debut' instructions 'fin'
  ;

parfixe: 'fixe' '(' pf ( ';' pf)* ')'
  ;

pf  : type ident  ( ',' ident  )*
  ;

parmod  : 'mod' '(' pm ( ';' pm)* ')'
  ;

pm  : type ident  ( ',' ident  )*
  ;

instructions
  : instruction ( ';' instruction)*
  ;

instruction
  : inssi
  | inscond
  | boucle
  | lecture
  | ecriture
  | affouappel
  |
  ;

inssi : 'si' expression {PtGen.pt(800);} 'alors'  instructions ('sinon' {PtGen.pt(801);} instructions)? 'fsi' {PtGen.pt(802);}
  ;

inscond : 'cond'  expression  ':' instructions
          (','  expression  ':' instructions )*
          ('aut'  instructions |  )
          'fcond'
  ;

boucle  : 'ttq' {PtGen.pt(890);} expression {PtGen.pt(891);} 'faire' instructions 'fait' {PtGen.pt(892);}
  ;

lecture: 'lire' '(' ident {PtGen.pt(920);} ( ',' ident {PtGen.pt(920);} )* ')'
  ;

ecriture: 'ecrire' '(' expression {PtGen.pt(950);} ( ',' expression {PtGen.pt(950);} )* ')'
   ;

affouappel
  : ident  ( {PtGen.pt(990);} ':=' expression {PtGen.pt(991);}
            |   (effixes (effmods)?)?
           )
  ;

effixes : '(' (expression  (',' expression  )*)? ')'
  ;

effmods :'(' (ident  (',' ident  )*)? ')'
  ;

expression: (exp1) ('ou' {PtGen.pt(1110);} exp1 {PtGen.pt(1110); PtGen.pt(1111);} )*
  ;

exp1  : exp2 ('et' {PtGen.pt(1110);} exp2 {PtGen.pt(1110); PtGen.pt(1131);} )*
  ;

exp2  : 'non' exp2 {PtGen.pt(1160);}
  | exp3
  ;

exp3  : exp4
  ( '='  {PtGen.pt(1200);} exp4 {PtGen.pt(1200); PtGen.pt(1211); PtGen.pt(1201);}
  | '<>' {PtGen.pt(1200);} exp4 {PtGen.pt(1200); PtGen.pt(1221); PtGen.pt(1201);}
  | '>'  {PtGen.pt(1200);} exp4 {PtGen.pt(1200); PtGen.pt(1231); PtGen.pt(1201);}
  | '>=' {PtGen.pt(1200);} exp4 {PtGen.pt(1200); PtGen.pt(1241); PtGen.pt(1201);}
  | '<'  {PtGen.pt(1200);} exp4 {PtGen.pt(1200); PtGen.pt(1251); PtGen.pt(1201);}
  | '<=' {PtGen.pt(1200);} exp4 {PtGen.pt(1200); PtGen.pt(1261); PtGen.pt(1201);}
  ) ?
  ;

exp4  : exp5
        ('+' {PtGen.pt(1200);} exp5 {PtGen.pt(1200); PtGen.pt(1310);}
        |'-' {PtGen.pt(1200);} exp5 {PtGen.pt(1200); PtGen.pt(1320);}
        )*
  ;

exp5  : primaire
        (    '*'  {PtGen.pt(1200);} primaire {PtGen.pt(1200); PtGen.pt(1370);}
          | 'div' {PtGen.pt(1200);} primaire {PtGen.pt(1200); PtGen.pt(1380);}
        )*
  ;

primaire: valeur {PtGen.pt(1420);}
  | ident {PtGen.pt(1430);}
  | '(' expression ')'
  ;

valeur  : nbentier {PtGen.pt(1470);}
  | '+' nbentier {PtGen.pt(1470);}
  | '-' nbentier {PtGen.pt(1490);}
  | 'vrai' {PtGen.pt(1500);}
  | 'faux' {PtGen.pt(1510);}
  ;

// partie lexicale  : cette partie ne doit pas etre modifie  //
// les unites lexicales de ANTLR doivent commencer par une majuscule
// attention : ANTLR n'autorise pas certains traitements sur les unites lexicales,
// il est alors ncessaire de passer par un non-terminal intermediaire
// exemple : pour l'unit lexicale INT, le non-terminal nbentier a du etre introduit


nbentier  :   INT { UtilLex.valNb = Integer.parseInt($INT.text);}; // mise a jour de valNb

ident : ID  { UtilLex.traiterId($ID.text); } ; // mise a jour de numId
     // tous les identificateurs seront places dans la table des identificateurs, y compris le nom du programme ou module
     // la table des symboles n'est pas geree au niveau lexical


ID  :   ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;

// zone purement lexicale //

INT   :   '0'..'9'+ ;
WS    :   (' '|'\t' |'\r')+ {skip();} ; // definition des "espaces"
LIGNE :   '\n' {UtilLex.incrementeLigne();skip();};


COMMENT
  :  '\{' (.)* '\}' {skip();}   // toute suite de caracteres entouree d'accolades est un commentaire
  |  '#' ~( '\r' | '\n' )* {skip();}  // tout ce qui suit un caractere diese sur une ligne est un commentaire
  ;

// commentaires sur plusieurs lignes
ML_COMMENT    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;




