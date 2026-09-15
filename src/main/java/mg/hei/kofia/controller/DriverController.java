package mg.hei.kofia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mg.hei.kofia.entity.Driver;
import mg.hei.kofia.entity.Trip;
import mg.hei.kofia.entity.dto.DriverRevenueResponse;
import mg.hei.kofia.entity.dto.DriverStatisticsResponse;
import mg.hei.kofia.exception.BadRequestException;
import mg.hei.kofia.exception.NotFoundException;
import mg.hei.kofia.mapper.DriverMapper;
import mg.hei.kofia.repository.DataRetriever;
import mg.hei.kofia.service.RevenueService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DriverController {

    private final DataRetriever dataRetriever;
    private final RevenueService revenueService;
    private final DriverMapper driverMapper;

    public DriverController(DataRetriever dataRetriever, RevenueService revenueService, DriverMapper driverMapper) {
        this.dataRetriever = dataRetriever;
        this.revenueService = revenueService;
        this.driverMapper = driverMapper;
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

            DriverRevenueResponse response = driverMapper.toDriverRevenueResponse(driver, from, to, revenue, fee);
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

            DriverStatisticsResponse response = driverMapper.toDriverStatisticsResponse(
                    driver, completedTrips, totalDistanceKm, avgPricePerKm);
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
