package mg.hei.kofia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mg.hei.kofia.entity.enums.VehicleType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private String id;
    private String plateNumber;
    private VehicleType type;
    private BigDecimal capacityTons;
}
