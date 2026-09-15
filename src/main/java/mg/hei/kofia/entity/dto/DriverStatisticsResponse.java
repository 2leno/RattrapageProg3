package mg.hei.kofia.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatisticsResponse {
    private String driverId;
    private String driverName;
    private int completedTrips;
    private int totalDistanceKm;
    private double averagePricePerKm;
}
