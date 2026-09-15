package school.hei.rattrapageprog3.service;

import org.springframework.stereotype.Service;
import school.hei.rattrapageprog3.entity.Driver;
import school.hei.rattrapageprog3.entity.Trip;
import school.hei.rattrapageprog3.repository.DataRetriever;

import java.time.LocalDate;
import java.util.List;

@Service
public class RevenueService {

    private static final double COOPERATIVE_RATE = 0.15;

    private final DataRetriever dataRetriever;

    public RevenueService(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
    }

    public long computeDriverRevenue(String driverId, LocalDate from, LocalDate to) {
        List<Trip> trips = dataRetriever.findTripsByDriver(driverId);
        long revenue = 0;
        for (Trip trip : trips) {
            if (trip.isBillable()
                    && !trip.getTripDate().isBefore(from)
                    && !trip.getTripDate().isAfter(to)) {
                revenue += trip.getBilledAmount();
            }
        }
        return revenue;
    }

    public long computeCooperativeFee(String driverId, LocalDate from, LocalDate to) {
        long revenue = computeDriverRevenue(driverId, from, to);
        return (long) (revenue * COOPERATIVE_RATE);
    }

    public double computeAveragePricePerKm(String driverId) {
        List<Trip> trips = dataRetriever.findTripsByDriver(driverId);
        double totalPricePerKm = 0.0;
        int count = 0;
        for (Trip trip : trips) {
            if (trip.isBillable()) {
                totalPricePerKm += trip.pricePerKm();
                count++;
            }
        }
        if (count == 0) return 0.0;
        return totalPricePerKm / count;
    }

    public Driver findTopEarningDriver(LocalDate from, LocalDate to) {
        List<Driver> allDrivers = dataRetriever.findAllDrivers();
        Driver topDriver = null;
        long maxRevenue = -1;
        for (Driver driver : allDrivers) {
            long revenue = computeDriverRevenue(driver.getId(), from, to);
            if (revenue > maxRevenue) {
                maxRevenue = revenue;
                topDriver = driver;
            }
        }
        return topDriver;
    }
}
