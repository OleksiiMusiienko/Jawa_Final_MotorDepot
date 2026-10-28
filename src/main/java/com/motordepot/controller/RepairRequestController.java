package com.motordepot.controller;

import com.motordepot.service.RepairRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/repairs")
public class RepairRequestController {

    private final RepairRequestService repairRequestService;

    @Autowired
    public RepairRequestController(
            RepairRequestService repairRequestService) {

        this.repairRequestService = repairRequestService;
    }

    @GetMapping
    public String showRepairs(Model model) {
        model.addAttribute(
                "repairs",
                repairRequestService.findAll()
        );

        return "repairs";
    }

    @PostMapping("/{id}/start")
    public String startRepair(
            @PathVariable("id") Integer repairId,
            RedirectAttributes redirectAttributes) {

        try {
            repairRequestService.startRepair(repairId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Ремонт по заявке №" + repairId + " начат"
            );
        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/repairs";
    }

    @PostMapping("/{id}/complete")
    public String completeRepair(
            @PathVariable("id") Integer repairId,
            RedirectAttributes redirectAttributes) {

        try {
            repairRequestService.completeRepair(repairId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Ремонт завершён. Автомобиль исправен. "
                            + "Если рейс приостановлен, возобновите его "
                            + "на странице рейсов."
            );
        } catch (IllegalArgumentException
                 | IllegalStateException
                 | NoSuchElementException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/repairs";
    }
}