package com.motordepot.logging;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Component
public class FilePrint implements PrintTrip {

    private static final String FILE_NAME = "trips.log";

    @Override
    public synchronized void print(String message) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(FILE_NAME, true),
                        StandardCharsets.UTF_8
                )
        )) {
            writer.write(message);
            writer.newLine();
        } catch (IOException exception) {
            throw new UncheckedIOException("Не удалось записать рейс в файл " + FILE_NAME, exception);
        }
    }
}
