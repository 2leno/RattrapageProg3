package mg.hei.kofia.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRevenueResponse {
    private String driverId;
    private String driverName;
    private String from;
    private String to;
    private long revenue;
    private long cooperativeFee;
}
