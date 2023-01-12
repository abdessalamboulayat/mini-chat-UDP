package org.example.Utilisateur;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Utilisateur {
    private int id;
    private String username;
    private String nom;
    private String prenom;
    private String password;
    private DatagramSocket datagramSocket;
    public Utilisateur() throws UnknownHostException {}
    public Utilisateur(String username) throws UnknownHostException {
        this.username = username;
        try {
            this.datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public Utilisateur(String username, String nom, String prenom, String password) throws UnknownHostException {
        this.username = username;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
    public void setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    byte[] tampon = new byte[1024];
    InetAddress inetAddress = InetAddress.getByName("localhost");
    Scanner clavier = new Scanner(System.in);
    final int PORTSERVEUR = 1234; //Port du serveur
    //Méthode qui permet à l'utilisateur d'ajouter un nouveau ami(e)
    public void ajouterUnAmi(DatagramSocket socket){
        //Suggerer des amis(es) à l'utilisateur:
        System.out.println("Veuillez choisir un ami (saisissez son username): ");
        demandeSuggestionAmis(socket);
        System.out.println("4- Menu précédent");
        String nouveauAmi = clavier.nextLine();
        if(nouveauAmi.equals("4")) return;
        try {
            tampon = new byte[1024];
            tampon = ("ajouterUnAmi|" + username+"|"+nouveauAmi).getBytes();
            DatagramPacket packet = new DatagramPacket(tampon, tampon.length, inetAddress, PORTSERVEUR);
            socket.send(packet);
        }catch (Exception ex){
            throw new RuntimeException();
        }
    }
    //Méthode de demande de suggestion d'amis(es)
    public void demandeSuggestionAmis(DatagramSocket socket){
        try {
            tampon = new byte[1024];
            tampon = ("demandeSuggestionAmis|" + username).getBytes();
            DatagramPacket packet = new DatagramPacket(tampon, tampon.length, inetAddress, PORTSERVEUR);
            socket.send(packet);
        }catch (Exception ex){
            throw new RuntimeException();
        }
    }
    //Méthode qui permet à l'utilisateur d'afficher ses amis(es)
    public void listeAmis(DatagramSocket socket){
        System.out.println("4- Menu précédent");

        try {
            tampon = new byte[1024];
            tampon = ("demandeListAmis|" + username).getBytes();
            DatagramPacket packet = new DatagramPacket(tampon, tampon.length, inetAddress, PORTSERVEUR);
            socket.send(packet);
        }catch (Exception ex){
            throw new RuntimeException();
        }
        String precedent = clavier.nextLine();
        if(precedent.equals("4")) return;
    }
    //Méthode qui permet à l'utilisateur d'afficher ses amis(es) connectés(es)
    public void demandeListAmisConnecte(DatagramSocket socket){
        System.out.println("4- Menu précédent");
        try {
            tampon = new byte[1024];
            tampon = ("demandeListAmisConnecte|" + username).getBytes();
            DatagramPacket packet = new DatagramPacket(tampon, tampon.length, inetAddress, PORTSERVEUR);
            socket.send(packet);
        }catch (Exception ex){
            throw new RuntimeException();
        }
        //choix pour retourner au menu précédent
        String choix = clavier.nextLine();
        if(choix.equals("4")) return;
    }
    //Méthode qui permet à l'utilisateur de se déconnecter de l'application
    public void demandeDeconnexion(DatagramSocket socket){
        try {
            tampon = new byte[1024];
            tampon = ("demandeDeconnexion|"+username).getBytes();
            DatagramPacket packet = new DatagramPacket(tampon, tampon.length, inetAddress, PORTSERVEUR);
            socket.send(packet);
        }catch (Exception ex){

        }
    }
}
