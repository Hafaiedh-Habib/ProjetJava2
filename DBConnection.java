import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/db_contacts";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // yraja3 class   // bech testad3a 7aja men bara el jdk ysob el deiver  ***  bech tnajem tconnecti 3al base !!!! ***
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
        return conn;
    }
}
