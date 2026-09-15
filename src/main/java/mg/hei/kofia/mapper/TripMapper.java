package mg.hei.kofia.mapper;

import org.springframework.stereotype.Component;
import mg.hei.kofia.entity.Trip;
import mg.hei.kofia.entity.dto.TripResponse;

@Component
public class TripMapper {
    private final DriverMapper driverMapper;
    private final VehicleMapper vehicleMapper;

    public TripMapper(DriverMapper driverMapper, VehicleMapper vehicleMapper) {
        this.driverMapper = driverMapper;
        this.vehicleMapper = vehicleMapper;
    }

    public TripResponse toTripResponse(Trip trip) {
        if (trip == null) {
            return null;
        }
        return TripResponse.builder()
                .id(trip.getId())
                .driver(driverMapper.toDriverSummary(trip.getDriver()))
                .vehicle(vehicleMapper.toVehicleSummary(trip.getVehicle()))
                .tripDate(trip.getTripDate().toString())
                .departureCity(trip.getDepartureCity())
                .arrivalCity(trip.getArrivalCity())
                .distanceKm(trip.getDistanceKm())
                .billedAmount(trip.getBilledAmount())
                .status(trip.getStatus().name())
                .build();
    }
}
