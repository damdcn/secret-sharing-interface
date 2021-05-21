# **Partage d'images secrètes issues de réseaux sociaux pour la protection de la vie privée**

[![badge_java](https://img.shields.io/badge/Fait%20avec-Java-orange)](https://www.java.com/fr/)  [![badge_um](https://img.shields.io/badge/Projet%20TER%20-Universit%C3%A9%20de%20Montpellier-ff69b4)](https://sciences.edu.umontpellier.fr/)


Inspirées des méthodes de partage de secret, ce projet propose une interface de simulation de partage d'image entre *n* utilisateurs de façon sécurisée. Chaque utilisateur reçoit une information issue de ce partage sous la forme d’une image appelée partie (*share*). Cette partie est personnelle, unique et semble visuellement avoir été générée aléatoirement. L’image originale ne peut alors être reconstruite qu'après réunion d’au moins *k* de ces parties avec *k ≤ n*. 

## Pour commencer


### Pré-requis

- Eclipse
- Java

### Installation (pour Windows)

* Installation de OpenCV

Dans le build path du projet, déroulez le JAR opencv (dans libraries) > Native Library > Edit > Workspace (allez chercher dans src/libs le dossier x64 (si vous êtes en 64bits)) > Ok > Apply and close

* Installation de JavaFX

**Plugin eclipse** :

Eclipse > Help > Install new software

Collez cet url: http://download.eclipse.org/efxclipse/updates-released/3.0.0/site/

Cochez les deux éléments e(fx)clipse puis next > next > accept > finish

Attendre la fin de l'installation et relancer eclipse

**Ajouter les dépendances** :

Téléchargez les libs javafx ici : https://gluonhq.com/download/javafx-11-0-2-sdk-windows/

Créez une librairie utilisateur :
Eclipse > Window > Preference > New (appelez la JavaFX) > Add externals JARs (selectionnez tous les jars du dossier lib de l'archive que vous venez de télécharger) > Ok > Apply and close

Ajouter la librairie au projet :
Clic droit sur le projet > build path > add librairies > (selectionnez JavaFX) > finish

**Ajouter les arguments d'éxécution** :

Eclipse > Run > Run configurations > Java Application (ajoutez une configuration pour le projet si il n'y en a pas) > Arguments > dans 'Arguments VM' ajoutez : 
``--module-path "C:\....\javafx-sdk-x.y.z\lib" --add-modules javafx.controls,javafx.swing,javafx.fxml`` > Apply and close

<hr>

*Note* : Nous avons également travaillé sur une version linux avec makefile (sans eclipse) mais nous somme confronter à un problème d'import de la librairie opencv car elle doit être build pour chaque machine. Vous pouvez cependant trouver cette version [ici](vanilla-version).

## Démarrage

Via eclipse, cliquez simplement sur exécuter à partir de la classe Main.

Pour la version vanilla, faire un ``make`` puis ``make exec``.

## Fabriqué avec

* [OpenCV](https://opencv.org/) - Bibliothèque graphique spécialisée dans le traitement d'images
* [JavaFX](https://openjfx.io/) - Framework et bibliothèque d'interface utilisateur
* [Jaxb](https://javaee.github.io/jaxb-v2/) - API Java permettant de créer des documents XML à partir de classes Java


## Auteurs

* **Damien Duchon** _alias_ [@damdcn](https://github.com/damdcn)
* **Solal Goldstein** _alias_ [@Solal-G](https://github.com/Solal-G)
* **Tiavina Razafintsalama** _alias_ [@TiaviR](https://github.com/TiaviR)
* **Jérémy Simione** _alias_ [@jerems34](https://github.com/jerems34)
