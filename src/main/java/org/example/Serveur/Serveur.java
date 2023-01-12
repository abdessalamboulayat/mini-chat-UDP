package org.example.Serveur;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Serveur extends Thread{
    DatagramSocket datagramSocketServer;
    int port;
    byte[] tampon;
    InetAddress inetAddress;

    public Serveur(int port){
        this.port=port;
        try {
            inetAddress = InetAddress.getByName("localhost");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        System.out.println("Le serveur est demarré");
        try {
            datagramSocketServer = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        //Création du thread serveur thread qui prend en charge les demandes de chaque utilisateur
        ServeurThread serveurThread = new ServeurThread(datagramSocketServer);
        serveurThread.start();
    }
}
