package com.motordepot.logging;

import org.springframework.stereotype.Component;

@Component
public class ConsolePrint implements PrintTrip {

    @Override
    public void print(String data) {
        System.out.println(data);
    }
}