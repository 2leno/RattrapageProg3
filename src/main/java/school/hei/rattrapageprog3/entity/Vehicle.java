package school.hei.rattrapageprog3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private String id;
    private String plateNumber;
    private VehicleType type;
    private double capacityTons;
}
