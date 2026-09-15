package mg.hei.kofia.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopEarningDriverResponse {
    private String driverId;
    private String driverName;
    private long revenue;
}
