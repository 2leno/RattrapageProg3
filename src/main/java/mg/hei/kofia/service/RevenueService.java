package mg.hei.kofia.service;

import org.springframework.stereotype.Service;
import mg.hei.kofia.entity.Driver;
import mg.hei.kofia.entity.Trip;
import mg.hei.kofia.entity.enums.TripStatus;
import mg.hei.kofia.repository.DataRetriever;

import java.time.LocalDate;
import java.util.List;

@Service
public class RevenueService {

    private static final long COOPERATIVE_FEE_PERCENTAGE = 15;

    private final DataRetriever dataRetriever;

    public RevenueService(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
    }

    public long computeDriverRevenue(String driverId, LocalDate from, LocalDate to) {
        List<Trip> trips = dataRetriever.findTripsByDriverAndPeriod(driverId, from, to);
        long revenue = 0;
        for (Trip trip : trips) {
            if (trip.isBillable()) {
                revenue += trip.getBilledAmount();
            }
        }
        return revenue;
    }

    public long computeCooperativeFee(String driverId, LocalDate from, LocalDate to) {
        long revenue = computeDriverRevenue(driverId, from, to);
        return revenue * COOPERATIVE_FEE_PERCENTAGE / 100;
    }

    public double computeAveragePricePerKm(String driverId) {
        List<Trip> trips = dataRetriever.findTripsByDriver(driverId);
        long totalRevenue = 0;
        int totalDistanceKm = 0;
        for (Trip trip : trips) {
            if (trip.isBillable()) {
                totalRevenue += trip.getBilledAmount();
                totalDistanceKm += trip.getDistanceKm();
            }
        }
        if (totalDistanceKm == 0) {
            return 0.0;
        }
        return (double) totalRevenue / totalDistanceKm;
    }

    public Driver findTopEarningDriver(LocalDate from, LocalDate to) {
        List<Driver> drivers = dataRetriever.findAllDrivers();
        Driver topDriver = null;
        long maxRevenue = 0;
        for (Driver driver : drivers) {
            long revenue = computeDriverRevenue(driver.getId(), from, to);
            if (revenue > maxRevenue) {
                maxRevenue = revenue;
                topDriver = driver;
            }
        }
        return topDriver;
    }
}
