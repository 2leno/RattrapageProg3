package school.hei.rattrapageprog3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopEarningDriverResponseDTO {
    private String driverId;
    private String driverName;
    private long revenue;
}
