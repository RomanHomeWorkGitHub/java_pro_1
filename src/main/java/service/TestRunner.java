package service;

import annotations.AfterSuite;
import annotations.BeforeSuite;
import annotations.CsvSource;
import annotations.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;

public class TestRunner {
    public static void runTests(Class<?> testClass) throws Exception {
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;

        // Проверка методов @BeforeSuite и @AfterSuite
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (beforeSuiteMethod != null) {
                    throw new IllegalStateException("Обнаружено более одного метода @BeforeSuite.");
                }
                beforeSuiteMethod = method;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                if (afterSuiteMethod != null) {
                    throw new IllegalStateException("Обнаружено более одного метода @AfterSuite.");
                }
                afterSuiteMethod = method;
            }
        }

        // Выполнение метода @BeforeSuite
        if (beforeSuiteMethod != null) {
            beforeSuiteMethod.invoke(null);
        }

        // Выполнение методов @Test
        Method[] testMethods = testClass.getDeclaredMethods();
        Arrays.sort(testMethods, Comparator.comparingInt(m -> m.getAnnotation(Test.class).priority()));

        for (Method testMethod : testMethods) {
            if (testMethod.isAnnotationPresent(Test.class)) {
                invokeTestMethod(testMethod);
            }
        }

        // Выполнение метода @AfterSuite
        if (afterSuiteMethod != null) {
            afterSuiteMethod.invoke(null);
        }
    }

    private static void invokeTestMethod(Method testMethod) throws Exception {
        // Параметры метода
        Parameter[] parameters = testMethod.getParameters();

        // Проверка на @CsvSource
        if (testMethod.isAnnotationPresent(CsvSource.class)) {
            String csv = testMethod.getAnnotation(CsvSource.class).value();
            String[] values = csv.split(", ");
            Object[] params = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                params[i] = convertValue(values[i], parameters[i].getType());
            }
            testMethod.invoke(null, params);
        } else {
            testMethod.invoke(null);
        }
    }

    private static Object convertValue(String value, Class<?> type) {
        if (type.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (type.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (type.equals(String.class)) {
            return value;
        }
        throw new IllegalArgumentException("Unsupported parameter type");
    }
}
