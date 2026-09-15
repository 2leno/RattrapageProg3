package mg.hei.kofia.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Fournit des connexions JDBC vers la base PostgreSQL de la cooperative.
 *
 * CETTE CLASSE VOUS EST FOURNIE. Ne la modifiez pas.
 *
 * Utilisation attendue dans votre DataRetriever :
 *
 *   try (Connection connection = dbConnection.getConnection();
 *        PreparedStatement statement = connection.prepareStatement(sql)) {
 *       statement.setString(1, someId);
 *       try (ResultSet resultSet = statement.executeQuery()) {
 *           ...
 *       }
 *   } catch (SQLException e) {
 *       throw new RuntimeException(e);
 *   }
 *
 * Chaque appel a getConnection() ouvre une nouvelle connexion. C'est a
 * l'appelant de la refermer, d'ou l'usage systematique du try-with-resources.
 */
@Component
public class DbConnection {

    private final String url;
    private final String user;
    private final String password;

    public DbConnection(
            @Value("${kofia.datasource.url}") String url,
            @Value("${kofia.datasource.username}") String user,
            @Value("${kofia.datasource.password}") String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
