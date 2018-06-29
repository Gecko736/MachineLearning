package WayFinder;

/**
 * Class holds a functioning neural network.
 *
 * @author Ben Rovik
 *
 */
public class Brain {
    public static Brain population(int max, int min, int beginMax, int beginMin,
                                   int generations, int generationSize, int[] layers, Data[] data) {
        double oldMax = MAX;
        double oldMin = MIN;
        MAX = max;
        MIN = min;

        Brain[] population = new Brain[generationSize];
        for (int i = 0; i < generationSize; i++)
            population[i] = new Brain(layers, beginMax, beginMin);

        double range = beginMax - beginMin;

        for (int j = 0; j < generations; j++) {
            System.out.println("testing generation " + j);
            double bestCost = -1;
            int bestNeurd = 0;
            double currentCost;

            for (int i = 0; i < population.length; i++) {
                System.out.print(".");
                currentCost = population[i].clone().learn(data);
                if (currentCost < bestCost || bestCost == -1) {
                    bestNeurd = i;
                    bestCost = currentCost;
                }
            }
            System.out.println();
            range = Math.sqrt(range + 1) - 1;

            for (int i = 0; i < bestNeurd; i++)
                population[i] = population[bestNeurd].child(range);

            for (int i = bestNeurd + 1; i < generationSize; i++)
                population[i] = population[bestNeurd].child(range);
        }

        System.out.println("final generation learning");

        double bestCost = -1;
        int bestNeurd = 0;
        double currentCost;
        for (int i = 0; i < generationSize; i++) {
            System.out.print(".");
            currentCost = population[i].learn(data);
            if (currentCost < bestCost || bestCost == -1) {
                bestNeurd = i;
                bestCost = currentCost;
            }
        }
        System.out.println();

        MAX = oldMax;
        MIN = oldMin;
        return population[bestNeurd];
    }

    /**
     * Method creates a population of Brains, trains them all against training
     * data, and return the one that learned it with the lowest cost.
     *
     * @param max    maximum value for calculation
     * @param min    minimum value for calculation
     * @param size   population size
     * @param layers passed into the Brain constructor
     * @param data   training data
     * @return the Brain that best learned the training data
     */
    public static Brain best(int max, int min, int size, int[] layers, Data[] data) {
        double oldMax = MAX;
        double oldMin = MIN;
        MAX = max;
        MIN = min;

        double bestCost = -1;
        Brain bestNeurd = null;

        double currentCost;
        Brain currentNeurd;

        for (int i = 0; i < size; i++) {
            currentNeurd = new Brain(layers);
            currentCost = currentNeurd.learn(data);
            if (currentCost < bestCost || bestCost == -1) {
                bestNeurd = currentNeurd;
                bestCost = currentCost;
            }
        }
        MAX = oldMax;
        MIN = oldMin;
        return bestNeurd;
    }

    /**
     * Array stores the layers of neurons for this neural network.
     */
    private Layer[] layers;

    /**
     * Doubles are the maximum and minimum values of the output values of the
     * neural network. Using these as upper and lower bounds on the activation
     * values of all the neurons increases the learning rate.
     * <p>
     * For individual Brain instantiation, they must be set manually.
     */
    public static double MAX;
    public static double MIN;

    /**
     * Array stores the constructor input for use in #clone()
     */
    private int[] layerguide;

    /**
     * Constructor takes an integer array, and for each integer in the given
     * array, creates a layer of that length. Also instantiates the matrix
     * array for backpropagation.
     * <p>
     * IMPORTANT:
     * <p>The first number in the given array is the number of inputs for this
     * network and does not get a layer of neurons. The last number in the
     * given array is the number of outputs.</p>
     *
     * @param layers the number of inputs for this network followed by the
     *               number of neurons for each layer
     */
    public Brain(int[] layers) {
        this.layers = new Layer[layers.length - 1];
        matrix = new double[layers.length][];
        matrix[0] = new double[layers[0]];
        for (int i = 1; i < layers.length; i++) {
            this.layers[i - 1] = new Layer(layers[i - 1], layers[i]);
            matrix[i] = new double[layers[i]];
        }
        layerguide = layers;
    }

    /**
     * Constructor takes an integer array, and for each integer in the given
     * array, creates a layer of that length. Also instantiates the matrix
     * array for backpropagation. The weights of each neuron are set to a
     * random double within a given range.
     *
     * @param layers the number of inputs for this network followed by the
     *               number of neurons for each layer
     * @param max upper limit of weight range
     * @param min lower limit of weight range
     */
    public Brain(int[] layers, int max, int min) {
        this.layers = new Layer[layers.length - 1];
        matrix = new double[layers.length][];
        matrix[0] = new double[layers[0]];
        for (int i = 1; i < layers.length; i++) {
            this.layers[i - 1] = new Layer(layers[i - 1], layers[i], max, min);
            matrix[i] = new double[layers[i]];
        }
        layerguide = layers;
    }

    /**
     * Method takes a double array and feeds it through the layers starting at
     * index 0 and moving up.
     *
     * @param input input values
     * @return output values
     */
    public double[] output(double[] input) {
        double[] output = input;
        for (Layer l : layers)
            output = l.acts(output);
        return output;
    }

    /**
     * Method prints every weight and bias in the network.
     * Exists for debugging purposes.
     */
    public void print() {
        for (int x = 0; x < layers.length; x++) {
            System.out.println("Layer " + x);
            for (int y = 0; y < layers[x].neurons.length; y++) {
                System.out.print("\tneuron " + y + ":");
                for (int z = 0; z < layers[x].neurons[y].weights.length; z++)
                    System.out.printf(" %-20s", layers[x].neurons[y].weights[z]);
                System.out.println();
            }
        }
    }

    /**
     * Method prints the activation values for each node in the network given
     * an input.
     * Exists for debugging purposes.
     *
     * @param input input values into the network
     */
    public void print(double[] input) {
        System.out.print("Input:");
        for (double d : input)
            System.out.printf(" %-20s", d);
        System.out.println();

        for (int x = 0; x < layers.length; x++) {
            System.out.print("Layer " + x + ":");
            for (int y = 0; y < layers[x].neurons.length; y++)
                System.out.printf(" neuron %d: %-20s", y, layers[x].neurons[y].act(input));
            System.out.println();
            input = layers[x].acts(input);
        }
    }

    /**
     * Method backpropagates the neural network against the given training data
     * a given number of times.
     *
     * @param data  training data
     * @param times number of backpropagations to perform
     */
    public void study(Data[] data, int times) {
        for (int i = 0; i < times; i++)
            backpropagate(data);
    }

    /**
     * Method backpropagates the neural network until the the cost value of
     * the network's output begins to increase.
     *
     * @param data training data
     * @return the final cost value
     */
    public double learn(Data[] data) {
        boolean go;
        double lastCost = -1;
        do {
            double cost = backpropagate(data);
            go = cost < lastCost || lastCost == -1;
            lastCost = cost;
        } while (go);
        return lastCost;
    }

    /**
     * 2D array holds the activations of each neuron in the neural network.
     * <p>
     * Only exists for the methods involved in backpropagation and is set to
     * null after the process is finished.
     */
    private double[][] matrix;

    /**
     * 4D array holds the cost differential of each weight and bias according
     * to each data of the given training data.
     * <p>
     * Only exists for the methods involved in backpropagation and is set to
     * null after the process is finished.
     */
    private double[][][][] allCostDiffs;

    /**
     * Method makes one step to the neural network in the cost differential
     * gradient space.
     *
     * @param data training data
     * @return the step vector, returned for consideration in
     * #learn(Data[], double)
     */
    private double backpropagate(Data[] data) {
        // loop instantiates allCostDiffs to the correct size
        allCostDiffs = new double[data.length][][][];
        for (int i = 0; i < data.length; i++) {
            allCostDiffs[i] = new double[layers.length][][];
            for (int x = 0; x < layers.length; x++) {
                allCostDiffs[i][x] = new double[layers[x].neurons.length][];
                for (int y = 0; y < layers[x].neurons.length; y++)
                    allCostDiffs[i][x][y] = new double[layers[x].neurons[y].weights.length];
            }
        }

        // loop instantiates the step vector to the correct size
        double[][][] step = new double[layers.length][][];
        for (int x = 0; x < layers.length; x++) {
            step[x] = new double[layers[x].neurons.length][];
            for (int y = 0; y < layers[x].neurons.length; y++)
                step[x][y] = new double[layers[x].neurons[y].weights.length];
        }

        // loop fills the step vector with the sums of all the cost
        // differentials corresponding to the given training data
        for (int i = 0; i < data.length; i++) {
            matrix[0] = data[i].input;
            for (int x = 1; x < layers.length; x++)
                layers[x].setActs(x);

            setCostDiffs(data[i].output, i);
            double[][][] here = allCostDiffs[i];
            for (int x = 0; x < here.length; x++)
                for (int y = 0; y < here[x].length; y++)
                    for (int z = 0; z < here[x][y].length; z++)
                        step[x][y][z] += here[x][y][z];
        }

        // loop turns the sums in the step vector into averages
        for (int x = 0; x < step.length; x++)
            for (int y = 0; y < step[x].length; y++)
                for (int z = 0; z < step[x][y].length; z++)
                    step[x][y][z] /= data.length;

        // loop calculates the magnitude of the step vector
        double size = 0;
        for (double[][] layer : step)
            for (double[] neuron : layer)
                for (double weight : neuron)
                    size += Math.pow(weight, 2);
        size = Math.sqrt(size);

        double cost = cost(data);

        // loop makes the step
        for (int x = 0; x < layers.length; x++)
            for (int y = 0; y < layers[x].neurons.length; y++)
                for (int z = 0; z < layers[x].neurons[y].weights.length; z++)
                    layers[x].neurons[y].weights[z] += step[x][y][z] * Math.pow(cost, 2) / (Math.pow(size, 2) * 4665);

        allCostDiffs = null;
        return cost;
    }

    /**
     * Method calculates and returns the cost value of this neural network
     * given an array of training data.
     * <p>
     * Cost = the average of {
     * <t>the sums of {
     * <t><t>the squares of { the differences between {
     * <t><t><t>the real output and the desired output</t></t></t>
     * </t></t>}} for each corresponding index of the two
     * </t>} for each Data
     * } across the given Data[]
     * <p>
     * in other words:
     * <p>
     * Cost =
     * sum (from i = 0 to Data[].length - 1) of {
     * <t>sum (from j = 0 to Data[i].output.length - 1) of {
     * <t><t>(this.output(Data[i].input)[j] - Data[i].output[j]) ^ 2</t></t>
     * </t>}
     * } / Data[].length
     *
     * @param data training data set
     * @return cost value defined above
     */
    public double cost(Data[] data) {
        double sum = 0;
        for (Data d : data) {
            double sum2 = 0;
            double[] output = output(d.input);
            for (int i = 0; i < output.length; i++)
                sum2 += Math.pow(output[i] - d.output[i], 2);
            sum += sum2;
        }
        return sum / data.length;
    }

    /**
     * Method begins to fill the allCostDiffs array at the given index
     * according to the given data. The indices corresponding to the biases of
     * the neurons in the output layer are set according to the differential of
     * the cost function.
     * <p>
     * Cost function:
     * cost[i] = (currentOutput[i] - desiredOutput[i]) ^ 2
     * where i is each index in the output array
     * <p>
     * Cost differential at output biases:
     * dCost[i] = 2 (currentOutput[i] - desiredOutput[i])
     * where i is each index in the output array
     *
     * @param output output of current training data
     * @param i      index in allCostDiffs corresponding to the the index of
     *               the current WayFinder.Data in the given WayFinder.Data[].
     */
    private void setCostDiffs(double[] output, int i) {
        for (int j = 0; j < output.length; j++)
            allCostDiffs[i][allCostDiffs[i].length - 1][j][0] =
                    -2 * (matrix[matrix.length - 1][j] - output[j]);

        for (int x = layers.length - 1; x >= 0; x--)
            for (int y = 0; y < layers[x].neurons.length; y++)
                setCostDiffsAux(i, x, y);
    }

    /**
     * Method fills the allCostDiffs array at the given index according to the
     * current training data. Method is specified a neuron and fills its
     * corresponding indices in allCostDiffs assuming that the bias index is
     * already filled.
     *
     * @param i index in allCostDiffs corresponding to the the index of the
     *          current WayFinder.Data in the given WayFinder.Data[].
     * @param x layer of specified neuron
     * @param y index of specified neuron in specified layer
     */
    private void setCostDiffsAux(int i, int x, int y) {
        double[] dCosts = allCostDiffs[i][x][y];
        double[] acts = matrix[x];
        for (int j = 1; j < dCosts.length; j++)
            dCosts[j] += dCosts[0] * acts[j - 1];

        if (x > 0) {
            double[][] layer = allCostDiffs[i][x - 1];
            double[] weights = layers[x].neurons[y].weights;
            for (int j = 0; j < layer.length; j++)
                layer[j][0] += dCosts[0] * weights[j + 1];
        }
    }

    /**
     * Class holds an array of neurons and performs various collective methods.
     */
    private class Layer {
        /**
         * Array stores the neurons of this layer.
         */
        private Neuron[] neurons;

        /**
         * Constructor fills the neurons array with the specified number of
         * neurons, and constructs each neuron with the number of neurons in
         * the layer above.
         *
         * @param aboveLength number of neurons in the layer above
         * @param thisLength  number of neurons in this layer
         */
        private Layer(int aboveLength, int thisLength) {
            neurons = new Neuron[thisLength];
            for (int i = 0; i < thisLength; i++)
                neurons[i] = new Neuron(aboveLength);
        }

        /**
         * Constructor fills the neurons array with the specified number of
         * neurons, and constructs each neuron with the number of neurons in
         * the layer above.
         *
         * @param aboveLength number of neurons in the layer above
         * @param thisLength  number of neurons in this layer
         * @param max upper limit of weight range
         * @param min lower limit of weight range
         */
        private Layer(int aboveLength, int thisLength, int max, int min) {
            neurons = new Neuron[thisLength];
            for (int i = 0; i < thisLength; i++)
                neurons[i] = new Neuron(aboveLength, max, min);
        }

        /**
         * Method gives the matrix of the neurons in the layer above to
         * each neuron in the neurons array and saves the resultant activation
         * of each neuron in an output array.
         *
         * @param acts matrix of the neurons in the layer above
         * @return matrix of the neurons in this layer
         */
        private double[] acts(double[] acts) {
            double[] output = new double[neurons.length];
            for (int i = 0; i < neurons.length; i++)
                output[i] = neurons[i].act(acts);
            return output;
        }

        /**
         * Method calls each neuron in this layer to calculate its activation
         * value and place it in the matrix matrix.
         *
         * @param x x coordinate of this layer
         */
        private void setActs(int x) {
            for (int y = 0; y < neurons.length; y++)
                neurons[y].setAct(x, y);
        }

        /**
         * Class holds an array of weights and uses them to calculate an
         * activation value.
         */
        private class Neuron {
            /**
             * Array stores coefficients for the activation value of each
             * neuron in the layer above and a bias value at index 0. The bias
             * and every weight begins with a value of 0.
             */
            private double[] weights;

            /**
             * Constructor takes the number of neurons in the layer above and
             * allocates memory for the weights array.
             *
             * @param length number of neurons in the layer above.
             */
            private Neuron(int length) {
                weights = new double[length + 1];
                for (int i = 0; i <= length; i++)
                    weights[i] = Math.random();
            }

            /**
             * Constructor takes the number of neurons in the layer above and
             * allocates memory for the weights array, and instantiates each
             * weight to a random number within a given range.
             *
             * @param length number of neurons in the layer above
             * @param max upper limit of weight range
             * @param min lower limit of weight range
             */
            private Neuron(int length, int max, int min) {
                weights = new double[length + 1];
                for (int i = 0; i <= length; i++)
                    weights[i] = (Math.random() * (max - min)) + min;
            }

            /**
             * Method returns the activation value of this neuron given an
             * array of the activation values of all the neurons in the layer
             * above.
             *
             * @param acts the activation values of the neurons in the layer
             *             above
             * @return the bias plus the sum of each above activation
             * multiplied by its corresponding coefficient stored in this
             * neuron
             */
            private double act(double[] acts) {
                double output = weights[0];
                for (int i = 0; i < acts.length; i++)
                    output += weights[i + 1] * acts[i];
                return output;//Math.max(MIN, Math.min(MAX, output));
            }

            /**
             * Method calculates the activation of this neuron and stores it in
             * the specified location in the matrix matrix.
             *
             * @param x x coordinate of this neuron
             * @param y neurons coordinate of this neuron
             */
            private void setAct(int x, int y) {
                double act = weights[0];
                for (int i = 0; i < matrix[x - 1].length; i++)
                    act += weights[i + 1] * matrix[x - 1][i];
                matrix[x + 1][y] = act;//Math.max(MIN, Math.min(MAX, act));
            }
        }
    }

    @Override
    public Brain clone() {
        Brain clone = new Brain(layerguide);
        for (int x = 0; x < this.layers.length; x++)
            for (int y = 0; y < this.layers[x].neurons.length; y++)
                System.arraycopy(this.layers[x].neurons[y].weights, 0,
                        clone.layers[x].neurons[y].weights, 0,
                        this.layers[x].neurons[y].weights.length );

        return clone;
    }

    /**
     * Method returns a network with weights within a given range of the values
     * of the weights in this network.
     *
     * @param range the range around each weight in this network within which
     *              each corresponding weight in the child network is set
     * @return child network
     */
    public Brain child(double range) {
        Brain child = new Brain(layerguide);
        for (int x = 0; x < this.layers.length; x++)
            for (int y = 0; y < this.layers[x].neurons.length; y++)
                for (int z = 0; z < this.layers[x].neurons[y].weights.length; z++)
                    child.layers[x].neurons[y].weights[z] = this.layers[x].neurons[y].weights[z]
                            + (Math.random() * range) - (range / 2);
        return child;
    }
}