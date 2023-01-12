package org.example.ConnexionDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnexionDb {
    public Connection connexion() {
        try {
            //charger le pilote JDBC
            Class.forName("com.mysql.jdbc.Driver");
            //Cree une connexion à la base de données
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minichat", "root", "");
            return connection;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Connexion Utilisateur:
    public int connexionUtilisateur(String username, String password) {
        try {
            Connection connectionDb = connexion();
            PreparedStatement ps = connectionDb.prepareStatement("select * from utilisateur where username = '" + username + "'");
            ResultSet res = ps.executeQuery();

            if (!res.next()) {
                return 0;
            }

            if (res.getString("password").equals(password)) {
                return 1;
            } else {
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Verifier l'unicité de Username
    public String verifierUniciteUsername(String username) {
        try {
            Connection connectionDb = connexion();
            PreparedStatement ps = connectionDb.prepareStatement("select * from utilisateur where username = '" + username + "'");
            ResultSet res = ps.executeQuery();

            if (!res.next()) {
                return "username unique";
            } else {
                return "existe deja";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Inscrire un nouveau Utilisateur
    public void inscription(String nom, String prenom, String username, String password) {
        try {
            Connection connectionDb = connexion();
            if (nom != null && prenom != null && username != null && password != null) {
                PreparedStatement ps = connectionDb.prepareStatement(
                        "insert into utilisateur(username, nom, prenom, password) " +
                                "VALUES('" + username + "','" + nom + "','" + prenom + "','" + password + "')");
                int res = ps.executeUpdate();
                if (res > 0) {
                    System.out.println("NOUVEAU UTILISATEUR EST AJOUTÉ");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Suggestion des amis à un utilisateur:
    public List<String> suggestionAmis(String username) {
        if (username != null) {
            Connection connexionDb = connexion();
            PreparedStatement ps = null;
            try {
                ps = connexionDb.prepareStatement("select * from utilisateur where username NOT LIKE '" + username + "'");
                ResultSet res = ps.executeQuery();
                List<String> suggestionAmis = new ArrayList<>();
                while (res.next()) {
                    suggestionAmis.add(res.getString("username"));
                }
                return suggestionAmis;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    //liste des utilisateur
    public List<String> listeUtilisateur() {
        try {
            Connection connectionDb = connexion();
            PreparedStatement ps = connectionDb.prepareStatement("select * from utilisateur");
            ResultSet res = ps.executeQuery();

            /*if(!res.next()){
                return null;
            }*/
            List<String> listUtilisateurs = new ArrayList<>();
            while (res.next()) {
                System.out.println(res.getString("username"));
                listUtilisateurs.add(res.getString("username"));
            }
            return listUtilisateurs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Ajouter un ami:
    public String ajouterUnAmi(String user1, String user2) {
        try {
            if (user1 != null && user2 != null) {
                Connection connectionDb = connexion();
                PreparedStatement ps = connectionDb.prepareStatement("select * from utilisateur where username = '" + user1 + "'");
                ResultSet res = ps.executeQuery();
                PreparedStatement ps1 = connectionDb.prepareStatement("select * from utilisateur where username = '" + user2 + "'");
                ResultSet res1 = ps1.executeQuery();

                if (!res.next() || !res1.next()) {
                    return "utilisateurNexistePas";
                } else {
                    PreparedStatement ps2 = connectionDb.prepareStatement(
                            "insert into amis(utilisateur1, utilisateur2) " +
                                    "VALUES('" + res.getString("idUtilisateur") + "','" + res1.getString("idUtilisateur") + "')");
                    int res2 = ps2.executeUpdate();
                    PreparedStatement ps3 = connectionDb.prepareStatement(
                            "insert into amis(utilisateur1, utilisateur2) " +
                                    "VALUES('" + res1.getString("idUtilisateur") + "','" + res.getString("idUtilisateur") + "')");
                    int res3 = ps3.executeUpdate();
                    if (res2 < 0 || res3 < 0) {
                        return "error";
                    }
                    return "nouveau ami est ajouté";
                }
            } else {
                return "error";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //liste des amis d'un utilisateur:
    public List<String> listeAmiUtilisateur(String username) {
        try {
            Connection connectionDb = connexion();
            PreparedStatement ps = connectionDb.prepareStatement("select * from utilisateur WHERE username='" + username + "'");
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                PreparedStatement ps1 = connectionDb.prepareStatement("SELECT utilisateur2 from amis where utilisateur1='" + res.getString("idUtilisateur") + "'");
                ResultSet res1 = ps1.executeQuery();
                List<String> listAmis = new ArrayList<>();
                while (res1.next()){
                    System.out.println("retourner : "+res1.getString("utilisateur2"));
                    PreparedStatement ps2 = connectionDb.prepareStatement("SELECT * FROM utilisateur WHERE idUtilisateur ='" + res1.getString("utilisateur2") + "'");
                    ResultSet res2 = ps2.executeQuery();
                    if(res2.next()) {
                        System.out.println("res2 : "+res2.getString("username"));
                        listAmis.add(res2.getString("username"));
                    }
                    System.out.println("username:" +res.getString("username"));
                }
                return listAmis;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
