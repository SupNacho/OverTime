package ru.supernacho.overtime.utils;

import java.text.DecimalFormat;
import java.util.Random;

public class PinGenerator {
    public static String getEmplPin(){
        Random generator = new Random();
        DecimalFormat formatter = new DecimalFormat("000000");
        generator.setSeed(System.currentTimeMillis());
        int pin = generator.nextInt(1000000) % 1000000;
        return formatter.format(pin);
    }

    public static String getAdminPin(){
        Random generator = new Random();
        DecimalFormat formatter = new DecimalFormat("000000");
        generator.setSeed(System.currentTimeMillis());
        int pin = generator.nextInt(1000000) % 1000000;
        return formatter.format(pin);
    }
}
