package school.hei.rattrapageprog3.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
@AllArgsConstructor
public class HealthRepository {
    private final Connection connection;

    public boolean checkConnection() {
        String sql = "SELECT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            throw new RuntimeException("Database connection failed: " + e.getMessage());
        }
    }
}
