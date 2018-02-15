unite  :   unitprog  EOF
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

consts  : 'const' ( ident  '=' valeur  ptvg  )+
  ;

vars  : 'var' ( type ident ( ','  ident  )* ptvg  )+
  ;

type  : 'ent'
  |     'bool'
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

inssi : 'si' expression 'alors' instructions ('sinon'  instructions)? 'fsi'
  ;

inscond : 'cond'  expression  ':' instructions
          (','  expression  ':' instructions )*
          ('aut'  instructions |  )
          'fcond'
  ;

boucle  : 'ttq'  expression 'faire' instructions 'fait'
  ;

lecture: 'lire' '(' ident  ( ',' ident  )* ')'
  ;

ecriture: 'ecrire' '(' expression  ( ',' expression  )* ')'
   ;

affouappel
  : ident  (    ':=' expression
            |   (effixes (effmods)?)?
           )
  ;

effixes : '(' (expression  (',' expression  )*)? ')'
  ;

effmods :'(' (ident  (',' ident  )*)? ')'
  ;

expression: (exp1) ('ou'  exp1  )*
  ;

exp1  : exp2 ('et'  exp2  )*
  ;

exp2  : 'non' exp2
  | exp3
  ;

exp3  : exp4
  ( '='   exp4
  | '<>'  exp4
  | '>'   exp4
  | '>='  exp4
  | '<'   exp4
  | '<='  exp4
  ) ?
  ;

exp4  : exp5
        ('+'  exp5
        |'-'  exp5
        )*
  ;

exp5  : primaire
        (    '*'   primaire
          | 'div'  primaire
        )*
  ;

primaire: valeur
  | ident
  | '(' expression ')'
  ;

valeur  : nbentier
  | '+' nbentier
  | '-' nbentier
  | 'vrai'
  | 'faux'
  ;