programme T1p:	{compilation separee}

	ref initi, opplus fixe (ent,ent) mod (ent), opnon fixe (bool) mod (bool)
	
	const n=3;
	var ent i, valent, tmpent;
		bool valbool;
	
	proc init
	debut
		valent := 0;
		valbool := faux;
		initi;
	fin;
	
	proc disp fixe (ent n)
	debut
		ecrire(faux, faux, faux);
		ecrire(n,valent,valbool);
		ecrire(vrai, vrai, vrai);
	fin;

debut
	init;
	i := 0;
	disp(i);
	ttq i <> n faire
		lire(tmpent);
		opplus(valent,tmpent)(valent);
		opnon(valbool)(valbool);
		disp(i);
		i := i + 1;
	fait;
fin
