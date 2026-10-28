package com.motordepot.service;

import com.motordepot.model.TransportRequest;
import com.motordepot.model.enums.CargoType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RequestGeneratorService {

    private final  TransportRequestService transportRequestService;

    private static final List<String> DESTINATIONS = List.of(
            "Киев",
            "Львов",
            "Одесса"
    );

    @Autowired
    public RequestGeneratorService(TransportRequestService transportRequestService){
        this.transportRequestService = transportRequestService;
    }
    //генератор заявок
    @Transactional
    public List<TransportRequest> generateDailyRequests() {
        //пордготовка
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int requestCount = random.nextInt(1, 7);

        List<TransportRequest> requests = new ArrayList<>();

        CargoType[] cargoTypes = CargoType.values();

        //создание
        for (int i = 0; i < requestCount; i++) {
            String destination = DESTINATIONS.get(random.nextInt(DESTINATIONS.size()));

            CargoType cargoType = cargoTypes[random.nextInt(cargoTypes.length)];

            double cargoWeightKg = random.nextInt(5, 51) * 100.0;
            TransportRequest request = transportRequestService.create(destination, cargoWeightKg, cargoType);
            requests.add(request);
        }

        return requests;
    }

}
