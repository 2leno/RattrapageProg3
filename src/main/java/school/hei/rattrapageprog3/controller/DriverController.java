package school.hei.rattrapageprog3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.hei.rattrapageprog3.entity.Driver;
import school.hei.rattrapageprog3.entity.Trip;
import school.hei.rattrapageprog3.dto.DriverRevenueResponseDTO;
import school.hei.rattrapageprog3.dto.DriverStatisticsResponseDTO;
import school.hei.rattrapageprog3.exception.BadRequestException;
import school.hei.rattrapageprog3.exception.NotFoundException;
import school.hei.rattrapageprog3.repository.DataRetriever;
import school.hei.rattrapageprog3.service.RevenueService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DriverController {

    private final DataRetriever dataRetriever;
    private final RevenueService revenueService;

    public DriverController(DataRetriever dataRetriever, RevenueService revenueService) {
        this.dataRetriever = dataRetriever;
        this.revenueService = revenueService;
    }

    @GetMapping("/drivers/{driverId}/revenue")
    public ResponseEntity<?> getDriverRevenue(
            @PathVariable String driverId,
            @RequestParam String from,
            @RequestParam String to) {
        try {
            Driver driver = dataRetriever.findDriverById(driverId);
            if (driver == null) {
                throw new NotFoundException("Driver not found: " + driverId);
            }

            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);
            long revenue = revenueService.computeDriverRevenue(driverId, fromDate, toDate);
            long fee = revenueService.computeCooperativeFee(driverId, fromDate, toDate);

            DriverRevenueResponseDTO response = DriverRevenueResponseDTO.builder()
                    .driverId(driver.getId())
                    .driverName(driver.getName())
                    .from(from)
                    .to(to)
                    .revenue(revenue)
                    .cooperativeFee(fee)
                    .build();
            return ResponseEntity.ok(response);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + ex.getMessage());
        }
    }

    @GetMapping("/drivers/{driverId}/statistics")
    public ResponseEntity<?> getDriverStatistics(@PathVariable String driverId) {
        try {
            Driver driver = dataRetriever.findDriverById(driverId);
            if (driver == null) {
                throw new NotFoundException("Driver not found: " + driverId);
            }

            List<Trip> trips = dataRetriever.findTripsByDriver(driverId);
            int completedTrips = 0;
            int totalDistanceKm = 0;
            for (Trip trip : trips) {
                if (trip.isBillable()) {
                    completedTrips++;
                    totalDistanceKm += trip.getDistanceKm();
                }
            }

            double avgPricePerKm = revenueService.computeAveragePricePerKm(driverId);

            DriverStatisticsResponseDTO response = DriverStatisticsResponseDTO.builder()
                    .driverId(driver.getId())
                    .driverName(driver.getName())
                    .completedTrips(completedTrips)
                    .totalDistanceKm(totalDistanceKm)
                    .averagePricePerKm(avgPricePerKm)
                    .build();
            return ResponseEntity.ok(response);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + ex.getMessage());
        }
    }
}
