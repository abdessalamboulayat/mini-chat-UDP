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
                String[] msg = message.split("[|]");
                if(msg.length>0){
                    switch (msg[0]){
                        case "message":
                            System.out.println(msg[1]+" : "+msg[2]);
                            break;
                        case "messageError":
                            System.out.println(msg[1]);
                            break;
                        case "listeUtilisateurs":
                            for(int i=1; i<msg.length; i++){
                                System.out.println(msg[i]);
                            }
                            break;
                        case "fichier":
                            System.out.println("recevoir fichier ---");
                            String nomFichier = msg[3];
                            rp.recevoirFichier(this.datagramSocket,nomFichier,msg[2]);
                            break;

                        case "image":
                            System.out.println("recevoir Image ---");
                            rp.recevoirImage(this.datagramSocket,msg[1],msg[2],msg[3]);
                            break;

                        case "suggestionAmis":
                            System.out.println("================================");
                            for(int i=1; i<msg.length; i++){
                                System.out.println("|       *** "+msg[i]);
                            }
                            System.out.println("================================");
                            break;

                        case "listAmis":
                            System.out.println("============================");
                            for(int i=1; i<msg.length; i++){
                                System.out.println("|       *** "+msg[i]+"      ");
                            }
                            System.out.println("=============================");
                            break;

                        case "AmiEstAjoute":
                            System.out.println("--- "+msg[1]+" est devenue votre ami(e) ---");
                            break;

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
