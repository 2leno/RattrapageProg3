package mg.hei.kofia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mg.hei.kofia.entity.enums.TripStatus;

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
        return status == TripStatus.COMPLETED;
    }
}
