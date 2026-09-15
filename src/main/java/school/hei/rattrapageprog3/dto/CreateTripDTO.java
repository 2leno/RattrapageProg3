package school.hei.rattrapageprog3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTripDTO {
    private String driverId;
    private String vehicleId;
    private String tripDate;
    private String departureCity;
    private String arrivalCity;
    private int distanceKm;
    private long billedAmount;
}
