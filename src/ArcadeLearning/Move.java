package ArcadeLearning;

public class Move implements Cloneable {
    public final int[] ints;
    public final State state;

    public Move(int[] ints, State state) {
        this.ints = new int[ints.length];
        System.arraycopy(ints, 0, this.ints, 0, ints.length);
        this.state = state;
    }

    public Move(int[] ints, int[] state) {
        this.ints = new int[ints.length];
        System.arraycopy(ints, 0, this.ints, 0, ints.length);
        this.state = new State(state);
    }

    public double[] getMove() {
        double[] output = new double[ints.length];
        for (int i = 0; i < ints.length; i++)
            output[i] = ints[i];
        return output;
    }

    public double[] getState() {
        double[] output = new double[state.ints.length];
        for (int i = 0; i < state.ints.length; i++)
            output[i] = state.ints[i];
        return output;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(toString());
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i : ints)
            output.append(Math.abs(i));
        return output.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Move && ints == ((Move) o).ints && ((Move) o).state == state;
    }

    @Override
    public Move clone() {
        return new Move(ints, state);
    }
}