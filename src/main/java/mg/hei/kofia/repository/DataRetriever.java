package mg.hei.kofia.repository;

import org.springframework.stereotype.Repository;
import mg.hei.kofia.db.DbConnection;
import mg.hei.kofia.entity.Driver;
import mg.hei.kofia.entity.Vehicle;
import mg.hei.kofia.entity.Trip;
import mg.hei.kofia.entity.enums.LicenseCategory;
import mg.hei.kofia.entity.enums.VehicleType;
import mg.hei.kofia.entity.enums.TripStatus;
import mg.hei.kofia.exception.BadRequestException;
import mg.hei.kofia.exception.NotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DataRetriever {

    private final DbConnection dbConnection;

    public DataRetriever(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Driver findDriverById(String id) {
        String sql = """
            SELECT id, name, license_category, affiliation_date
            FROM driver WHERE id = ?
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapDriver(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find driver: " + e.getMessage());
        }
    }

    public List<Driver> findAllDrivers() {
        String sql = """
            SELECT id, name, license_category, affiliation_date
            FROM driver ORDER BY id
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Driver> drivers = new ArrayList<>();
            while (rs.next()) {
                drivers.add(mapDriver(rs));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find drivers: " + e.getMessage());
        }
    }

    public Vehicle findVehicleById(String id) {
        String sql = """
            SELECT id, plate_number, type, capacity_tons
            FROM vehicle WHERE id = ?
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapVehicle(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find vehicle: " + e.getMessage());
        }
    }

    public Trip findTripById(String id) {
        String sql = """
            SELECT t.id, t.driver_id, t.vehicle_id, t.trip_date,
                   t.departure_city, t.arrival_city, t.distance_km,
                   t.billed_amount, t.status
            FROM trip t WHERE t.id = ?
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapTrip(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find trip: " + e.getMessage());
        }
    }

    public List<Trip> findAllTrips() {
        String sql = """
            SELECT t.id, t.driver_id, t.vehicle_id, t.trip_date,
                   t.departure_city, t.arrival_city, t.distance_km,
                   t.billed_amount, t.status
            FROM trip t ORDER BY t.trip_date
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Trip> trips = new ArrayList<>();
            while (rs.next()) {
                trips.add(mapTrip(rs));
            }
            return trips;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find trips: " + e.getMessage());
        }
    }

    public List<Trip> findTripsByDriver(String driverId) {
        String sql = """
            SELECT t.id, t.driver_id, t.vehicle_id, t.trip_date,
                   t.departure_city, t.arrival_city, t.distance_km,
                   t.billed_amount, t.status
            FROM trip t WHERE t.driver_id = ? ORDER BY t.trip_date
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Trip> trips = new ArrayList<>();
                while (rs.next()) {
                    trips.add(mapTrip(rs));
                }
                return trips;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find trips by driver: " + e.getMessage());
        }
    }

    public List<Trip> findTripsByPeriod(LocalDate from, LocalDate to) {
        String sql = """
            SELECT t.id, t.driver_id, t.vehicle_id, t.trip_date,
                   t.departure_city, t.arrival_city, t.distance_km,
                   t.billed_amount, t.status
            FROM trip t WHERE t.trip_date >= ? AND t.trip_date <= ? ORDER BY t.trip_date
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            try (ResultSet rs = stmt.executeQuery()) {
                List<Trip> trips = new ArrayList<>();
                while (rs.next()) {
                    trips.add(mapTrip(rs));
                }
                return trips;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find trips by period: " + e.getMessage());
        }
    }

    public List<Trip> findTripsByDriverAndPeriod(String driverId, LocalDate from, LocalDate to) {
        String sql = """
            SELECT t.id, t.driver_id, t.vehicle_id, t.trip_date,
                   t.departure_city, t.arrival_city, t.distance_km,
                   t.billed_amount, t.status
            FROM trip t WHERE t.driver_id = ? AND t.trip_date >= ? AND t.trip_date <= ?
            ORDER BY t.trip_date
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, driverId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));
            try (ResultSet rs = stmt.executeQuery()) {
                List<Trip> trips = new ArrayList<>();
                while (rs.next()) {
                    trips.add(mapTrip(rs));
                }
                return trips;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find trips by driver and period: " + e.getMessage());
        }
    }

    public Trip saveTrip(Trip trip) {
        String sql = """
            INSERT INTO trip (id, driver_id, vehicle_id, trip_date, departure_city,
                              arrival_city, distance_km, billed_amount, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                driver_id = EXCLUDED.driver_id,
                vehicle_id = EXCLUDED.vehicle_id,
                trip_date = EXCLUDED.trip_date,
                departure_city = EXCLUDED.departure_city,
                arrival_city = EXCLUDED.arrival_city,
                distance_km = EXCLUDED.distance_km,
                billed_amount = EXCLUDED.billed_amount,
                status = EXCLUDED.status
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trip.getId());
            stmt.setString(2, trip.getDriver().getId());
            stmt.setString(3, trip.getVehicle().getId());
            stmt.setDate(4, Date.valueOf(trip.getTripDate()));
            stmt.setString(5, trip.getDepartureCity());
            stmt.setString(6, trip.getArrivalCity());
            stmt.setInt(7, trip.getDistanceKm());
            stmt.setLong(8, trip.getBilledAmount());
            stmt.setString(9, trip.getStatus().name());
            stmt.executeUpdate();
            return trip;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save trip: " + e.getMessage());
        }
    }

    public Trip updateTripStatus(String tripId, TripStatus status) {
        String sql = """
            UPDATE trip SET status = ? WHERE id = ?
        """;
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, tripId);
            stmt.executeUpdate();
            return findTripById(tripId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update trip status: " + e.getMessage());
        }
    }

    private Driver mapDriver(ResultSet rs) throws SQLException {
        return Driver.builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .licenseCategory(LicenseCategory.valueOf(rs.getString("license_category")))
                .affiliationDate(rs.getDate("affiliation_date").toLocalDate())
                .build();
    }

    private Vehicle mapVehicle(ResultSet rs) throws SQLException {
        return Vehicle.builder()
                .id(rs.getString("id"))
                .plateNumber(rs.getString("plate_number"))
                .type(VehicleType.valueOf(rs.getString("type")))
                .capacityTons(rs.getBigDecimal("capacity_tons"))
                .build();
    }

    private Trip mapTrip(ResultSet rs) throws SQLException {
        Driver driver = findDriverById(rs.getString("driver_id"));
        Vehicle vehicle = findVehicleById(rs.getString("vehicle_id"));
        return Trip.builder()
                .id(rs.getString("id"))
                .driver(driver)
                .vehicle(vehicle)
                .tripDate(rs.getDate("trip_date").toLocalDate())
                .departureCity(rs.getString("departure_city"))
                .arrivalCity(rs.getString("arrival_city"))
                .distanceKm(rs.getInt("distance_km"))
                .billedAmount(rs.getLong("billed_amount"))
                .status(TripStatus.valueOf(rs.getString("status")))
                .build();
    }
}
