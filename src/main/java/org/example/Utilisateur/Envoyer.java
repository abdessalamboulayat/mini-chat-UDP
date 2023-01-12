package org.example.Utilisateur;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Envoyer extends Thread{
    private Utilisateur utilisateur;
    private String typeMessage;
    private String chemin;
    Rp rp;

    public Envoyer(Utilisateur utilisateur, String typeMessage) throws UnknownHostException {
        this.utilisateur = utilisateur;
        this.typeMessage = typeMessage;
        rp = new Rp();
    }
    public Envoyer(Utilisateur utilisateur, String typeMessage, String chemin) throws UnknownHostException {
        this.utilisateur=utilisateur;
        this.typeMessage=typeMessage;
        this.chemin=chemin;
    }

    Scanner clavier = new Scanner(System.in);

    @Override
    public void run() {

        switch (typeMessage){
            case "messageText":
                rp.envoyerMsgTxt(this.utilisateur.getDatagramSocket(),this.utilisateur.getUsername());
                break;
            case "fichier":
                rp.envoyerUnFichier(this.utilisateur.getDatagramSocket(),this.utilisateur.getUsername());
                break;
            case "image":
                rp.envoyerImage(this.utilisateur.getDatagramSocket(),this.utilisateur.getUsername());
                break;
        }
    }
}
