package school.hei.rattrapageprog3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatisticsResponseDTO {
    private String driverId;
    private String driverName;
    private int completedTrips;
    private int totalDistanceKm;
    private double averagePricePerKm;
}
