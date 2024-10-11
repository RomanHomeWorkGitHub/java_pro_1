import annotations.AfterSuite;
import annotations.BeforeSuite;
import annotations.CsvSource;
import annotations.Test;

public class TestClass {
    @BeforeSuite
    public static void setup() {
        System.out.println("Setup before the test suite");
    }

    @Test(priority = 1)
    @CsvSource("10, Java, 20, true")
    public void testMethod(int a, String b, int c, boolean d) {
        System.out.println("Test Method executed with params: " + a + ", " + b + ", " + c + ", " + d);
    }

    @Test(priority = 2)
    public void anotherTest() {
        System.out.println("Another Test executed");
    }

    @AfterSuite
    public static void teardown() {
        System.out.println("Teardown after the test suite");
    }
}
