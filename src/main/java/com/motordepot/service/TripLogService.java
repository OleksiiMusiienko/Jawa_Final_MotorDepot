package com.motordepot.service;

import com.motordepot.dao.TripRepository;
import com.motordepot.logging.PrintTrip;
import com.motordepot.model.Trip;
import com.motordepot.model.enums.TripStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class TripLogService {

    private final List<PrintTrip> printers;
    private final TripRepository tripRepository;

    @Autowired
    public TripLogService(List<PrintTrip> printers, TripRepository tripRepository) {
        this.printers = printers;
        this.tripRepository = tripRepository;
    }

    public void log(String data) {
        for (PrintTrip printer : printers) {
            printer.print(data);
        }
    }

    @Transactional(readOnly = true)
    public int printCompletedTrips() {
        List<Trip> trips = tripRepository.findAllByStatus(TripStatus.COMPLETED);

        if (trips.isEmpty()) {
            return 0;
        }

        StringBuilder text = new StringBuilder();
        String newLine = System.lineSeparator();

        text.append("ОТЧЁТ О ЗАВЕРШЁННЫХ РЕЙСАХ")
                .append(newLine);

        for (Trip trip : trips) {
            String line = String.format(
                    Locale.ROOT,
                    "Рейс №%d | Водитель: %s (ID=%d) "
                            + "| Автомобиль: %s | Назначение: %s "
                            + "| Груз: %s | Масса: %.2f кг "
                            + "| Стоимость: %.2f | Выплата: %.2f "
                            + "| Состояние после рейса: %s",
                    trip.getId(),
                    trip.getDriver().getFullName(),
                    trip.getDriver().getId(),
                    trip.getAuto().getRegistrationNumber(),
                    trip.getRequest().getDestination(),
                    trip.getRequest().getCargoType(),
                    trip.getRequest().getCargoWeightKg(),
                    trip.getTransportationCost(),
                    trip.getDriverPayment(),
                    trip.getAutoConditionAtCompletion()
            );

            text.append(line.replace('\n', ' ').replace('\r', ' ')).append(newLine);
        }

        text.append("Всего рейсов: ").append(trips.size());
        log(text.toString());
        return trips.size();
    }
}