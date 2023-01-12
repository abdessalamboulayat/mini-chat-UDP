package org.example.Utilisateur;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Recevoir extends Thread{
    DatagramSocket datagramSocket;
    Rp rp;
    public Recevoir(DatagramSocket socket){
        this.datagramSocket=socket;
        rp = new Rp();
    }

    @Override
    public void run() {
        while (true){
            byte[] tampon = new byte[1024];
            try {

                String message = rp.recevoirMsg(this.datagramSocket);
                //division du message pour determiner son type:
                String[] msg = message.split("[|]");

                if(msg.length>0){
                    switch (msg[0]){
                        //Message text
                        case "message":
                            System.out.println(msg[1]+" : "+msg[2]);
                            break;
                        //Message d'error
                        case "messageError":
                            System.out.println(msg[1]);
                            break;
                        //recevoir la liste des utilisateurs
                        case "listeUtilisateurs":
                            for(int i=1; i<msg.length; i++){
                                System.out.println(msg[i]);
                            }
                            break;
                        //recevoir un fichier
                        case "fichier":
                            System.out.println("recevoir fichier ---");
                            String nomFichier = msg[3];
                            rp.recevoirFichier(this.datagramSocket,nomFichier,msg[2]);
                            break;
                        //recevoir une image
                        case "image":
                            System.out.println("recevoir Image ---");
                            rp.recevoirImage(this.datagramSocket,msg[1],msg[2],msg[3]);
                            break;
                        //recevoir la liste de suggestion d'amis(es)
                        case "suggestionAmis":
                            System.out.println("================================");
                            for(int i=1; i<msg.length; i++){
                                System.out.println("|       *** "+msg[i]);
                            }
                            System.out.println("================================");
                            break;
                        //recevoir la liste d'amis(es) d'un utilisateur
                        case "listAmis":
                            System.out.println("============================");
                            for(int i=1; i<msg.length; i++){
                                System.out.println("|       *** "+msg[i]+"      ");
                            }
                            System.out.println("=============================");
                            break;
                        //message amis est ajouté
                        case "AmiEstAjoute":
                            System.out.println("--- "+msg[1]+" est devenue votre ami(e) ---");
                            break;
                        //recevoir la liste des amis(es) connectés(es)
                        case "listAmisConnecte":
                            if(msg.length<2){
                                System.out.println("--- Aucun(e) ami(e) n'est enligne ---");
                                break;
                            }
                            System.out.println("--------------------------");
                            for(int i=1; i<msg.length; i++){
                                System.out.println("|       * "+msg[i]+"      |");
                            }
                            System.out.println("--------------------------");
                            break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
