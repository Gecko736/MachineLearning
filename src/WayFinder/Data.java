package WayFinder;

/**
 * Class stores the training data for the neural network
 */
public class Data {
    /**
     * Arrays are corresponding input and output
     */
    public final double[] input;
    public final double[] output;

    /**
     * Constructor assigns the input and output arrays to given arrays.
     *
     * @param input  input array
     * @param output output array
     */
    public Data(double[] input, double[] output) {
        this.input = input;
        this.output = output;
    }
}