package school.hei.rattrapageprog3.repository;

import org.springframework.stereotype.Repository;
import school.hei.rattrapageprog3.entity.Driver;
import school.hei.rattrapageprog3.entity.Trip;
import school.hei.rattrapageprog3.entity.Vehicle;
import school.hei.rattrapageprog3.entity.LicenseCategory;
import school.hei.rattrapageprog3.entity.TripStatus;
import school.hei.rattrapageprog3.entity.VehicleType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DataRetriever {

    private final Connection connection;

    public DataRetriever(Connection connection) {
        this.connection = connection;
    }

    // ---- Driver ----

    public Driver findDriverById(String id) {
        String sql = "SELECT id, name, license_category, affiliation_date FROM driver WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT id, name, license_category, affiliation_date FROM driver ORDER BY id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<Driver> drivers = new ArrayList<>();
                while (rs.next()) {
                    drivers.add(mapDriver(rs));
                }
                return drivers;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find drivers: " + e.getMessage());
        }
    }

    // ---- Vehicle ----

    public Vehicle findVehicleById(String id) {
        String sql = "SELECT id, plate_number, type, capacity_tons FROM vehicle WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

    // ---- Trip ----

    public Trip findTripById(String id) {
        String sql = """
                SELECT t.id, t.driver_id, t.vehicle_id, t.trip_date, t.departure_city,
                       t.arrival_city, t.distance_km, t.billed_amount, t.status
                FROM trip t WHERE t.id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

    public List<Trip> findTripsByDriver(String driverId) {
        String sql = """
                SELECT t.id, t.driver_id, t.vehicle_id, t.trip_date, t.departure_city,
                       t.arrival_city, t.distance_km, t.billed_amount, t.status
                FROM trip t WHERE t.driver_id = ? ORDER BY t.trip_date
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
                SELECT t.id, t.driver_id, t.vehicle_id, t.trip_date, t.departure_city,
                       t.arrival_city, t.distance_km, t.billed_amount, t.status
                FROM trip t WHERE t.trip_date >= ? AND t.trip_date <= ? ORDER BY t.trip_date
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, from);
            stmt.setObject(2, to);
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

    public List<Trip> findAllTrips() {
        String sql = """
                SELECT t.id, t.driver_id, t.vehicle_id, t.trip_date, t.departure_city,
                       t.arrival_city, t.distance_km, t.billed_amount, t.status
                FROM trip t ORDER BY t.trip_date
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<Trip> trips = new ArrayList<>();
                while (rs.next()) {
                    trips.add(mapTrip(rs));
                }
                return trips;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all trips: " + e.getMessage());
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trip.getId());
            stmt.setString(2, trip.getDriver().getId());
            stmt.setString(3, trip.getVehicle().getId());
            stmt.setObject(4, trip.getTripDate());
            stmt.setString(5, trip.getDepartureCity());
            stmt.setString(6, trip.getArrivalCity());
            stmt.setInt(7, trip.getDistanceKm());
            stmt.setLong(8, trip.getBilledAmount());
            stmt.setString(9, trip.getStatus().name());
            stmt.executeUpdate();
            return findTripById(trip.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save trip: " + e.getMessage());
        }
    }

    public Trip updateTripStatus(String tripId, TripStatus status) {
        String sql = "UPDATE trip SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, tripId);
            stmt.executeUpdate();
            return findTripById(tripId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update trip status: " + e.getMessage());
        }
    }

    // ---- Mapping methods ----

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
                .capacityTons(rs.getDouble("capacity_tons"))
                .build();
    }

    private Trip mapTrip(ResultSet rs) throws SQLException {
        String driverId = rs.getString("driver_id");
        String vehicleId = rs.getString("vehicle_id");
        Driver driver = findDriverById(driverId);
        Vehicle vehicle = findVehicleById(vehicleId);

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
