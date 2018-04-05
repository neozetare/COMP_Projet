import java.io.*;
import java.util.HashMap;

public class Edl {

	// nombre max de modules, taille max d'un code objet d'une unité
	static final int MAXMOD = 5, MAXOBJ = 1000;
	// nombres max de références externes (REF) et de points d'entrée (DEF)
	// pour une unité
	private static final int MAXREF = 10, MAXDEF = 10;

	// typologie des erreurs
	private static final int FATALE = 0, NONFATALE = 1;

	// valeurs possibles du vecteur de translation
	private static final int TRANSDON = 1, TRANSCODE = 2, REFEXT = 3;

	// table de tous les descripteurs concernes par l'edl
	static Descripteur[] tabDesc = new Descripteur[MAXMOD + 1];

	// declarations de variables A COMPLETER SI BESOIN
	static int ipo, nMod, nbErr;
	static String nomProg;
	static int tailletabDesc;
	static int tailleDicoDef;

	// Variables personnelles
	static int[] transDon = new int[6];
	static int[] transCode = new int[6];
	static EltDicoDef[] dicoDef = new EltDicoDef[MAXMOD + 1 * MAXDEF];
	static int[][] adFinale = new int[MAXMOD + 1][MAXDEF];

	// Tableau contenant les noms des unites utilisees pour l'edition de lien
	static String[] tabObj = new String[MAXMOD + 1];

	// Classe reprenant la classe EltDef dans Descripteur
	// Permet de remplir le dicoDef prenant pour chaque lignes un nomProc, adPo
	// et un nbParam
	public static class EltDicoDef {
		public String nomProc;
		public int adPo, nbParam;

		public EltDicoDef(String nomProc, int adPo, int nbParam) {
			this.nomProc = nomProc;
			this.adPo = adPo;
			this.nbParam = nbParam;
		}
	}

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
			erreur(NONFATALE, "programme attendu");
		nomProg = s;
		tabObj[nMod] = s;

		nMod = 0;
		tailletabDesc++;

		if (tabDesc[0].getNbDef() != 0) {
			ajoutDicoDef(tabDesc[0]);
		}

		while (!s.equals("") && nMod < MAXMOD) {
			System.out.print("nom de module " + (nMod + 1) + " (RC si termine) ");
			s = Lecture.lireString();
			if (!s.equals("")) {
				Descripteur descTmp = new Descripteur();
				descTmp.lireDesc(s);

				// On vérifie que le module donne n'as pas deja ete declare
				for (int i = 0; i < tailletabDesc; i++) {
					if (compareTab(descTmp, tabDesc[i])) {
						erreur(NONFATALE, "Module deja fourni");
					}
				}

				nMod = nMod + 1;
				tabObj[nMod] = s;
				tabDesc[nMod] = new Descripteur();
				tabDesc[nMod].lireDesc(s);
				tailletabDesc++;

				if (!tabDesc[nMod].getUnite().equals("module")) {
					erreur(NONFATALE, "module attendu");
				}

				// transDon[nMod] est egale a la somme de la valeur d'avant et
				// du nombre de parametre globaux
				transDon[nMod] = transDon[nMod - 1] + tabDesc[nMod - 1].getTailleGlobaux();
				
				// transCode[nMod] est egale a la somme de la valeur d'avant et
				// de la taille du code du module precedent
				transCode[nMod] = transCode[nMod - 1] + tabDesc[nMod - 1].getTailleCode();

				
				if (tabDesc[nMod].getNbDef() != 0) {
					ajoutDicoDef(tabDesc[nMod]);
				}
			}
		}
	}

	// Permet d'ajouter une tabDef de module dans le dicoDef
	private static void ajoutDicoDef(Descripteur desc) {
		
		for (int i = 1; i <= desc.getNbDef(); i++) {
			
			if (tailleDicoDef < dicoDef.length) {
				
				if (!existeDico(desc.getDefNomProc(i))) {
					
					dicoDef[tailleDicoDef] = new EltDicoDef(desc.getDefNomProc(i), desc.getDefAdPo(i), desc.getDefNbParam(i));
					
					// On ajoute adPo à la valeur transCode associee a l'unite
					dicoDef[tailleDicoDef].adPo += transCode[nMod];
					tailleDicoDef++;
					
				} else {
					erreur(NONFATALE, "Double definition d'une procedure");
				}
				
			} else {
				erreur(NONFATALE, "Dépassement taille dicoDef");
			}
		}
	}

	// Verifie si il existe une double definition de procedure
	// Rend true si il y'a une double definition
	private static boolean existeDico(String nom) {
		for (int j = 0; j < tailleDicoDef; j++) {
			if (dicoDef[j].nomProc.equals(nom)) {
				return true;
			}
		}
		return false;
	}

	// Permet de comparer deux descripteurs
	// Rend true si les deux descripteurs sont les memes
	private static boolean compareTab(Descripteur desc1, Descripteur desc2) {
		boolean a = false;

		if ((desc1.getUnite().compareTo(desc2.getUnite()) == 0) && desc1.getTailleCode() == desc2.getTailleCode()
				&& desc1.getTailleGlobaux() == desc2.getTailleGlobaux() && desc1.getNbDef() == desc2.getNbDef()
				&& desc1.getNbRef() == desc2.getNbRef() && desc1.getNbTransExt() == desc2.getNbTransExt()) {
			a = true;
		}
		if (a) {
			for (int i = 1; i <= desc1.getNbDef(); i++) {
				String tmp = desc2.getDefNomProc(i);
				if (desc1.presentDef(tmp) == 0) {
					return false;
				}
			}
			for (int j = 1; j <= desc1.getNbRef(); j++) {
				String tmp = desc2.getRefNomProc(j);
				if (desc1.presentRef(tmp) == 0) {
					return false;
				}
			}
		}
		return a;
	}

	// Fonction permettant la creation de la table adFinale
	private static void createAdFinale() {
		if (verifRefDef()) {
			for (int i = 0; i <= nMod; i++) {
				if (tabDesc[i].getNbRef() != 0) {
					for (int j = 1; j <= tabDesc[i].getNbRef(); j++) {
						adFinale[i][j] = getDicoDefAdPo(tabDesc[i].getRefNomProc(j));
					}
				}
			}
		} else {
			erreur(NONFATALE, "Nombre de REF different du nombre de DEF");
		}
	}

	// Retourne l'adresse adPo dans le dicoDef de la procedure passee en
	// parametre
	// -1 sinon
	private static int getDicoDefAdPo(String nomProcRef) {
		for (int k = 0; k < tailleDicoDef; k++) {
			if (dicoDef[k].nomProc.equals(nomProcRef)) {
				return dicoDef[k].adPo;
			}
		}
		erreur(NONFATALE, "Reference \"" + nomProcRef + "\" introuvable dans le dicoDef");
		return -1;
	}

	// Verifie que le nombre de REF total differents est le meme que le nombre
	// de DEF total
	private static boolean verifRefDef() {
		int nbDefTotal = 0;
		int nbRefTotal = 0;
		for (int i = 0; i <= nMod; i++) {
			nbDefTotal += tabDesc[i].getNbDef();
			nbRefTotal += tabDesc[i].getNbRef();
		}

		for (int j = 0; j <= nMod; j++) {
			nbRefTotal = nbRefTotal - appartientRef(tabDesc[j].getRefNomProc(j));
		}

		return (nbDefTotal == nbRefTotal);
	}

	// Compte le nombre de repetion de la meme reference dans differents modules
	// afin de ne pas les compter plusieurs fois
	private static int appartientRef(String nom) {
		int res = 0;
		if (nMod >= 1) {
			for (int i = 1; i <= nMod; i++) {
				if (tabDesc[i].presentRef(nom) != 0) {
					res++;
				}
			}
		}
		return res;
	}

	// Affichage des differentes tables
	private static void printDicoDef() {
		System.out.println("---------DicoDef--------");
		System.out.println("i  Proc adPo nbParam");
		for (int i = 0; i < tailleDicoDef; i++) {
			System.out.println(i + "  " + dicoDef[i].nomProc + "  " + dicoDef[i].adPo + "  " + dicoDef[i].nbParam);
		}
	}

	private static void printTransDon() {
		System.out.println("----------transDon---------");
		for (int i = 0; i < transDon.length; i++) {
			System.out.println(transDon[i]);
		}
	}

	private static void printTransCode() {
		System.out.println("---------transCode-------");
		for (int i = 0; i < tailleDicoDef; i++) {
			System.out.println(transCode[i]);
		}
	}

	private static void printAdFinale() {
		for (int i = 0; i <= nMod; i++) {
			System.out.print("[" + i + "]" + " | ");

			if (tabDesc[i].getNbRef() == 0) {
				System.out.print("Aucune references");
			}

			for (int j = 1; j <= tabDesc[i].getNbRef(); j++) {
				System.out.print("[" + adFinale[i][j] + "] ");
			}

			System.out.println("");
		}
	}

	// Phase 2 ====>
	static void constMap() {

		// f2 = fichier exécutable .map construit
		OutputStream f2 = Ecriture.ouvrir(nomProg + ".map");
		if (f2 == null)
			erreur(NONFATALE, "création du fichier " + nomProg + ".map impossible");
		// pour construire le code concaténé de toutes les unités
		int[] po = new int[(nMod + 1) * MAXOBJ + 1];

		// Pour chaque unite
		for (int i = 0; i <= nMod; i++) {

			InputStream obj = Lecture.ouvrir(tabObj[i] + ".obj");

			if (obj == null) {
				erreur(FATALE, "Erreur ouverture fichier " + "\"" + tabObj[i] + ".obj\"");
			}

			int poAdr = 0;
			int transType = 0;

			HashMap<Integer, Integer> transitions = new HashMap<Integer, Integer>();

			// On remplit une map avec les transitions en tete du fichier .obj
			// courant
			for (int j = 0; j < tabDesc[i].getNbTransExt(); j++) {
				poAdr = Lecture.lireInt(obj) + transCode[i];
				transType = Lecture.lireIntln(obj);
				transitions.put(poAdr, transType);
			}

			// Variable de type Integer car get(ipo) retourn un type Integer
			Integer transi = 0;
			int adrRef = 1;
			int instructionFinProg = tabDesc[i].getTailleCode();

			if (i == nMod) {
				instructionFinProg = tabDesc[i].getTailleCode() - 1;
			}

			// Pour chaque lignes du .obj courant 
			for (int k = 1; k <= instructionFinProg; k++) {

				po[ipo] = Lecture.lireIntln(obj);
				
				// On verifie si c'est une partie de la table de translation ou du code lui meme
				transi = transitions.get(ipo);

				if (transi != null) {

					// Selon la translation utilisee
					switch (transi) {

					case TRANSDON:
						po[ipo] += transDon[i];
						break;

					case TRANSCODE:
						po[ipo] += transCode[i];
						break;

					case REFEXT:
						po[ipo] = adFinale[i][adrRef];
						adrRef++;
						break;
					}
				}

				ipo++;
			}

			// On ferme l'inputStream du fichier obj
			Lecture.fermer(obj);
		}

		// On met a jour le nombre a reserver a la ligne po = 2
		po[2] = transDon[nMod] + tabDesc[nMod].getTailleGlobaux();

		// Permet de remplir le fichier .map avec le tableau po
		for (int i = 1; i <= ipo; i++) {
			Ecriture.ecrireStringln(f2, po[i] + "");
		}

		Ecriture.fermer(f2);
		// création du fichier en mnémonique correspondant
		Mnemo.creerFichier(ipo, po, nomProg + ".ima");
	}

	public static void main(String argv[]) {
		System.out.println("EDITEUR DE LIENS / PROJET LICENCE");
		System.out.println("---------------------------------");
		System.out.println("Par LE MASLE Alexis et HYSAJ Elgeta");
		System.out.println();

		nbErr = 0;
		tailletabDesc = 0;
		tailleDicoDef = 0;
		transCode[0] = 0;
		transDon[0] = 0;
		ipo = 1;

		// Phase 1 de l'édition de liens
		// -----------------------------
		lireDescripteurs();
		createAdFinale();

		/*
		 * Lignes a decommenter pour afficher la table DicoDef, la table des
		 * TransCode, la table des TransDon et la table adFinale
		 */
		printDicoDef();
		printTransCode();
		printTransDon();
		printAdFinale();

		if (nbErr > 0) {
			System.out.println("programme exécutable non produit");
			System.exit(1);
		}

		// Phase 2 de l'édition de liens
		// -----------------------------
		constMap();
		System.out.println("Edition de liens terminee");
	}
}
