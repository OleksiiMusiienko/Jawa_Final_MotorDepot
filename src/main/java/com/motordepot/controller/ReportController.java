package com.motordepot.controller;

import com.motordepot.service.TripLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UncheckedIOException;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final TripLogService tripLogService;

    @Autowired
    public ReportController(TripLogService tripLogService) {
        this.tripLogService = tripLogService;
    }

    @PostMapping("/trips")
    public String printTrips(RedirectAttributes redirectAttributes) {
        try {
            int count = tripLogService.printCompletedTrips();

            String message = count == 0 ? "Завершённых рейсов пока нет"  : "Отчёт напечатан в консоль и сохранён в trips.log "
                    + "Количество рейсов: " + count;

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    message
            );
        } catch (UncheckedIOException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Не удалось записать отчёт в файл. "
                            + "Вывод мог выполниться частично."
            );
        }

        return "redirect:/statistics";
    }
}