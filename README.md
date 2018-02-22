# COMP_Projet
Projet de COMP - L3 INFO g1.2 - MARTIN\_RAZA\_PHAM

---

## Fichiers du projet

### Fichiers sources (/src)
* **projet.java** : Fichier .java principal à exécuter pour compiler
* **PtGen.java** : Points de génération
* **projet.g** : Grammaire
* **Mapile.java** : Fichier .java pour exécuter un code mapile

### Librairies (/lib)
* **libclass** : Classes Ecriture, Lecture, UtilLex et ExecMapile
* **antlr** : Jar antlr

### Jeux de tests (/)
#### Jeux des profs
* **DeclExp-T\*.pro** : Jeux valides de déclaration et d'expressions
* **Err\*-DeclExp.pro** : Jeux erronnés de déclaration et d'expressions
* **polyP\*.pro** : Jeux du poly
* **TDexo\*.pro** : Jeux des TD
#### Jeux persos
* **Bases-T\*.pro** : Jeux valides d'instructions de bases

---

## Notes

### Génération avec ANTLR
A chaque modification de la grammaire *projet.g*, lancer la commande `src g2java projet.g` puis rafraichir le projet Java.

### Compilation
Pour compiler "test/code.pro", lancer `src/projet.java` et donner "code" comme entrée.

### Notation des points de générations
Un point de génération a pour numéro son numéro de ligne dans `grammar.g` * 10 à quelques unités près.

### Méthodes et attributs utiles
Voir `/src/PtGen.java`.

### AVANT LE RENDU
Enlever `nomDuSource = "test/"+nomDuSource;` de `src/projet.java`.
