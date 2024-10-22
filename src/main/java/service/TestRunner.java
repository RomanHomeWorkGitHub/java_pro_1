package service;

import annotations.*;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class TestRunner {
    private Method beforeSuiteMethod;
    private Method afterSuiteMethod;
    private final List<Method> testMethods;
    private final List<Method> beforeTestMethods;
    private final List<Method> afterTestMethods;

    public TestRunner() {
        beforeSuiteMethod = null;
        afterSuiteMethod = null;
        testMethods = new ArrayList<>();
        beforeTestMethods = new ArrayList<>();
        afterTestMethods = new ArrayList<>();
    }

    @SneakyThrows
    public void runTests(Class<?> testClass) throws Exception {
        Object cls = testClass.getDeclaredConstructor().newInstance();
        // Формирование списка методов
        for (Method method : testClass.getDeclaredMethods()) {
            // Проверка методов @BeforeSuite и @AfterSuite
            if (Modifier.isStatic(method.getModifiers())) {
                checkStaticMethods(method);
            }
            // Проверка и формирование списка методов @Test
            checkTestMethods(method);
            // Проверка методов и формирование списка @BeforeTest и @AfterTest
            checkBeforeOrAfterTestMethods(method);
        }

        // Выполнение метода @BeforeSuite
        if (beforeSuiteMethod != null) {
            try {
                beforeSuiteMethod.invoke(null);
            } catch (Exception e) {
                throw new RuntimeException("Метод @BeforeSuite завершен с ошибкой: ", e);
            }
        }

        // Сортировка методов @Test по priority
        testMethods.sort(Comparator.comparingInt(m -> m.getAnnotation(Test.class).priority()));

        for (Method testMethod : testMethods) {
            if (!beforeTestMethods.isEmpty()) beforeTestMethods.forEach(m -> {
                try {
                    invokeTestMethod(cls, m);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            invokeTestMethod(cls, testMethod);
            if (!afterTestMethods.isEmpty()) afterTestMethods.forEach(m -> {
                try {
                    invokeTestMethod(cls, m);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // Выполнение метода @AfterSuite
        if (afterSuiteMethod != null) {
            try {
                afterSuiteMethod.invoke(null);
            } catch (Exception e) {
                throw new RuntimeException("Метод @AfterSuite завершен с ошибкой: ", e);
            }
        }
    }

    private void checkStaticMethods(Method method) {
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

    private void checkBeforeOrAfterTestMethods(Method method) {
        if (method.isAnnotationPresent(BeforeTest.class)) {
            beforeTestMethods.add(method);
        }
        if (method.isAnnotationPresent(AfterTest.class)) {
            afterTestMethods.add(method);
        }
    }

    private void checkTestMethods(Method method) {
        if (method.isAnnotationPresent(Test.class) && validatePriority(method)) {
            testMethods.add(method);
        }
    }


    private boolean validatePriority(Method method) {
        int value = method.getDeclaredAnnotation(Test.class).priority();
        if (value < 1 || value > 10) {
            System.out.println("Тестовый метод: " + method.getName() + " c приоритетом: " + value + " не выполнялся, т.к. значение поля priority должно быть между 1 и 10");
            return false;
        }
        return true;
    }

    private void invokeTestMethod(Object object, Method testMethod) throws Exception {
        // Параметры метода
        Parameter[] parameters = testMethod.getParameters();
        try {
            // Проверка на @CsvSource
            if (testMethod.isAnnotationPresent(CsvSource.class)) {
                String csv = testMethod.getAnnotation(CsvSource.class).value();
                String[] values = csv.split(", ");
                Object[] params = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    params[i] = convertValue(values[i], parameters[i].getType());
                }
                testMethod.invoke(object, params);
            } else {
                testMethod.invoke(object);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object convertValue(String value, Class<?> type) {
        if (type.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (type.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (type.equals(String.class)) {
            return value;
        }
        throw new IllegalArgumentException("Неподдерживаемый тип параметра");
    }

}