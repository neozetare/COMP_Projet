import java.io.*;
import java.util.Arrays;


public class Edl {
	
	// nombre max de modules, taille max d'un code objet d'une unite
	static final int MAXMOD = 5, MAXOBJ = 1000;
	// nombres max de references externes (REF) et de points d'entree (DEF)
	// pour une unite
	private static final int MAXREF = 10, MAXDEF = 10;
	
	// typologie des erreurs
	private static final int FATALE = 0, NONFATALE = 1;
	
	// valeurs possibles du vecteur de translation
	private static final int TRANSDON=1,TRANSCODE=2,REFEXT=3;
	
	// table de tous les descripteurs concernes par l'edl
	static Descripteur[] tabDesc = new Descripteur[MAXMOD + 1];
	
	// declarations de variables A COMPLETER SI BESOIN
	static int ipo, nMod, nbErr;
	static String nomProg;
	
	static String[] nomsUnites = new String[MAXMOD + 1];
	
	static int[] transDon;
	static int[] transCode;
	
	static Descripteur.EltDef[] dicoDef = new Descripteur.EltDef[(MAXMOD + 1) * MAXDEF];

	static int[][] adFinale = new int[MAXMOD + 1][MAXREF + 1];
	
	static int nbDef = 0;

	// utilitaire de traitement des erreurs
	// ------------------------------------
	static void erreur(int te, String m) {
		System.out.println(m);
		if (te == FATALE) {
			System.out.println("ABANDON DE L'EDITION DE LIENS");
			System.exit(1);
		}
		nbErr = nbErr + 1;
	}

	// utilitaire de remplissage de la table des descripteurs tabDesc
	// --------------------------------------------------------------
	static void lireDescripteurs() {
		String s;
		System.out.println("les noms doivent etre fournis sans suffixe");
		System.out.print("nom du programme : ");
		s = Lecture.lireString();
		tabDesc[0] = new Descripteur();
		tabDesc[0].lireDesc(s);
		if (!tabDesc[0].getUnite().equals("programme"))
			erreur(FATALE, "programme attendu");
		nomProg = s;

		nMod = 0;
		nomsUnites[nMod] = s;
		while (!s.equals("") && nMod < MAXMOD) {
			System.out.print("nom de module " + (nMod + 1)
					+ " (RC si termine) ");
			s = Lecture.lireString();
			if (!s.equals("")) {
				nMod = nMod + 1;
				nomsUnites[nMod] = s;
				tabDesc[nMod] = new Descripteur();
				tabDesc[nMod].lireDesc(s);
				if (!tabDesc[nMod].getUnite().equals("module"))
					erreur(FATALE, "module attendu");
			}
		}
	}
	
	static void basesDeDecalage() {
		transDon = new int[nMod + 1];
		transCode = new int[nMod + 1];
		
		transDon[0] = 0; transCode[0] = 0;
		for (int i = 1; i <= nMod; i++) {
			transDon[i] = transDon[i-1] + tabDesc[i-1].getTailleGlobaux();
			transCode[i] = transCode[i-1] + tabDesc[i-1].getTailleCode();
		}
	}
	
	static void dicoDeDefs() {
		for (int i = 1; i <= nMod; i++) {
			if (tabDesc[i].getNbDef() > MAXDEF)
				erreur(FATALE, nomsUnites[i] + " : " + MAXDEF + " def maximum (" + tabDesc[i].getNbDef() + " trouvées)");

			boolean presentDef;
			for (int j = 1; j <= tabDesc[i].getNbDef(); j++) {
				presentDef = false;
				
				for (int k = 1; k <= nbDef; k++) {
					if (tabDesc[i].getDefNomProc(j).equals(dicoDef[k].nomProc))
						presentDef = true;
				}
				
				if (presentDef)
					erreur(FATALE, nomsUnites[i] + " : def \"" + tabDesc[i].getDefNomProc(j) + "\" déjà définie)");
				
				dicoDef[++nbDef] = tabDesc[i].new EltDef(
					tabDesc[i].getDefNomProc(j),
					transCode[i] + tabDesc[i].getDefAdPo(j),
					tabDesc[i].getDefNbParam(j)
				);
			}
		}
	}
	
	static void adressesFinales() {
		for (int i = 0; i <= nMod; i++) {
			for (int j = 1; j <= tabDesc[i].getNbRef(); j++) {
				adFinale[i][j] = -1;
				
				for (int k = 1; k <= nbDef; k++) {
					if (tabDesc[i].getRefNomProc(j).equals(dicoDef[k].nomProc)) {
						adFinale[i][j] = dicoDef[k].adPo;
						break;
					}
				}
				
				if (adFinale[i][j] == -1) {
					erreur(FATALE, nomsUnites[i] + " : def référencée \"" + tabDesc[i].getRefNomProc(j) + "\" inexistante)");
				}
			}
		}
	}
	
	static void printDebug() {
		System.out.println();
		
		System.out.println("transDon");
		System.out.println(Arrays.toString(transDon));
		System.out.println();

		System.out.println("transCode");
		System.out.println(Arrays.toString(transCode));
		System.out.println();

		System.out.println("dicoDef");
		for (int i = 1; i <= nbDef; i++)
			System.out.println(i + " [" + dicoDef[i].nomProc + ", " + dicoDef[i].adPo + ", " + dicoDef[i].nbParam + "]");
		System.out.println();
		
		System.out.println("adFinale");
		for (int i = 0; i <= nMod; i++) {
			System.out.print(i + " [");
			for (int j = 1; j <= tabDesc[i].getNbRef(); j++) {
				System.out.print(adFinale[i][j]);
				if (j != tabDesc[i].getNbRef())
					System.out.print(", ");
			}
			System.out.println(']');
		}
		System.out.println();
	}
	
	static void constMap() {
		// f2 = fichier ex�cutable .map construit
		OutputStream f2 = Ecriture.ouvrir(nomProg + ".map");
		if (f2 == null)
			erreur(FATALE, "création du fichier " + nomProg + ".map impossible");
		// pour construire le code concat�n� de toutes les unit�s
		int[] po = new int[(nMod + 1) * MAXOBJ];

		// Pour chaque descripteur
		for (int i = 0; i <= nMod; i++) {
			InputStream f = Lecture.ouvrir(nomsUnites[i] + ".obj");
			if (f == null)
				erreur(FATALE, "fichier " + nomsUnites[i] + ".obj inexistant");

			VTrans[] vTrans = new VTrans[MAXOBJ];
			int nbVTrans = 0;

			// Récupération des vecteurs dans les descripteurs
			for (int j = 0; j < tabDesc[i].getNbTransExt(); j++)
				vTrans[nbVTrans++] = new VTrans(Lecture.lireInt(f), Lecture.lireInt(f));

			// Concaténation du programme objet source dans le po final
			for (int j = 1; j <= tabDesc[i].getTailleCode(); j++) {
				po[j + transCode[i]] = Lecture.lireInt(f);
				ipo++;
			}

			// Translations de vecteurs
			for (int k = 0; k < nbVTrans; k++) {
				switch (vTrans[k].code) {
					case TRANSCODE:
						po[vTrans[k].adPo + transCode[i]] += transCode[i];
						break;
					case TRANSDON:
						po[vTrans[k].adPo + transCode[i]] += transDon[i];
						break;
					case REFEXT:
						po[vTrans[k].adPo + transCode[i]] = adFinale[i][po[vTrans[k].adPo + transCode[i]]];
						break;
					default:
						erreur(FATALE, nomsUnites[i] + " : code " + vTrans[k].code + " inattendu (vecteur n°" + k + ": " + vTrans[k].adPo + ' ' + vTrans[k].code + ")");
						break;
				}
			}

			Lecture.fermer(f);
		}
		
		// S'il y a un RESERVER, on met à jour son argument
		if (po[1] == 1)
			po[2] = transDon[nMod] + tabDesc[nMod].getTailleGlobaux();
		
		Ecriture.fermer(f2);
		// création du fichier en mnémonique correspondant
		Mnemo.creerFichier(ipo, po, nomProg + ".ima");

		// création du fichier map correspondant
		OutputStream f = Ecriture.ouvrir(nomProg + ".map");
		if (f == null)
			erreur(FATALE, "impossible de creer " + nomProg + ".map");
		for (int i = 1; i <= ipo; i++)
			Ecriture.ecrireStringln(f, "" + po[i]);
		Ecriture.fermer(f);
	}

	public static void main(String argv[]) {
		System.out.println("EDITEUR DE LIENS / PROJET LICENCE");
		System.out.println("---------------------------------");
		System.out.println("");
		nbErr = 0;
		
		// Phase 1 de l'edition de liens
		// -----------------------------
		// Lecture des descripteurs
		lireDescripteurs();

		// Bases de décalage
		basesDeDecalage();
		
		// Dictionnaire de defs
		dicoDeDefs();
		
		// Adresses finales
		adressesFinales();

		// printDebug();
		
		if (nbErr > 0) {
			System.out.println("programme exécutable non produit");
			System.exit(1);
		}
		
		// Phase 2 de l'edition de liens
		// -----------------------------
		constMap();
		System.out.println("Edition de liens terminee");
	}
}

class VTrans {
	public int adPo, code;

	public VTrans(int adPo, int code) {
		this.adPo = adPo;
		this.code = code;
	}
}
