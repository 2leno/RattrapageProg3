package school.hei.rattrapageprog3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.hei.rattrapageprog3.entity.Driver;
import school.hei.rattrapageprog3.dto.TopEarningDriverResponseDTO;
import school.hei.rattrapageprog3.exception.BadRequestException;
import school.hei.rattrapageprog3.exception.NotFoundException;
import school.hei.rattrapageprog3.service.RevenueService;

import java.time.LocalDate;

@RestController
public class StatisticsController {

    private final RevenueService revenueService;

    public StatisticsController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/statistics/top-earning-driver")
    public ResponseEntity<?> getTopEarningDriver(
            @RequestParam String from,
            @RequestParam String to) {
        try {
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);

            Driver topDriver = revenueService.findTopEarningDriver(fromDate, toDate);
            if (topDriver == null) {
                throw new NotFoundException("No driver found for the given period");
            }

            long revenue = revenueService.computeDriverRevenue(topDriver.getId(), fromDate, toDate);

            TopEarningDriverResponseDTO response = TopEarningDriverResponseDTO.builder()
                    .driverId(topDriver.getId())
                    .driverName(topDriver.getName())
                    .revenue(revenue)
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
