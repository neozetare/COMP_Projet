module T1m1:	{compilation separee}
	
	def initi, opplus, opmoins, opet, opou, opnon;
	var ent i;
	
	proc initi
	debut
		i := 0;
	fin;

	proc opplus fixe (ent x,y) mod (ent r)
	debut
		r := x + y;
		i := i + 1
	fin;

	proc opmoins fixe (ent x,y) mod (ent r)
	debut
		r := x - y;
		i := i + 1
	fin;

	proc opet fixe (bool x,y) mod (bool r)
	debut
		r := x et y;
		i := i + 1
	fin;

	proc opnon fixe (bool x) mod (bool r)
	debut
		r := non x;
		i := i + 1
	fin;