    public class Contact {
        private String id ;
        private String nom;
        private String prenom;
        private String telephone;

        public Contact(String id , String nom, String prenom, String telephone) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.telephone = telephone;
        }
        public String getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getTelephone() { return telephone; }
    }