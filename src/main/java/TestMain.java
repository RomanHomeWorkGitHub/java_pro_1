import service.TestRunner;

public class TestMain {
    public static void main(String[] args) {
        try {
            new TestRunner().runTests(TestClass.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}