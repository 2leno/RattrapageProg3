package mg.hei.kofia.mapper;

import org.springframework.stereotype.Component;
import mg.hei.kofia.entity.Vehicle;
import mg.hei.kofia.entity.dto.VehicleSummary;

@Component
public class VehicleMapper {

    public VehicleSummary toVehicleSummary(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return VehicleSummary.builder()
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .build();
    }
}
