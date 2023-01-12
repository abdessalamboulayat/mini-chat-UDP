package org.example.Serveur;

import org.example.ConnexionDb.ConnexionDb;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServeurThread extends Thread{
    DatagramSocket datagramSocket;
    DatagramSocket datagramSocketServer;
    ArrayList<ConnectedUsers> connectedUsers = new ArrayList<>();
    public ServeurThread(DatagramSocket datagramSocket){
        this.datagramSocketServer = datagramSocket;
        try {
            this.datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
    DatagramPacket datagramPacket;
    //Méthode qui permet d'envoyer un message à l'utilisateur
    public void envoyerAuClient(String message, InetAddress inetAddress, int port) throws IOException {
        byte[] tampon = new byte[1024];
        tampon = message.getBytes();
        datagramPacket = new DatagramPacket(tampon,tampon.length,inetAddress,port);
        datagramSocket.send(datagramPacket);
    }
    //Méthode qui permet d'envoyer un message au destinataire
    public void envoyerAuClientMsg(String emetteur,String message, InetAddress inetAddress, int port) throws IOException {
        byte[] tampon = new byte[1024];
        tampon = ("message|"+emetteur+"|"+message).getBytes();
        datagramPacket = new DatagramPacket(tampon,tampon.length,inetAddress,port);
        datagramSocket.send(datagramPacket);
    }
    //Méthode recevoir fichier
    public void recevoirFichier(String nomFichier) {
        String chemin = nomFichier;
        OutputStream fos;
        byte[] tampon = new byte[1024];
        try {
            fos = new FileOutputStream(chemin);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int nbr=1;
            while(true) {
                DatagramPacket packetIn = new DatagramPacket(tampon, tampon.length);
                datagramSocketServer.receive(packetIn);
                System.out.println(new String(packetIn.getData(),0,tampon.length));

                if(packetIn.getLength()>0) {
                    bos.write(packetIn.getData(), 0, packetIn.getData().length);
                }else {
                    break;
                }
            }
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Méthode envoyer fichier au destinataire
    public void envoyerFichier(String emetteur, String destinataire, String chemin){
        byte[] tampon = new byte[1024];
        InetAddress inetAddress = null; int port = 0;
        for(int i=0;i<connectedUsers.size();i++){
            if(connectedUsers.get(i).username.equals(destinataire)){
                inetAddress = connectedUsers.get(i).inetAddress;
                port = connectedUsers.get(i).port;
            }
        }
        tampon = ("fichier|"+destinataire+"|"+emetteur+"|"+chemin).getBytes();
        DatagramPacket packet1 = new DatagramPacket(tampon,tampon.length,inetAddress,port);
        try {
            datagramSocket.send(packet1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = new File(chemin);
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            InetAddress IPHost = inetAddress;
            byte[] buffer = new byte[1024];
            int nbr=1;
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                DatagramPacket packet = new DatagramPacket(buffer, bytesRead, inetAddress, port);
                datagramSocket.send(packet);
            }
            DatagramPacket packet = new DatagramPacket(new byte[0], 0, inetAddress, port);
            datagramSocket.send(packet);
            fis.close();
            supprimerFile(chemin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Méthode supprimer fichier
    public void supprimerFile(String chemin){
        File file = new File(chemin);
        if(file.delete()){
            System.out.println("Fichier supprimer");
        }
    }
    //Méthode recevoir une image
    public void recevoirImage(String nomImage){
        byte[] tampon = new byte[1024];
        String chemin = nomImage;
        String[] nomImage1 = nomImage.split("[.]");
        File file = new File(chemin);
        BufferedImage img = null;
        boolean on = true;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            int nbr=1;
            while(on) {
                DatagramPacket packetIn = new DatagramPacket(tampon, tampon.length);
                datagramSocketServer.receive(packetIn);
                System.out.println("Mini-packet num : "+nbr+++"  de taille "+packetIn.getLength());
                if(packetIn.getLength()>0) {
                    os.write(packetIn.getData(), 0, packetIn.getData().length);
                }else {
                    on = false;
                }
            }
            img = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
            ImageIO.write(img,nomImage1[1],file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Méthode envoyer image au destinataire
    public void envoyerImage(String emetteur, String destinataire,String chemin){
        BufferedImage image;
        InetAddress inetAddress = null; int port = 0;
        for(int i=0;i<connectedUsers.size();i++){
            if(connectedUsers.get(i).username.equals(destinataire)){
                inetAddress = connectedUsers.get(i).inetAddress;
                port = connectedUsers.get(i).port;
            }
        }
        try {
            File file = new File(chemin);
            String fileName = file.getName();
            String[] fileName1 = fileName.split("[.]");

            byte[] tampon = new byte[1024];
            tampon = ("image|"+fileName1[0]+"|"+fileName1[1]+"|"+emetteur+"|"+destinataire).getBytes();
            DatagramPacket packet = new DatagramPacket(tampon,tampon.length,inetAddress,port);
            datagramSocket.send(packet);

            image = ImageIO.read(new File(chemin));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, fileName1[1], byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byte[] buffer = byteArrayOutputStream.toByteArray();

            byte[] buff = new byte[1024];
            int c=0;int nbr=1;
            for(int i=0;i<buffer.length;i++){
                buff[c] = buffer[i];
                c++;
                if(c==1024){
                    packet = new DatagramPacket(buff, buff.length, inetAddress, port);
                    buff = new byte[1024];
                    datagramSocket.send(packet);
                    c=0;
                }
            }
            if(c>0) {
                packet = new DatagramPacket(buff, c, inetAddress, port);
                datagramSocket.send(packet);
            }
            packet = new DatagramPacket(new byte[0], 0, inetAddress, port);
            datagramSocket.send(packet);
            supprimerFile(chemin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getUser(int port, InetAddress inetAddress){
        for(int i=0; i<connectedUsers.size(); i++){
            if(connectedUsers.get(i).port==port && connectedUsers.get(i).inetAddress==inetAddress){
                return connectedUsers.get(i).username;
            }
        }
        return null;
    }
    //Méthode supprimer utilisateur de liste des utilisateurs connectés
    public void deconnecteUser(String username){
        for (int i=0; i<connectedUsers.size();i++){
            if(connectedUsers.get(i).username.equals(username)){
                connectedUsers.remove(i);
                break;
            }
        }
    }
    @Override
    public void run() {
        String message = null, operation;
        ConnexionDb connexionDb = new ConnexionDb();
        while (true) {
            try {
                byte[] tampon = new byte[1024];
                datagramPacket = new DatagramPacket(tampon,tampon.length);
                //Réception du packet
                datagramSocketServer.receive(datagramPacket);
                //division du packet pour savoir le traitement à effectuer par le serveur
                String[] dataRecu = (new String(datagramPacket.getData(),0,tampon.length).trim()).split("[|]");
                operation = dataRecu[0];

                switch (operation){
                    case "connexion":
                        //connexion Utilisateurs:
                        System.out.println("Connexion d'un nouveau utilisateur");
                        String username = dataRecu[1]; String password = dataRecu[2];
                        //Connexion avec la base de données:
                        if(connexionDb.connexionUtilisateur(username,password)==1){
                            message = "valide";
                            System.out.println("Good information");
                            //Nouveau Utilisateur connecté
                            ConnectedUsers connectedUser1 = new ConnectedUsers(username,datagramPacket.getPort(),datagramPacket.getAddress());
                            if(!connectedUsers.contains(connectedUser1)) {
                                connectedUsers.add(connectedUser1);
                            }
                            System.out.println("Nouveau utilisateur connecté ::: "+username);
                        }
                        else {
                            message = "error";
                            System.out.println("Error information!");
                        }
                        envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());
                        break;
                    //Demande suggestion amis
                    case "demandeSuggestionAmis":
                        System.out.println("--- demande suggestion des amis ---");
                        username = dataRecu[1];
                        List<String> suggestionAmis = connexionDb.suggestionAmis(username);
                        message = "suggestionAmis";
                        for(int i=0; i<suggestionAmis.size(); i++){
                            message=message+"|"+suggestionAmis.get(i);
                        }
                        envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());
                        break;
                    //ajouter un ami
                    case "ajouterUnAmi":
                        System.out.println("--- demande d'ajouter un ami ---");
                        username = dataRecu[1];
                        String nouveauAmi = dataRecu[2];
                        suggestionAmis = connexionDb.suggestionAmis(username);
                        if(!suggestionAmis.contains(nouveauAmi)) {
                            System.out.println("ami ajout");
                            message="messageError|introuvable username";
                            envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());
                            break;
                        }
                            System.out.println("Ami exist");
                            String ajouterUnAmi = connexionDb.ajouterUnAmi(username,nouveauAmi);
                            if(ajouterUnAmi.equals("error")){
                                message="messageError|Error! veuillez reassayer";
                                envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());
                                break;
                            }
                            message="message|"+nouveauAmi +" est devenu votre amis!";
                            envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());

                        break;
                    //envoyer à l'utilisateur la liste de ses amis(es)
                    case "demandeListAmis":
                        System.out.println("--- demande liste amis ---");
                        username = dataRecu[1];
                        List<String> listAmis = connexionDb.listeAmiUtilisateur(username);
                        if(listAmis.size()==0){
                            message = "messageError|Aucun ami(e)";
                            envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());
                            break;
                        }
                        message="listAmis";
                        boolean connecte=false;
                        for(int i=0; i<listAmis.size();i++){
                            for (int j=0; j<connectedUsers.size();j++){
                                if(listAmis.get(i).equals(connectedUsers.get(j).username)){
                                    connecte=true;
                                    break;
                                }
                            }
                            message=message+"|"+listAmis.get(i);
                        }
                        envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());
                        break;
                    //Envoyer à l'utilisateur la liste de ses amis(es) connectés(es)
                    case "demandeListAmisConnecte":
                        System.out.println("--- Liste amis connectés(es) ---");
                        username = dataRecu[1];
                        listAmis = connexionDb.listeAmiUtilisateur(username);
                        message="listAmisConnecte";
                        for(int i=0; i<listAmis.size();i++){
                            connecte=false;
                            for (int j=0; j<connectedUsers.size();j++){
                                if(listAmis.get(i).equals(connectedUsers.get(j).username)){
                                    connecte=true;
                                    break;
                                }
                            }
                            if(connecte){
                                message=message+"|"+listAmis.get(i);
                            }
                        }
                        envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());

                        break;
                    //Envoyer le message recu de l'emetteur au destinataire
                    case "envoyerMsgTxt":
                        message = dataRecu[3];
                        String emetteur = dataRecu[2];
                        String destinataire = dataRecu[1];
                        System.out.println("--- Message Txt ---");
                        System.out.println("Message : "+message);
                        System.out.println("Emetteur : "+emetteur);
                        System.out.println("Destinataire : "+destinataire);
                        //recherche de l'utilisateur:
                        InetAddress inetAddress=null; int port=0;
                        for (int i=0; i<connectedUsers.size();i++){
                            if(connectedUsers.get(i).username.equals(destinataire)) {
                                inetAddress = connectedUsers.get(i).inetAddress;
                                port = connectedUsers.get(i).port;
                            }
                        }
                        envoyerAuClientMsg(emetteur,message,inetAddress,port);
                        break;
                    //liste utilisateur
                    case "listeUtilisateurs":
                        System.out.println("--- liste utilisateurs ---");
                        List<String> listeUtilisateurs = connexionDb.listeUtilisateur();
                        username = getUser(datagramPacket.getPort(),datagramPacket.getAddress());
                        message="";
                        for(int i=0; i<listeUtilisateurs.size(); i++){
                            if(listeUtilisateurs.get(i).equals(username)){
                                continue;
                            }
                            message=message+"|"+listeUtilisateurs.get(i);
                        }
                        envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());
                        break;
                    //Envoyer le fichier recu de l'emetteur au destinataire
                    case "fichier":
                        System.out.println("Je viens de recevoir un fichier---");
                        recevoirFichier(dataRecu[3]);
                        envoyerFichier(dataRecu[2],dataRecu[1],dataRecu[3]);
                        break;
                    //Envoyer l'image recu de l'emetteur au destinataire
                    case "image":
                        System.out.println("Je viens de recevoir une image: ---");
                        recevoirImage(dataRecu[1]);
                        System.out.println("image dans le serveur");
                        String chemin = dataRecu[1];
                        envoyerImage(dataRecu[2],dataRecu[3],chemin);
                        break;
                    //réponse à la demande de déconnexion de l'utilisateur
                    case "demandeDeconnexion":
                        System.out.println("--- Demande déconnexion ---");
                        deconnecteUser(dataRecu[1]);
                        envoyerAuClient("Vous êtes déconnectés(es)",datagramPacket.getAddress(),datagramPacket.getPort());
                        break;

                    case "verificationUsername":
                        //Verification Username du nouveau utilisateur
                        if(connexionDb.verifierUniciteUsername(dataRecu[1]).equals("username unique")){
                            message = "200OK";
                            System.out.println("usernameUnique");
                        }
                        else {
                            message = "username existe déja";
                            System.out.println("Username existe déja");
                        }
                        envoyerAuClient("Vous êtes déconnectés(es)",datagramPacket.getAddress(),datagramPacket.getPort());
                        break;
                    //Traitement demande inscription d'un nouveau utilisateur
                    case "inscription":
                        System.out.println("--- inscription ---");
                        connexionDb.inscription(dataRecu[1],dataRecu[2],dataRecu[3],dataRecu[4]);
                        message = "Compte crée avec succées";
                        envoyerAuClient(message,datagramPacket.getAddress(),datagramPacket.getPort());
                        break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    //classe des utilisateurs connectés afin de garder leurs ports et leurs addresses
    public class ConnectedUsers{
        int port;
        InetAddress inetAddress;
        String username;

        public ConnectedUsers(String username, int port, InetAddress inetAddress){
            this.port = port;
            this.username = username;
            this.inetAddress = inetAddress;
        }
    }
}
