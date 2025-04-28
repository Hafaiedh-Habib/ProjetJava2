import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.sql.SQLException;

public class InterfaceGraphique extends JFrame {
    private GestionContacts contactDAO = new GestionContacts();
    private JList<String> listContacts;
    private DefaultListModel<String> listModel;
    private JTextField tfId, tfNom, tfPrenom, tfTelephone, tfRecherche;

    public InterfaceGraphique() {
        setTitle("Projet Java Gestion des Contacts");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // pour l'afficher au centre pas dans une autre place

        listModel = new DefaultListModel<>();
        listContacts = new JList<>(listModel);
        listContacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Création des composants
        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnRechercher = new JButton("Rechercher");
        JButton btnReset = new JButton("Réinitialiser");

        tfId = new JTextField(5);
        tfNom = new JTextField(15);
        tfPrenom = new JTextField(15);
        tfTelephone = new JTextField(15);
        tfRecherche = new JTextField(15);

        // Configuration du layout
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel formulaire
        JPanel panelFormulaire = new JPanel(new GridLayout(3, 1, 5, 5));

        JPanel panelHaut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHaut.add(new JLabel("ID:"));
        panelHaut.add(tfId);
        panelHaut.add(new JLabel("Nom:"));
        panelHaut.add(tfNom);
        panelHaut.add(new JLabel("Prénom:"));
        panelHaut.add(tfPrenom);
        panelHaut.add(new JLabel("Téléphone:"));
        panelHaut.add(tfTelephone);

        JPanel panelMilieu = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelMilieu.add(btnAjouter);
        panelMilieu.add(btnModifier);
        panelMilieu.add(btnSupprimer);

        JPanel panelBas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBas.add(new JLabel("Recherche:"));
        panelBas.add(tfRecherche);
        panelBas.add(btnRechercher);
        panelBas.add(btnReset);

        panelFormulaire.add(panelHaut);
        panelFormulaire.add(panelMilieu);
        panelFormulaire.add(panelBas);

        panelPrincipal.add(panelFormulaire, BorderLayout.NORTH);
        panelPrincipal.add(new JScrollPane(listContacts), BorderLayout.CENTER);

        add(panelPrincipal);

        // Chargement initial des contacts
        chargerContacts();

        // Gestion des événements
        btnAjouter.addActionListener(e -> ajouterContact());
        btnModifier.addActionListener(e -> modifierContact());
        btnSupprimer.addActionListener(e -> supprimerContact());
        btnRechercher.addActionListener(e -> rechercherContact());
        btnReset.addActionListener(e -> {
            tfRecherche.setText("");
            chargerContacts();
        });

        listContacts.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listContacts.getSelectedIndex() != -1) {
                String selected = listContacts.getSelectedValue();
                String[] parts = selected.split(" - ");
                if (parts.length >= 4) {
                    tfId.setText(parts[0]);
                    tfNom.setText(parts[1]);
                    tfPrenom.setText(parts[2]);
                    tfTelephone.setText(parts[3]);
                }
            }
        });
    }

    private void ajouterContact() {
        try {
            if (tfId.getText().trim().isEmpty() || tfNom.getText().trim().isEmpty() ||
                    tfPrenom.getText().trim().isEmpty() || tfTelephone.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Tous les champs doivent être remplis");
            }

            Contact contact = new Contact(
                    tfId.getText().trim(),
                    tfNom.getText().trim(),
                    tfPrenom.getText().trim(),
                    tfTelephone.getText().trim()
            );

            contactDAO.ajouterContact(contact);
            chargerContacts();
            viderChamps();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void modifierContact() {
        try {
            int selectedIndex = listContacts.getSelectedIndex();
            if (selectedIndex == -1) {
                throw new IllegalStateException("Veuillez sélectionner un contact à modifier");
            }

            String id = tfId.getText().trim();
            Contact nouveauContact = new Contact(
                    id,
                    tfNom.getText().trim(),
                    tfPrenom.getText().trim(),
                    tfTelephone.getText().trim()
            );

            contactDAO.modifierContact(id, nouveauContact);
            chargerContacts();
            viderChamps();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void supprimerContact() {
        try {
            int selectedIndex = listContacts.getSelectedIndex();
            if (selectedIndex == -1) {
                throw new IllegalStateException("Veuillez sélectionner un contact à supprimer");
            }

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir supprimer ce contact?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                String selected = listContacts.getSelectedValue();
                String id = selected.split(" - ")[0];
                contactDAO.supprimerContact(id);
                chargerContacts();
                viderChamps();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void rechercherContact() {
        String motCle = tfRecherche.getText().trim();
        if (!motCle.isEmpty()) {
            List<Contact> resultats = contactDAO.rechercherContacts(motCle);
            remplirListe(resultats);
        }
    }

    private void chargerContacts() {
        List<Contact> contacts = contactDAO.getAllContacts();
        remplirListe(contacts);
    }

    private void remplirListe(List<Contact> contacts) {
        listModel.clear();
        for (Contact contact : contacts) {
            listModel.addElement(contact.getId() + " - " + contact.getNom() + " - " + contact.getPrenom() + " - " + contact.getTelephone());
        }
    }

    private void viderChamps() {
        tfId.setText("");
        tfNom.setText("");
        tfPrenom.setText("");
        tfTelephone.setText("");
        listContacts.clearSelection();
    }
}
