package ArcadeLearning;

public class ArrayLengthException extends Exception {
    public ArrayLengthException(String msg) {
        super(msg);
    }

    public ArrayLengthException(int expected, int given) {
        super("Expected:" + expected + " Given:" + given);
    }
}
