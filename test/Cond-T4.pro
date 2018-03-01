programme exempcond:

var bool b1, b2;
debut
	lire(b1,b2);
	ecrire(vrai);
	cond
		b1: ecrire(1),
		b2: ecrire(2)
		aut ecrire(3)
	fcond ;
	ecrire(faux);
fin
