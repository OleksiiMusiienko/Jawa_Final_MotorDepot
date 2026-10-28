package com.motordepot.controller;

import com.motordepot.model.Driver;
import com.motordepot.service.DriverService;
import com.motordepot.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final DriverService driverService;

    @Autowired
    public StatisticsController(
            StatisticsService statisticsService,
            DriverService driverService) {

        this.statisticsService = statisticsService;
        this.driverService = driverService;
    }

    @GetMapping
    public String showStatistics(Model model) {
        model.addAttribute("drivers", driverService.findAll());

        model.addAttribute(
                "tripCounts",
                statisticsService.getTripCountByDriver()
        );

        model.addAttribute(
                "cargoWeights",
                statisticsService.getCargoWeightByDriver()
        );

        model.addAttribute(
                "earnings",
                statisticsService.getEarningsByDriver()
        );

        model.addAttribute(
                "destinationCounts",
                statisticsService.getTripCountByDestination()
        );

        model.addAttribute(
                "destinationWeights",
                statisticsService.getCargoWeightByDestination()
        );

        Driver topDriver = statisticsService.getTopEarningDriver()
                .orElse(null);

        model.addAttribute("topDriver", topDriver);

        return "statistics";
    }
}