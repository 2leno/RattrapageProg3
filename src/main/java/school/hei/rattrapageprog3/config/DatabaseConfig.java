package school.hei.rattrapageprog3.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class DatabaseConfig {

    @Bean
    public Connection connection() {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("JDBC_URL");
        String username = dotenv.get("JDBC_USERNAME");
        String password = dotenv.get("JDBC_PASSWORD");
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage());
        }
    }
}
