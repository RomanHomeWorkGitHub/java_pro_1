package service;

import annotations.Test;

import java.lang.reflect.Field;

public class Validator {
    public static void validate(int value) throws IllegalAccessException {
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("Значение поля priority должно быть между 1 и 10");
        }
    }
}