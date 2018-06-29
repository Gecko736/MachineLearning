package ArcadeLearning;

public class State {
    public final int[] ints;
    private final int hash;

    public State(int[] ints) {
        this.ints = new int[ints.length];
        System.arraycopy(ints, 0, this.ints, 0, ints.length);
        hash = Integer.parseInt(toString());
    }

    public double[] getDoubles() {
        double[] output = new double[ints.length];
        for (int i = 0; i < ints.length; i++)
            output[i] = ints[i];
        return output;
    }

    public void setPerspective(int perspective) {
        ints[0] = perspective;
    }

    @Override
    public State clone() {
        return new State(ints);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof State && equals(((State) o).ints);
    }

    public boolean equals(int[] ints) {
        if (ints.length != this.ints.length)
            return false;
        for (int i = 0; i < ints.length; i++)
            if (ints[i] != this.ints[i])
                return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i : ints)
            output.append(Math.abs(i));
        return output.toString();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static int compare(int[] s1, int[] s2) {
        if (s1.length != s2.length)
            return -1;
        for (int i = 0; i < s1.length; i++)
            if (s1[i] != s2[i])
                return s1[i] - s2[i];
        return 0;
    }
}