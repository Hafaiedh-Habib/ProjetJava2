import java.sql.*;
import java.util.*;


public class GestionContacts {

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {  // traja3 collections d'objets contact

            while (rs.next()) {
                contacts.add(new Contact(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("telephone")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public boolean ajouterContact(Contact contact) {
        if (contactExiste(contact)) {
            return false; // Le contact existe déjà
        }

        if (!telephoneValide(contact.getTelephone())) {
            return false; // Numéro de téléphone invalide  {8chiffre}
        }

        String sql = "INSERT INTO contacts (id, nom, prenom, telephone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contact.getId());
            stmt.setString(2, contact.getNom());
            stmt.setString(3, contact.getPrenom());
            stmt.setString(4, contact.getTelephone());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean supprimerContact(String id) {
        String sql = "DELETE FROM contacts WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (Exception e) {
            return false;
        }
    }


    public boolean modifierContact(String id, Contact nouveauContact) {
        if (!telephoneValide(nouveauContact.getTelephone())) {
            return false;  // Retourne false si le téléphone est invalide
        }
        if (contactExiste(nouveauContact)){
            return  false;
        }

        String sql = "UPDATE contacts SET nom=?, prenom=?, telephone=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nouveauContact.getNom());
            stmt.setString(2, nouveauContact.getPrenom());
            stmt.setString(3, nouveauContact.getTelephone());
            stmt.setString(4, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;  // Retourne true si des lignes ont été affectées

        } catch (Exception e) {
            return false;
        }
    }


    public List<Contact> rechercherContacts(String motCle) {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE nom LIKE ? OR prenom LIKE ? OR telephone LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeMotCle = "%" + motCle + "%";
            stmt.setString(1, likeMotCle);
            stmt.setString(2, likeMotCle);
            stmt.setString(3, likeMotCle);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contacts.add(new Contact(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("telephone")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private boolean contactExiste(Contact contact) {
        String sql = "SELECT COUNT(*) FROM contacts WHERE nom=? AND prenom=? AND telephone=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contact.getNom());
            stmt.setString(2, contact.getPrenom());
            stmt.setString(3, contact.getTelephone());

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (Exception e) {
            return false;
        }
    }


    private boolean telephoneValide(String telephone) {
        return telephone != null && telephone.matches("\\d{8}");
    }
}