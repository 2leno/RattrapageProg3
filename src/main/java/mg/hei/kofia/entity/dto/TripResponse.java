package mg.hei.kofia.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    private String id;
    private DriverSummary driver;
    private VehicleSummary vehicle;
    private String tripDate;
    private String departureCity;
    private String arrivalCity;
    private int distanceKm;
    private long billedAmount;
    private String status;
}
