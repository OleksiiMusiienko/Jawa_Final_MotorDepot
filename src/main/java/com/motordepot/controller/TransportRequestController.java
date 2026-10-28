package com.motordepot.controller;

import com.motordepot.model.Trip;
import com.motordepot.service.DispatcherService;
import com.motordepot.service.RequestGeneratorService;
import com.motordepot.service.TransportRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/requests")
public class TransportRequestController {

    private final TransportRequestService transportRequestService;
    private final RequestGeneratorService requestGeneratorService;
    private final DispatcherService dispatcherService;

    @Autowired
    public TransportRequestController(
            TransportRequestService transportRequestService,
            RequestGeneratorService requestGeneratorService,
            DispatcherService dispatcherService) {

        this.transportRequestService = transportRequestService;
        this.requestGeneratorService = requestGeneratorService;
        this.dispatcherService = dispatcherService;
    }

    @GetMapping
    public String showRequests(Model model) {
        model.addAttribute(
                "requests",
                transportRequestService.findAll()
        );

        return "requests";
    }

    @PostMapping("/generate")
    public String generateRequests() {
        requestGeneratorService.generateDailyRequests();

        return "redirect:/requests";
    }

    //назначение рейса для заявки
    @PostMapping("/{id}/assign")
    public String assignTrip(
            @PathVariable("id") Integer requestId,
            @RequestParam("transportationCost") double transportationCost,
            RedirectAttributes redirectAttributes) {

        try {
            Trip trip = dispatcherService.assignTrip(
                    requestId,
                    transportationCost
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Создан рейс №" + trip.getId()
            );
        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/requests";
    }
}