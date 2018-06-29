package ArcadeLearning.Neurd;

import ArcadeLearning.ArrayLengthException;
import ArcadeLearning.Move;
import sun.plugin.javascript.navig4.Layer;

import java.util.Collection;

public class Brain {
    protected int[] layerGuide;

    private Layer[] layers;

    public Brain(int[] layers) {
        this.layers = new Layer[layers.length - 1];
        matrix = new double[layers.length][];
        matrix[0] = new double[layers[0]];
        for (int i = 1; i < layers.length; i++) {
            this.layers[i - 1] = new Layer(layers[i - 1], layers[i]);
            matrix[i] = new double[layers[i]];
        }
        layerGuide = layers;
    }

    public Brain(double[][][] mind) {
        layers = new Layer[mind.length];
        for (int i = 0; i < mind.length; i++)
            layers[i] = new Layer(mind[i]);
    }

    public double[] move(int[] input) {
        double[] nums = new double[input.length];
        for (int i = 0; i < nums.length; i++)
            nums[i] = input[i];

        for (int i = layers.length - 1; i >= 0; i--)
            nums = layers[i].act(nums);

        return nums;
    }

    public double learn(Collection<Move> moves) {
        boolean go;
        double lastCost = -1;
        do {
            double cost = backpropagate(moves);
            go = cost < lastCost || lastCost == -1;
            lastCost = cost;
        } while (go);
        return lastCost;
    }

    private double[][] matrix;
    private double[][][][] allCostDiffs;

    private double backpropagate(Collection<Move> moves) {
        allCostDiffs = new double[moves.size()][][][];
        for (int i = 0; i < allCostDiffs.length; i++) {
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
        int i = 0;
        for (Move move : moves) {
            matrix[0] = move.getState();

            for (int x = 1; x < layers.length; x++)
                layers[x].setActs(x);

            setCostDiffs(move.getMove(), i);
            double[][][] here = allCostDiffs[i];
            for (int x = 0; x < here.length; x++)
                for (int y = 0; y < here[x].length; y++)
                    for (int z = 0; z < here[x][y].length; z++)
                        step[x][y][z] += here[x][y][z];
            i++;
        }

        // loop turns the sums in the step vector into averages
        for (int x = 0; x < step.length; x++)
            for (int y = 0; y < step[x].length; y++)
                for (int z = 0; z < step[x][y].length; z++)
                    step[x][y][z] /= moves.size();

        // loop calculates the magnitude of the step vector
        double size = 0;
        for (double[][] layer : step)
            for (double[] neuron : layer)
                for (double weight : neuron)
                    size += Math.pow(weight, 2);
        size = Math.sqrt(size);

        double cost = cost(moves);

        // loop makes the step
        for (int x = 0; x < layers.length; x++)
            for (int y = 0; y < layers[x].neurons.length; y++)
                for (int z = 0; z < layers[x].neurons[y].weights.length; z++)
                    layers[x].neurons[y].weights[z] += step[x][y][z] * Math.pow(cost, 2) / (Math.pow(size, 2) * 4665);

        allCostDiffs = null;
        return cost;
    }

    public double cost(Collection<Move> moves) {
        double sum = 0;
        for (Move move : moves) {
            double[] output = move(move.state.ints);
            for (int i = 0; i < output.length; i++)
                sum += Math.pow(output[i] - move.ints[i], 2);
        }
        return sum / moves.size();
    }

    private void setCostDiffs(double[] output, int i) {
        for (int j = 0; j < output.length; j++)
            allCostDiffs[i][allCostDiffs[i].length - 1][j][0] =
                    -2 * (matrix[matrix.length - 1][j] - output[j]);

        for (int x = layers.length - 1; x >= 0; x--)
            for (int y = 0; y < layers[x].neurons.length; y++)
                setCostDiffsAux(i, x, y);
    }

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

    private double[][][] costDiffs;

    private void getCostDiffs(Collection<Move> moves) {
        costDiffs = new double[layerGuide.length - 1][][];
        for (int i = 0; i < layerGuide.length - 1; i++)
            costDiffs[i] = new double[layerGuide[i + 1]][layerGuide[i]];

        for (Move move : moves) {
            matrix[0] = move.getState();
            for (int i = 1; i < layers.length; i++)
                layers[i].setActs(i);

            for (int i = 0; i < move.ints.length; i++)
                costDiffs[costDiffs.length - 1][i][0] =
                        -2 * (matrix[matrix.length - 1][i] - move.state.ints[i]);

            for (int x = layers.length - 1; x >= 0; x--)
                for (int y = 0; y < layers[x].neurons.length; y++)
                    getCostDiffsAux(x, y);
        }
    }

    private void getCostDiffsAux(int x, int y) {
        double[] dCosts = costDiffs[x][y];
        double[] acts = matrix[x];
        for (int j = 1; j < dCosts.length; j++)
            dCosts[j] += dCosts[0] * acts[j - 1];

        if (x > 0) {
            double[][] layer = costDiffs[x - 1];
            double[] weights = layers[x].neurons[y].weights;
            for (int j = 0; j < layer.length; j++)
                layer[j][0] += dCosts[0] * weights[j + 1];
        }
    }

    public Brain child(double range) {
        Brain child = new Brain(layerGuide);
        for (int x = 0; x < this.layers.length; x++)
            for (int y = 0; y < this.layers[x].neurons.length; y++)
                for (int z = 0; z < this.layers[x].neurons[y].weights.length; z++)
                    child.layers[x].neurons[y].weights[z] = this.layers[x].neurons[y].weights[z]
                            + (Math.random() * range) - (range / 2);
        return child;
    }

    public Brain child(Collection<Move> moves) {
        getCostDiffs(moves);
        Brain child = new Brain(layerGuide);
        for (int x = 0; x < this.layers.length; x++)
            for (int y = 0; y < this.layers[x].neurons.length; y++)
                for (int z = 0; z < this.layers[x].neurons[y].weights.length; z++)
                    child.layers[x].neurons[y].weights[z] = this.layers[x].neurons[y].weights[z]
                            + Math.random() * costDiffs[x][y][z];
        costDiffs = null;
        return child;
    }

    public void print() {
        for (int x = 0; x < this.layers.length; x++) {
            System.out.print("layer " + x);
            for (int y = 0; y < this.layers[x].neurons.length; y++) {
                System.out.print("\n\tneuron " + y + ": ");
                for (int z = 0; z < this.layers[x].neurons[y].weights.length; z++)
                    System.out.print(" " + this.layers[x].neurons[y].weights[z]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public double[][][] getMind() {
        double[][][] output = new double[layers.length][][];
        for (int i = 0; i < layers.length; i++) {
            output[i] = new double[layers[i].neurons.length][layers[i].neurons[0].weights.length];
            for (int j = 0; j < layers[i].neurons.length; i++)
                System.arraycopy(layers[i].neurons[j].weights, 0, output[i][j], 0, output[i][j].length);
        }
        return output;
    }

    public void setMind(double[][][] step) throws ArrayLengthException {
        if (layers.length != step.length)
            throw new ArrayLengthException(layers.length, step.length);
        for (int i = 0; i < layers.length; i++) {
            if (layers[i].neurons.length != step[i].length)
                throw new ArrayLengthException(layers[i].neurons.length, step[i].length);
            for (int j = 0; j < layers[i].neurons.length; j++)
                if (layers[i].neurons[j].weights.length != step[i][j].length)
                    throw new ArrayLengthException(layers[i].neurons[j].weights.length, step[i][j].length);
        }

        for (int x = 0; x < layers.length; x++)
            for (int y = 0; y < layers[x].neurons.length; y++)
                for (int z = 0; z < layers[x].neurons[y].weights.length; z++)
                    layers[x].neurons[y].weights[z] += step[x][y][z];
    }

    public int inputSize() {
        return layerGuide[0];
    }

    public int outputSize() {
        return layerGuide[layerGuide.length - 1];
    }

    @Override
    public Brain clone() {
        return new Brain(getMind());
    }

    private class Layer implements Cloneable {
        private Neuron[] neurons;

        private Layer(int thisLength, int aboveLength) {
            neurons = new Neuron[thisLength];
            for (int i = 0; i < thisLength; i++)
                neurons[i] = new Neuron(aboveLength);
        }

        private Layer(double[][] mind) {
            neurons = new Neuron[mind.length];
            for (int i = 0; i < mind.length; i++)
                neurons[i] = new Neuron(mind[i]);
        }

        private double[] act(double[] vals) {
            double[] output = new double[neurons.length];
            for (int i = 0; i < neurons.length; i++)
                output[i] = neurons[i].act(vals);
            return output;
        }

        private void setActs(int x) {
            for (int y = 0; y < neurons.length; y++)
                neurons[y].setAct(x, y);
        }

        @Override
        public Layer clone() {
            Layer clone = new Layer(neurons.length, neurons[0].weights.length - 1);
            for (int i = 0; i < neurons.length; i++)
                clone.neurons[i] = neurons[i].clone();
            return clone;
        }

        public class Neuron implements Cloneable {
            private double[] weights;

            private Neuron(int weights) {
                this.weights = new double[weights + 1];
                for (int i = 0; i <= weights; i++)
                    this.weights[i] = (Math.random() * 2) - 1;
            }

            private Neuron(double[] mind) {
                weights = new double[mind.length];
                System.arraycopy(mind, 0, weights, 0, mind.length);
            }

            private double act(double[] vals) {
                double sum = weights[0];
                for (int i = 1; i < weights.length; i++)
                    sum += vals[i - 1] * weights[i];
                return sum;
            }

            private void setAct(int x, int y) {
                double act = weights[0];
                for (int i = 0; i < matrix[x - 1].length; i++)
                    act += weights[i + 1] * matrix[x - 1][i];
                matrix[x + 1][y] = act;
            }

            @Override
            public Neuron clone() {
                Neuron clone = new Neuron(weights.length - 1);
                System.arraycopy(weights, 0, clone.weights, 0, weights.length);
                return clone;
            }
        }
    }
}