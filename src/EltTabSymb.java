// classe de definition d'un element d'une table des symboles
// 4 champs pour chaque identificateur
import java.io.*;

	public class EltTabSymb {
		public int code, categorie, type, info;

		public EltTabSymb(int code, int categorie, int type, int info) {
			this.code = code;
			this.categorie = categorie;
			this.type = type;
			this.info = info;
		}
	
		public String toString() {
			final String[] chcat = { "", "CONSTANTE      ", "VARGLOBALE     ",
					"VARLOCALE      ", "PARAMFIXE      ", "PARAMMOD       ",
					"PROC           ", "DEF            ", "REF            ",
					"PRIVEE         " };
			final String[] chtype = { "", "ENT     ", "BOOL    ", "NEUTRE  " };
			String ch = "";
			if (code == -1)
				ch += "-1";
			else
				ch += "@" + UtilLex.repId(code);
			while (ch.length() < 15)
				ch += ' ';
			return ch + chcat[categorie] + chtype[type] + info;
		} // toString
	} // EltTabSymb

