package com.motordepot.dao;

import com.motordepot.model.Driver;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndReadDriver() {
        // водитель без идентификатор
        Driver driver = new Driver("Иван Петренко", 5);

        assertNull(driver.getId());

        // сохр в базу
        Driver savedDriver = driverRepository.saveAndFlush(driver);

        assertNotNull(savedDriver.getId());

        Integer driverId = savedDriver.getId();

        // Убираем объекты из контекста Hibernate,
        // чтобы следующее чтение действительно обратилось к базе.
        entityManager.clear();

        Driver foundDriver = driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new AssertionError("Водитель не найден в базе"));

        // Проверяем прочитанные значения.
        assertEquals(driverId, foundDriver.getId());
        assertEquals("Иван Петренко", foundDriver.getFullName());
        assertEquals(5, foundDriver.getExperienceYears());
    }
}