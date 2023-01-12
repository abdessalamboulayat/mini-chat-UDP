package org.example.Serveur;

public class AppServeur {
    public static void main(String[] args) {
        final int PORTSERVEUR = 1234;
        //Démarrage du serveur:
        Serveur serveur = new Serveur(PORTSERVEUR);
        serveur.start();
    }
}
