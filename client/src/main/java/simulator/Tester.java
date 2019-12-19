package simulator;

public class Tester {
    public static void main(String[] args) {
        new Tester("Does this bow");
        System.out.println(System.getenv("DATASET"));
    }

    private String value;
    Tester(String  value) {
        this.value = value;

        printValue();
    }

    private void printValue() {
        System.out.println(value);
    }
}