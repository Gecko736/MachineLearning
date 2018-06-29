package WayFinder;

public class Test {
    public static void main(String[] args) {
        Data[] data = new Data[30];
            int j = 0;
            for (int i = -10; i < 20; i++) {
                double[] input = {i};
                double[] output = {(i * 10) + 5};
                data[j++] = new Data(input, output);
            }
        Brain.MAX = 205;
        Brain.MIN = -95;
        int[] layers = {1, 3, 1};
        Brain best = Brain.population(205, -95, 10, 0, 25, 50, layers, data);
        //Brain best = Brain.best(205, -95, 50, layers, data);
        best.print();
        System.out.println(best.cost(data));
    }
}