package mg.hei.kofia.mapper;

import org.springframework.stereotype.Component;
import mg.hei.kofia.entity.Driver;
import mg.hei.kofia.entity.dto.DriverSummary;
import mg.hei.kofia.entity.dto.DriverRevenueResponse;
import mg.hei.kofia.entity.dto.DriverStatisticsResponse;

@Component
public class DriverMapper {

    public DriverSummary toDriverSummary(Driver driver) {
        if (driver == null) {
            return null;
        }
        return DriverSummary.builder()
                .id(driver.getId())
                .name(driver.getName())
                .build();
    }

    public DriverRevenueResponse toDriverRevenueResponse(Driver driver, String from, String to, long revenue, long cooperativeFee) {
        if (driver == null) {
            return null;
        }
        return DriverRevenueResponse.builder()
                .driverId(driver.getId())
                .driverName(driver.getName())
                .from(from)
                .to(to)
                .revenue(revenue)
                .cooperativeFee(cooperativeFee)
                .build();
    }

    public DriverStatisticsResponse toDriverStatisticsResponse(Driver driver, int completedTrips, int totalDistanceKm, double averagePricePerKm) {
        if (driver == null) {
            return null;
        }
        return DriverStatisticsResponse.builder()
                .driverId(driver.getId())
                .driverName(driver.getName())
                .completedTrips(completedTrips)
                .totalDistanceKm(totalDistanceKm)
                .averagePricePerKm(averagePricePerKm)
                .build();
    }
}
