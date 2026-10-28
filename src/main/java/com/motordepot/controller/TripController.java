package com.motordepot.controller;

import com.motordepot.model.enums.AutoCondition;
import com.motordepot.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.motordepot.service.RepairRequestService;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final RepairRequestService repairRequestService;

    @Autowired
    public TripController(
            TripService tripService,
            RepairRequestService repairRequestService) {

        this.tripService = tripService;
        this.repairRequestService = repairRequestService;
    }
//возобновление рейса после ремонта
    @PostMapping("/{id}/resume")
    public String resumeTrip(
            @PathVariable("id") Integer tripId,
            RedirectAttributes redirectAttributes) {

        try {
            tripService.resumeTrip(tripId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Рейс №" + tripId + " возобновлён"
            );
        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/trips";
    }

    //заявка на ремонт во врем рнйса
    @PostMapping("/{id}/repair")
    public String requestRepair(
            @PathVariable("id") Integer tripId,
            @RequestParam("description") String description,
            RedirectAttributes redirectAttributes) {

        try {
            repairRequestService.createForTrip(tripId, description);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Заявка на ремонт создана. Рейс №"
                            + tripId + " приостановлен"
            );
        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/trips";
    }

    @GetMapping
    public String showTrips(Model model) {
        model.addAttribute("trips", tripService.findAll());
        return "trips";
    }

    @PostMapping("/{id}/start")
    public String startTrip(
            @PathVariable("id") Integer tripId,
            RedirectAttributes redirectAttributes) {

        try {
            tripService.startTrip(tripId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Рейс №" + tripId + " начат"
            );
        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/trips";
    }

    @PostMapping("/{id}/complete")
    public String completeTrip(
            @PathVariable("id") Integer tripId,
            @RequestParam("condition") AutoCondition condition,
            RedirectAttributes redirectAttributes) {

        try {
            tripService.completeTrip(tripId, condition);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Рейс №" + tripId + " завершён"
            );
        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/trips";
    }
}