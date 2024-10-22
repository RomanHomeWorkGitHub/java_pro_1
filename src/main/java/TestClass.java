import annotations.*;

public class TestClass {
    @BeforeSuite
    public static void setup() {
        System.out.println("Настройка перед набором тестов");
    }

    @BeforeTest
    public void beforeTest1() {
        System.out.println("Настройка 1 перед тестом");
    }

    @BeforeTest
    public void beforeTest2() {
        System.out.println("Настройка 2 перед тестом");
    }

    @AfterTest
    public void afterTest1() {
        System.out.println("Настройка 1 после теста");
    }

    @AfterTest
    public void afterTest2() {
        System.out.println("Настройка 2 после теста");
    }

    @Test(priority = 1)
    @CsvSource("10, Java, 20, true")
    public void testMethod(int a, String b, int c, boolean d) throws NoSuchMethodException {
        int priority = getClass()
                .getMethod("testMethod", int.class, String.class, int.class, boolean.class)
                .getAnnotation(Test.class).priority();
        System.out.println();
        System.out.println("Метод тестирования с приоритетом: " + priority + ", выполняемый с параметром: " + a + ", " + b + ", " + c + ", " + d);
    }

    @Test(priority = 2)
    public void anotherTest() throws NoSuchMethodException {
        int priority = getClass()
                .getMethod("anotherTest")
                .getAnnotation(Test.class).priority();
        System.out.println("Еще один тест выполнен без параметров с приоритетом: " + priority);
    }

    @Test(priority = 11)
    public void wrongPriorityTest() throws NoSuchMethodException {
        int priority = getClass()
                .getMethod("wrongPriorityTest")
                .getAnnotation(Test.class).priority();
        System.out.println("Метод тестирования с приоритетом: " + priority);
    }

    @AfterSuite
    public static void teardown() {
        System.out.println("Разборка после набора тестов");
    }
}
