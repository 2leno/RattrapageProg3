package mg.hei.kofia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mg.hei.kofia.entity.Driver;
import mg.hei.kofia.entity.Trip;
import mg.hei.kofia.entity.Vehicle;
import mg.hei.kofia.entity.dto.*;
import mg.hei.kofia.entity.enums.TripStatus;
import mg.hei.kofia.exception.BadRequestException;
import mg.hei.kofia.exception.NotFoundException;
import mg.hei.kofia.mapper.TripMapper;
import mg.hei.kofia.repository.DataRetriever;
import mg.hei.kofia.service.RevenueService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TripController {

    private final DataRetriever dataRetriever;
    private final TripMapper tripMapper;
    private final RevenueService revenueService;

    public TripController(DataRetriever dataRetriever, TripMapper tripMapper, RevenueService revenueService) {
        this.dataRetriever = dataRetriever;
        this.tripMapper = tripMapper;
        this.revenueService = revenueService;
    }

    @GetMapping("/trips")
    public ResponseEntity<?> getTrips(
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        try {
            List<Trip> trips;
            if (driverId != null && from != null && to != null) {
                LocalDate fromDate = LocalDate.parse(from);
                LocalDate toDate = LocalDate.parse(to);
                trips = dataRetriever.findTripsByDriverAndPeriod(driverId, fromDate, toDate);
            } else if (driverId != null) {
                trips = dataRetriever.findTripsByDriver(driverId);
            } else if (from != null && to != null) {
                LocalDate fromDate = LocalDate.parse(from);
                LocalDate toDate = LocalDate.parse(to);
                trips = dataRetriever.findTripsByPeriod(fromDate, toDate);
            } else {
                trips = dataRetriever.findAllTrips();
            }

            List<TripResponse> responses = new ArrayList<>();
            for (Trip trip : trips) {
                responses.add(tripMapper.toTripResponse(trip));
            }
            return ResponseEntity.ok(responses);
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + ex.getMessage());
        }
    }

    @PutMapping("/trips/{tripId}")
    public ResponseEntity<?> createOrReplaceTrip(@PathVariable String tripId, @RequestBody CreateTrip body) {
        try {
            Driver driver = dataRetriever.findDriverById(body.getDriverId());
            if (driver == null) {
                throw new NotFoundException("Driver not found: " + body.getDriverId());
            }
            Vehicle vehicle = dataRetriever.findVehicleById(body.getVehicleId());
            if (vehicle == null) {
                throw new NotFoundException("Vehicle not found: " + body.getVehicleId());
            }

            Trip existing = dataRetriever.findTripById(tripId);
            boolean isCreation = (existing == null);

            Trip trip = Trip.builder()
                    .id(tripId)
                    .driver(driver)
                    .vehicle(vehicle)
                    .tripDate(LocalDate.parse(body.getTripDate()))
                    .departureCity(body.getDepartureCity())
                    .arrivalCity(body.getArrivalCity())
                    .distanceKm(body.getDistanceKm())
                    .billedAmount(body.getBilledAmount())
                    .status(TripStatus.COMPLETED)
                    .build();

            dataRetriever.saveTrip(trip);
            Trip saved = dataRetriever.findTripById(tripId);

            if (isCreation) {
                return ResponseEntity.status(HttpStatus.CREATED).body(tripMapper.toTripResponse(saved));
            } else {
                return ResponseEntity.ok(tripMapper.toTripResponse(saved));
            }
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + ex.getMessage());
        }
    }

    @PutMapping("/trips/{tripId}/status")
    public ResponseEntity<?> updateTripStatus(@PathVariable String tripId, @RequestBody UpdateTripStatus body) {
        try {
            Trip existing = dataRetriever.findTripById(tripId);
            if (existing == null) {
                throw new NotFoundException("Trip not found: " + tripId);
            }

            TripStatus status = TripStatus.valueOf(body.getStatus());
            Trip updated = dataRetriever.updateTripStatus(tripId, status);
            return ResponseEntity.ok(tripMapper.toTripResponse(updated));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status: " + body.getStatus());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + ex.getMessage());
        }
    }
}
