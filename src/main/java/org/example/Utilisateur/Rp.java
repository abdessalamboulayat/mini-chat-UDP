package org.example.Utilisateur;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Rp {
    private boolean enTraindEcrire;
    private boolean entrainDeLire;

    public Rp() {
        this.enTraindEcrire = false;
        this.entrainDeLire = false;
    }
    public Rp(boolean enTraindEcrire, boolean entrainDeLire) {
        this.enTraindEcrire = false;
        this.entrainDeLire = false;
    }
    public boolean isEnTraindEcrire() {
        return enTraindEcrire;
    }
    public void setEnTraindEcrire(boolean enTraindEcrire) {
        this.enTraindEcrire = enTraindEcrire;
    }
    public boolean isEntrainDeLire() {
        return entrainDeLire;
    }
    public void setEntrainDeLire(boolean entrainDeLire) {
        this.entrainDeLire = entrainDeLire;
    }

    Scanner clavier = new Scanner(System.in);
    InetAddress inetAddress;
    final int PORT = 1234;

    {
        try {
            inetAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void envoyerMsgTxt(DatagramSocket socket,String emetteur){
        if(enTraindEcrire || entrainDeLire){
            System.out.println("Attendez-vous ...");
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        enTraindEcrire=true;
        System.out.println("DESTINATAIRE : ");
        String destinataire = clavier.nextLine();
        String msg;
        byte[] tampon;
        while(true) {
            tampon = new byte[1024];
            msg = clavier.nextLine();
            if (msg.equals("EXIT") || msg.equals("Exit") || msg.equals("exit")) {
                break;
            }
            try {
                tampon = ("envoyerMsgTxt|" + destinataire + "|" + emetteur + "|" + msg).getBytes();
                DatagramPacket packet = new DatagramPacket(tampon, tampon.length, inetAddress, PORT);
                socket.send(packet);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        enTraindEcrire = false;
        notify();
    }
    public synchronized String recevoirMsg(DatagramSocket socket){
        if(entrainDeLire || enTraindEcrire){
            System.out.println("Attendez-vouuuuss ");
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        entrainDeLire = true;
        byte[] tampon = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(tampon,tampon.length);
        try {
            socket.receive(datagramPacket);
            String message = new String(datagramPacket.getData(),0,tampon.length).trim();
            entrainDeLire = false;
            notify();
            return message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized void envoyerUnFichier(DatagramSocket socket,String emetteur){
        if(enTraindEcrire || entrainDeLire){
            System.out.println("Vous devez attendre ... ");
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        enTraindEcrire=true;
        byte[] tampon = new byte[1024];
        System.out.println("DESTINATAIRE: ");
        String destinataire = clavier.nextLine();
        try {

            System.out.println("Chemin du fichier: ");
            String chemin = clavier.nextLine();
            File file = new File(chemin);
            String nomFichier = file.getName();
            tampon = ("fichier|"+destinataire+"|"+emetteur+"|"+nomFichier).getBytes();
            DatagramPacket packet1 = new DatagramPacket(tampon,tampon.length,inetAddress,PORT);
            socket.send(packet1);

            FileInputStream fileInputStream;
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                DatagramPacket packet = new DatagramPacket(buffer, bytesRead, inetAddress, PORT);
                socket.send(packet);
            }
            DatagramPacket packet = new DatagramPacket(new byte[0], 0, inetAddress, PORT);
            socket.send(packet);
            System.out.println("Fichier envoyé ");
            enTraindEcrire=false;
            notify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public synchronized void recevoirFichier(DatagramSocket datagramSocket,String nomFichier,String emetteur) {
        if(entrainDeLire || enTraindEcrire){
            System.out.println("Attendez-vous ");
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        entrainDeLire = true;
        String chemin = "E:\\auto\\java\\chat\\copie\\projet6\\src\\main\\resources\\"+nomFichier;
        OutputStream outputStream;
        byte[] tampon = new byte[1024];
        try {
            outputStream = new FileOutputStream(chemin);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            while(true) {
                DatagramPacket packetIn = new DatagramPacket(tampon, tampon.length);
                datagramSocket.receive(packetIn);
                if(packetIn.getLength()>0) {
                    bufferedOutputStream.write(packetIn.getData(), 0, packetIn.getData().length);
                }else {
                    break;
                }
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            System.out.println("--- "+emetteur+"Vous avez bien recu un fichier: "+chemin+" ---");
            entrainDeLire=false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void envoyerImage(DatagramSocket datagramSocket, String emetteur){
        if(enTraindEcrire || entrainDeLire){
            System.out.println("Vous devez attendre ... ");
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        enTraindEcrire=true;
        BufferedImage image;
        System.out.println("DESTINATAIRE: ");
        String destinataire = clavier.nextLine();
        System.out.println("Chemin de l'image: ");
        String chemin = clavier.nextLine();
        try {
            File file = new File(chemin);
            String nomImage = file.getName();
            String[] nomImage1 = nomImage.split("[.]");
            byte[] tampon = new byte[1024];
            tampon = ("image|"+nomImage+"|"+emetteur+"|"+destinataire).getBytes();
            DatagramPacket packet = new DatagramPacket(tampon,tampon.length,inetAddress,PORT);
            datagramSocket.send(packet);

            image = ImageIO.read(new File(chemin));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, nomImage1[1], byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byte[] buffer = byteArrayOutputStream.toByteArray();

            byte[] buff = new byte[1024];
            int c=0;int nbr=1;
            for(int i=0;i<buffer.length;i++){
                buff[c] = buffer[i];
                c++;
                if(c==1024){
                    packet = new DatagramPacket(buff, buff.length, inetAddress, PORT);
                    buff = new byte[1024];
                    datagramSocket.send(packet);
                    System.out.println("Mini-packet Envoyé : "+nbr+++"  "+c);
                    c=0;
                }
            }
            if(c>0) {
                packet = new DatagramPacket(buff, c, inetAddress, PORT);
                datagramSocket.send(packet);
                System.out.println("Dérnier mini-packet Envoyé : "+c);
            }
            packet = new DatagramPacket(new byte[0], 0, inetAddress, PORT);
            datagramSocket.send(packet);
            System.out.println("Image envoyé");
            enTraindEcrire = false;
            //clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void recevoirImage(DatagramSocket socket, String imageName, String extension,String emetteur){
        System.out.println(imageName+"||"+extension);
        if(enTraindEcrire || entrainDeLire){
            System.out.println("Vous devez attendre ... ");
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        entrainDeLire=true;
        byte[] tampon = new byte[1024];
        String chemin = "E:\\auto\\java\\chat\\copie\\projet6\\src\\main\\resources\\"+imageName+"."+extension;
        File file = new File(chemin);
        BufferedImage img = null;
        boolean on = true;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            int nbr=1;
            while(on) {
                DatagramPacket packetIn = new DatagramPacket(tampon, tampon.length);
                socket.receive(packetIn);
                if(packetIn.getLength()>0) {
                    os.write(packetIn.getData(), 0, packetIn.getData().length);
                }else {
                    on = false;
                }
            }
            img = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
            ImageIO.write(img,extension,file);
            entrainDeLire=false;
            os.close();
            System.out.println("--- "+emetteur+" Vous a envoyé une image:  ---"+chemin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
