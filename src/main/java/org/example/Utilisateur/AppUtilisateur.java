package org.example.Utilisateur;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class AppUtilisateur {
    Scanner clavier = new Scanner(System.in);
    byte[] tampon = new byte[1024];
    final int PORTSERVEUR = 1234;
    InetAddress inetAddress;

    {
        try {
            inetAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    //Méthode qui permet à l'utilisateur de se connecter à l'application
    public Utilisateur connexion(DatagramSocket socket) throws IOException {
        String reponseServeur;
        boolean isConnected;
        String username;
        do {
            System.out.println("Username: ");
            username = clavier.nextLine();
            System.out.println("Password: ");
            String password = clavier.nextLine();
            tampon = ("connexion|" + username + "|" + password).getBytes();
            DatagramPacket packet = new DatagramPacket(tampon, tampon.length, inetAddress, PORTSERVEUR);
            socket.send(packet);
            tampon = new byte[1024];
            DatagramPacket packetResponse = new DatagramPacket(tampon, tampon.length);

            socket.receive(packetResponse);
            reponseServeur = new String(packetResponse.getData(), 0, tampon.length).trim();
            System.out.println(reponseServeur);
            isConnected = true;
        } while (reponseServeur.equals("error"));
        return new Utilisateur(username);
    }
    //Méthode qui permet à l'utilisateur de s'inscrire
    public void inscription(DatagramSocket datagramSocket) throws IOException {
        //Inscription:
        System.out.println("--- Inscription --- ");
        //Demande des information à l'utilisateur
        System.out.println("Nom: ");
        String nom = clavier.nextLine();
        System.out.println("Prénom: ");
        String prenom = clavier.nextLine();
        String username;
        String informationInscription;
        DatagramPacket packetInscription;
        String reponseServeur;
        do {
            System.out.println("Username: ");
            username = clavier.nextLine();
            tampon = new byte[1024];

            informationInscription = "verificationUsername|" + username;
            tampon = informationInscription.getBytes();
            packetInscription = new DatagramPacket(tampon, tampon.length, inetAddress, PORTSERVEUR);
            //envoyer username pour savoir est ce qu'il existe déja ou non
            datagramSocket.send(packetInscription);
            tampon = new byte[1024];
            DatagramPacket packetResponse = new DatagramPacket(tampon, tampon.length);
            datagramSocket.receive(packetResponse);

            reponseServeur = new String(packetResponse.getData(), 0, tampon.length).trim();
            if(reponseServeur.equals("usernameExisteDeja")) System.out.println("Username existe deja, Veuillez saisir un nouveau");
        }while (reponseServeur.equals("usernameExisteDeja"));

        System.out.println("Password: ");
        String password = clavier.nextLine();
        tampon = new byte[1024];
        informationInscription = "inscription|"+nom+"|"+prenom+"|"+username+"|"+password;
        tampon = informationInscription.getBytes();
        packetInscription = new DatagramPacket(tampon,tampon.length,inetAddress,PORTSERVEUR);
        //envoi des informations du nouveau utilisateur au serveur
        datagramSocket.send(packetInscription);

        tampon = new byte[1024];
        DatagramPacket packetResponse = new DatagramPacket(tampon, tampon.length);
        datagramSocket.receive(packetResponse);
        reponseServeur = new String(packetResponse.getData(), 0, tampon.length).trim();
        System.out.println(reponseServeur);
    }

    public static void main(String[] args) throws IOException {
        while (true){
            Scanner clavier = new Scanner(System.in);
            String choix;
            AppUtilisateur app = new AppUtilisateur();
            Utilisateur utilisateurConnecte=null;
            System.out.println("1- Connexion\n" +
                    "2- Inscription");
            choix = clavier.nextLine();
            DatagramSocket socket = new DatagramSocket();
            switch (choix){
                //Connexion:
                case "1":
                    System.out.println("--- Connexion ---");
                    utilisateurConnecte = app.connexion(socket);
                    break;
                //Inscription:
                case "2":
                    app.inscription(socket);
                    break;
            }

            //Apres Connexion:
            while(utilisateurConnecte!=null) {
                Recevoir recevoir = new Recevoir(socket);
                recevoir.start();
                System.out.println("1- Envoyer un message\n" +
                        "2- Ajouter un ami\n" +
                        "3- Liste d'amis(es) connectés(es)\n" +
                        "4- Liste d'amis(es)\n" +
                        "7- Déconnexion");
                //L'utilisateur choisis l'operation qu'il veut:
                choix = clavier.nextLine();
                switch (choix) {
                    //Operation Message:
                    case "1":
                        System.out.println("1- Text\n" +
                                "2- Fichier\n" +
                                "3- Image\n" +
                                "4- Menu précédent");
                        choix = clavier.nextLine();

                        if(choix.equals("4")) break; //Retourner au menu précédent
                        switch (choix) {
                            case "1":
                                //L'envoi d'un message texte
                                Envoyer envoyer = new Envoyer(utilisateurConnecte, "messageText");
                                envoyer.start();
                                try {
                                    envoyer.join();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                System.out.println("Message envoyé");
                                break;
                            case "2":
                                //L'envoi d'une piéce jointe [Fichier]
                                Envoyer envoyerFichier = new Envoyer(utilisateurConnecte, "fichier");
                                envoyerFichier.start();
                                try {
                                    envoyerFichier.join();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                            case "3":
                                //L'envoi d'une piéce jointe [Image]
                                Envoyer envoyerImage = new Envoyer(utilisateurConnecte, "image");
                                envoyerImage.start();
                                try {
                                    envoyerImage.join();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                        }
                        break;

                    case "2":
                        //Ajouter un nouveau ami(e)
                        System.out.println("--- Propositions d'amis ---");
                        utilisateurConnecte.ajouterUnAmi(socket);
                        break;

                    case "3":
                        //Liste des ami(es) connectés(es)
                        System.out.println("--- Liste amis(es) connectés(es) ---");
                        utilisateurConnecte.demandeListAmisConnecte(socket);
                        break;

                    case "4":
                        //Liste d'amis(es) de l'utilisateur
                        System.out.println("--- Liste amis ---");
                        utilisateurConnecte.listeAmis(socket);
                        break;

                    case "7":
                        //l'utilisateur se déconnecte de l'application
                        System.out.println("--- Déconnexion ---");
                        utilisateurConnecte.demandeDeconnexion(socket);
                        utilisateurConnecte=null;
                        break;
                }
            }
        }
    }
}
