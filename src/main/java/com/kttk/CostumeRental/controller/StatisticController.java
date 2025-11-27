package com.kttk.CostumeRental.controller;

import com.kttk.CostumeRental.DTO.RevenueReportItem;
import com.kttk.CostumeRental.pattern.strategy.CashFlowStrategy;
import com.kttk.CostumeRental.pattern.strategy.SalesStrategy;
import com.kttk.CostumeRental.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueReport(
            @RequestParam String type,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {

        try {
            if ("CASH_FLOW".equals(type)) {
                statisticService.setStrategy(new CashFlowStrategy());
            } else if ("SALES".equals(type)) {
                statisticService.setStrategy(new SalesStrategy());
            } else {
                return ResponseEntity.badRequest().body("Loại báo cáo không hợp lệ!");
            }

            List<RevenueReportItem> report = statisticService.generateReport(start, end);
            return ResponseEntity.ok(report);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }
}