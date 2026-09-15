package school.hei.rattrapageprog3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.hei.rattrapageprog3.entity.Driver;
import school.hei.rattrapageprog3.entity.Trip;
import school.hei.rattrapageprog3.entity.Vehicle;
import school.hei.rattrapageprog3.dto.*;
import school.hei.rattrapageprog3.entity.TripStatus;
import school.hei.rattrapageprog3.exception.BadRequestException;
import school.hei.rattrapageprog3.exception.NotFoundException;
import school.hei.rattrapageprog3.repository.DataRetriever;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TripController {

    private final DataRetriever dataRetriever;

    public TripController(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
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
                List<Trip> allTrips = dataRetriever.findTripsByDriver(driverId);
                trips = new ArrayList<>();
                for (Trip trip : allTrips) {
                    if (!trip.getTripDate().isBefore(fromDate) && !trip.getTripDate().isAfter(toDate)) {
                        trips.add(trip);
                    }
                }
            } else if (driverId != null) {
                trips = dataRetriever.findTripsByDriver(driverId);
            } else if (from != null && to != null) {
                LocalDate fromDate = LocalDate.parse(from);
                LocalDate toDate = LocalDate.parse(to);
                trips = dataRetriever.findTripsByPeriod(fromDate, toDate);
            } else {
                trips = dataRetriever.findAllTrips();
            }

            List<TripResponseDTO> responses = new ArrayList<>();
            for (Trip trip : trips) {
                responses.add(toTripResponse(trip));
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
    public ResponseEntity<?> createOrReplaceTrip(@PathVariable String tripId, @RequestBody CreateTripDTO body) {
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
                return ResponseEntity.status(HttpStatus.CREATED).body(toTripResponse(saved));
            } else {
                return ResponseEntity.ok(toTripResponse(saved));
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
    public ResponseEntity<?> updateTripStatus(@PathVariable String tripId, @RequestBody UpdateTripStatusDTO body) {
        try {
            Trip existing = dataRetriever.findTripById(tripId);
            if (existing == null) {
                throw new NotFoundException("Trip not found: " + tripId);
            }

            TripStatus status = TripStatus.valueOf(body.getStatus());
            Trip updated = dataRetriever.updateTripStatus(tripId, status);
            return ResponseEntity.ok(toTripResponse(updated));
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

    private TripResponseDTO toTripResponse(Trip trip) {
        return TripResponseDTO.builder()
                .id(trip.getId())
                .driver(DriverSummaryDTO.builder()
                        .id(trip.getDriver().getId())
                        .name(trip.getDriver().getName())
                        .build())
                .vehicle(VehicleSummaryDTO.builder()
                        .id(trip.getVehicle().getId())
                        .plateNumber(trip.getVehicle().getPlateNumber())
                        .build())
                .tripDate(trip.getTripDate().toString())
                .departureCity(trip.getDepartureCity())
                .arrivalCity(trip.getArrivalCity())
                .distanceKm(trip.getDistanceKm())
                .billedAmount(trip.getBilledAmount())
                .status(trip.getStatus().name())
                .build();
    }
}
