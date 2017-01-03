[![Build Status](https://api.travis-ci.org/marcosemiao/jsp-compile-listener.png?branch=master)](https://travis-ci.org/marcosemiao/jsp-compile-listener) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.marcosemiao.jsp-compile-listener/jsp-compile-listener/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.marcosemiao.jsp-compile-listener/jsp-compile-listener)

# Jsp Compile Listener

## Fonctionnalités générales
Cet outil est un listener Web pour Tomcat permettant de compiler au démarrage tous les fichiers JSP contenus dans l'application.
- Compatible à partir de la version Java 5.
- Compatible toutes versions de Tomcat exceptées Tomcat 8 (version bientôt disponible).
- Facile d'utilisation, il suffit de rajouter la dépendance dans votre application.
- A partir de Servlet 3, il n'est plus nécessaire de rajouter le listener dans le descripteur de déploiement (web.xml)
- Disponible sur le repository central de Maven.

## Utilisation rapide

Il y a 2 étapes au maximum à effectuer pour l'utiliser :
- Ajouter la dépendance dans votre webapp:

````xml
<dependency>
	<groupId>com.github.marcosemiao.tomcat.listener</groupId>
	<artifactId>jsp-compile-listener</artifactId>
	<version>1.0.0</version>
    <scope>runtime</scope>
</dependency>
````

- Si votre descripteur de déploiement (web.xml) n'utilise pas les [metadata de Servlet 3](https://blogs.oracle.com/swchan/entry/servlet_3_0_web_fragment), ajoutez le listener web dans votre descripteur de déploiement (web.xml) :

````xml
	<listener>
		<display-name>Jsp Compile Listener</display-name>
		<listener-class>fr.ms.tomcat.listener.jsp.JspCompileListener</listener-class>
	</listener>
````
