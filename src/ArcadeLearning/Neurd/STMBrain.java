package ArcadeLearning.Neurd;

import ArcadeLearning.Arcade;
import ArcadeLearning.ArrayLengthException;
import ArcadeLearning.Move;

import java.util.Collection;
import java.util.LinkedList;

public class STMBrain {
    private ShortTermMem shortTerm;
    private Layer[] x;

    public STMBrain(int inputSize, int memCapacity, int[] hiddenLayers, int outputSize) {
        if (memCapacity == 0)
            throw new IllegalArgumentException("Short term shortTerm capacity cannot be 0");

        x = new Layer[hiddenLayers.length + 3];

        int compOut = (int) Math.sqrt(inputSize);
        int avg = (compOut + inputSize) / 2;
        x[0] = new Layer(avg, inputSize);
        x[1] = new Layer(compOut, avg);

        shortTerm = new ShortTermMem(memCapacity, compOut);

        if (hiddenLayers.length > 0) {
            x[2] = new Layer(hiddenLayers[0], compOut * memCapacity);
            for (int i = 3; i < x.length - 1; i++)
                x[i] = new Layer(hiddenLayers[i - 2], hiddenLayers[i - 3]);
            x[x.length - 1] = new Layer(outputSize, hiddenLayers[hiddenLayers.length - 1]);
        } else
            x[2] = new Layer(compOut, outputSize);
    }

    public double[] move(int[] input) throws ArrayLengthException {
        if (input.length != shortTerm.inputSize)
            throw new ArrayLengthException(input.length, shortTerm.inputSize);

        if (Arcade.game.initialState().equals(input))
            shortTerm.memory.clear();

        double[] nums = new double[input.length];
        for (int i = 0; i < nums.length; i++)
            nums[i] = input[i];

        nums = x[0].act(nums);
        nums = x[1].act(nums);
        nums = shortTerm.act(nums);
        for (int i = 2; i < x.length; i++)
            nums = x[i].act(nums);

        return nums;
    }

    private ShortTermMem mem;
    private double[] matrix;
    private int[][] addrs;
    private double[][] acts;

    public void backPropagate(Collection<Move> moves, int bps) {
        for (int i = 0; i < bps; i++) {
            shortTerm.memory.clear();
        }
    }

    private double[] getCostDiffs(int[] input, int[] goodMove) {
        acts = new double[x.length][];
        {
            double[] dInput = new double[input.length];
            for (int i = 0; i < input.length; i++)
                dInput[i] = input[i];

            acts[0] = x[0].act(dInput);
        }
        acts[1] = shortTerm.act(x[1].act(acts[0]));

        return new double[0];
    }

    private void getCostDiffsAux(int x, int y, double current) {
        matrix[addrs[x][y]] += current;
        for (int z = 1; z <= this.x[x].y[y].z.length; z++)
            matrix[addrs[x][y] + z] += current * acts[x][y];
    }

    public STMBrain child(Collection<Move> moves) {
        return null;
    }

    public STMBrain clone() {
        return null;
    }

    private class ShortTermMem {
        private final LinkedList<double[]> memory = new LinkedList<>();
        private final int capacity;
        private final int inputSize;

        private ShortTermMem(int capacity, int inputSize) {
            this.capacity = capacity;
            this.inputSize = inputSize;
        }

        private double[] act(double[] newInput) {
            if (memory.size() >= capacity)
                memory.removeLast();
            memory.addFirst(newInput);

            double[] output = new double[capacity * inputSize];
            int i = 0;
            for (double[] d : memory) {
                System.arraycopy(d, 0, output, i, inputSize);
                i += inputSize;
            }
            return output;
        }
    }

    private class Layer implements Cloneable {
        public Neuron[] y;

        public Layer(int thisLength, int aboveLength) {
            y = new Neuron[thisLength];
            for (int i = 0; i < thisLength; i++)
                y[i] = new Neuron(aboveLength);
        }

        public double[] act(double[] vals) {
            double[] output = new double[y.length];
            for (int i = 0; i < y.length; i++)
                output[i] = y[i].act(vals);
            return output;
        }

        @Override
        public Layer clone() {
            Layer clone = new Layer(y.length, y[0].z.length - 1);
            for (int i = 0; i < y.length; i++)
                clone.y[i] = y[i].clone();
            return clone;
        }

        public class Neuron implements Cloneable {
            public double[] z;

            private Neuron(int z) {
                this.z = new double[z + 1];
                for (int i = 0; i <= z; i++)
                    this.z[i] = (Math.random() * 2) - 1;
            }

            public double act(double[] vals) {
                double sum = z[0];
                for (int i = 1; i < z.length; i++)
                    sum += vals[i - 1] * z[i];
                return sum;
            }

            @Override
            public Neuron clone() {
                Neuron clone = new Neuron(z.length - 1);
                System.arraycopy(z, 0, clone.z, 0, z.length);
                return clone;
            }
        }
    }
}