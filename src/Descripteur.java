import java.io.InputStream;
import java.io.OutputStream;

// classe necessaire a la compilation separee d'un programme et de modules

public class Descripteur {
		
	
	// EltDef : type de chaque element de la table des points d'entree tabDef
	//
	class EltDef {
		public String nomProc;
		public int adPo, nbParam;

		public EltDef(String nomProc, int adPo, int nbParam) {
			this.nomProc = nomProc;
			this.adPo = adPo;
			this.nbParam = nbParam;
		}
	}

	// EltRef : type de chaque element de la table des references externes tabRef
    //
	class EltRef {
		public String nomProc;
		public int nbParam;

		public EltRef(String nomProc, int nbParam) {
			this.nomProc = nomProc;
			this.nbParam = nbParam;
		}
	}

	// Def des 8 champs d'un descripteur d'une unite compilee
	//
	private String unite;
	private int tailleCode, tailleGlobaux, 
	nbDef, nbRef, nbTransExt;

	private static final int MAXREF = 10, MAXDEF = 10;
	private EltDef[] tabDef = new EltDef[MAXDEF + 1];
	private EltRef[] tabRef = new EltRef[MAXREF + 1];

	// constructeur
	public Descripteur() {
		nbDef = 0; nbRef = 0; nbTransExt = 0;
		for (int i = 0; i <= MAXDEF; i++)
			tabDef[i] = new EltDef("inconnu", -2, -2);
		for (int i = 0; i <= MAXREF; i++)
			tabRef[i] = new EltRef("inconnu", -2);
	}
	
	// Methodes d'acces aux attributs unite, tailleCode, tailleGlobaux
	//
	
	public void setUnite(String s) {
		unite = s;
	}
	
	public String getUnite() {
		return unite;
	}
	
	public void setTailleCode (int n) {
		tailleCode = n;
	}

	public int getTailleCode() {
		return tailleCode;
	}

	public void setTailleGlobaux (int n) {
		tailleGlobaux = n;
	}

	public int getTailleGlobaux() {
		return tailleGlobaux;
	}
	// Methodes d'acces aux attributs nbDef, nbRef, nbTransExt
	//
	public int getNbDef() {
		return nbDef;
	}
	
	public int getNbRef() {
		return nbRef;
	}
	
	public void incrNbTansExt() {
		nbTransExt += 1;
	}
	
	public int getNbTransExt() {
		return nbTransExt;
	}
	
	// Methodes d'acces au tableau tabDef
	// ----------------------------------
	
	// presentDef: recherche le nom d'une procedure dans tabDef
	//		rend son indice si present, 0 sinon
	public  int presentDef(String idLu) { 
		int i = nbDef;
		while (i > 0 && !tabDef[i].nomProc.equals(idLu))
			i--;
		return i;
	}

	// ajoutDef: ajoute une procedure de nom IdLu dans tabDef
	public void ajoutDef(String idLu) { 
		if (nbDef == Descripteur.MAXDEF)
			UtilLex.messErr("trop de points d'entr�e");
		nbDef += 1;
		tabDef[nbDef] = new EltDef(idLu, -1, -1);
	}

	public String getDefNomProc(int i) {
		return tabDef[i].nomProc;
	}

	public void modifDefNbParam(int i, int nb) {
		tabDef[i].nbParam = nb;
	}
	
	public int getDefNbParam(int i) {
		return tabDef[i].nbParam;
	}

	public void modifDefAdPo(int i, int ad) {
		tabDef[i].adPo = ad;
	}
	
	public int getDefAdPo(int i) {
		return tabDef[i].adPo;
	}
	
	// Methodes d'acces au tableau tabRef
	// ----------------------------------
	
	// presentRef: recherche le nom d'une procedure dans tabRef
	//		rend son indice si present, 0 sinon	
	public int presentRef(String idLu) { 
		int i = nbRef;
		while (i > 0 && !tabRef[i].nomProc.equals(idLu))
			i--;
		return i;
	}

	// ajoutRef: ajoute une procedure de nom IdLu dans tabRef
	public void ajoutRef(String idLu) { 
		if (nbRef == Descripteur.MAXREF)
			UtilLex.messErr("trop de ref�rences externes");
		nbRef += 1;
		tabRef[nbRef] = new EltRef(idLu, -1);
	}

	public String getRefNomProc(int i) {
		return tabRef[i].nomProc;
	}

	public void modifRefNbParam(int i, int nb) {
		tabRef[i].nbParam = nb;
	}
	
	public int getRefNbParam(int i) {
		return tabRef[i].nbParam;
	}
	
	// pour affichage d'un descripteur
	//
	public String toString() {
		String s = "unite          " + unite + "\n" 
				+ "tailleCode     " + tailleCode + "\n" 
				+ "tailleGlobaux  " + tailleGlobaux + "\n"
				+ "nbDef          " + nbDef + "\n" 
				+ "nbRef          " + nbRef + "\n" 
				+ "nbTransExt     " + nbTransExt + "\n"
				+ "tabDef         " + " \n";
		for (int i = 1; i <= nbDef; i++)
			s = s 	+ "    " 
					+ tabDef[i].nomProc + "  " + tabDef[i].adPo + "  "
					+ tabDef[i].nbParam + "\n";
		s = s + "tabRef         " + " \n";
		for (int i = 1; i <= nbRef; i++)
			s = s 	+ "    " + tabRef[i].nomProc + "  " 
					+ tabRef[i].nbParam + "\n";
		return s;
	}
	
	// Ecriture du descripteur dans un fichier .desc
	//
	public void ecrireDesc(String nomFichier) {
		OutputStream f = Ecriture.ouvrir(nomFichier + ".desc");
		if (f == null) {
			System.out
					.println("creation de " + nomFichier + ".desc impossible");
			System.exit(1);
		}
		Ecriture.ecrireStringln(f, "FICHIER " + nomFichier + ".desc :");
		Ecriture.ecrireStringln(f, "");
		Ecriture.ecrireString(f, "" + this);
		Ecriture.fermer(f);
	}

	// Initialisation d'un descripteur par lecture d'un fichier .desc
	//
	public void lireDesc(String nomFichier) {
		InputStream f = Lecture.ouvrir(nomFichier + ".desc");
		String nomProc;
		int adPo, nbParam;
		if (f == null) {
			System.out.println("fichier " + nomFichier + ".desc inexistant");
			System.exit(1);
		}
		Lecture.lireString(f);
		Lecture.lireString(f);
		Lecture.lireUnite(f, false);
		unite = Lecture.lireUnite(f, true);
		Lecture.lireUnite(f, false);
		tailleCode = Lecture.lireIntln(f);
		Lecture.lireUnite(f, false);
		tailleGlobaux = Lecture.lireIntln(f);
		Lecture.lireUnite(f, false);
		nbDef = Lecture.lireIntln(f);
		Lecture.lireUnite(f, false);
		nbRef = Lecture.lireIntln(f);
		Lecture.lireUnite(f, false);
		nbTransExt = Lecture.lireIntln(f);
		Lecture.lireString(f);
		for (int i = 1; i <= nbDef; i++) {
			nomProc = Lecture.lireUnite(f, false);
			adPo = Lecture.lireInt(f);
			nbParam = Lecture.lireIntln(f);
			tabDef[i] = new EltDef(nomProc, adPo, nbParam);
		}
		Lecture.lireString(f);
		for (int i = 1; i <= nbRef; i++) {
			nomProc = Lecture.lireUnite(f, false);
			nbParam = Lecture.lireIntln(f);
			tabRef[i] = new EltRef(nomProc, nbParam);
		}
		Lecture.fermer(f);
	}

}