package school.hei.rattrapageprog3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    private String id;
    private Driver driver;
    private Vehicle vehicle;
    private LocalDate tripDate;
    private String departureCity;
    private String arrivalCity;
    private int distanceKm;
    private long billedAmount;
    private TripStatus status;

    public boolean isBillable() {
        return TripStatus.COMPLETED.equals(this.status);
    }

    public double pricePerKm() {
        if (distanceKm == 0) return 0.0;
        return (double) billedAmount / distanceKm;
    }
}
