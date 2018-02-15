# COMP_Projet
Projet de COMP - L3 INFO g1.2

---

## Fichiers du projet

### Fichiers sources (/src)
* **projet.java** : Fichier .java principal à exécuter
* **PtGen.java** : Points de génération
* **projet.g** : Grammaire

### Librairies (/lib)
* **libclass** : Classes Ecriture, Lecture, UtilLex et ExecMapile
* **antlr** : Jar antlr

### Jeux de tests (/)
* **DeclExp-TX.pro** : Jeux des profs (de T1 à T6)

---

## Notes

### Génération avec ANTLR
A chaque modification de la grammaire *projet.g*, lancer la commande `/src g2java projet.g` puis rafraichir le projet Java.

### Notation des points de générations
Un point de génération a pour numéro son numéro de ligne dans `/grammar.txt` * 10 à quelques unités près.

### Méthodes et attributs utiles
Voir `/src/PtGen.java`.
