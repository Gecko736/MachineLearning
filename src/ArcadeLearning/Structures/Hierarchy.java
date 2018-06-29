package ArcadeLearning.Structures;

import ArcadeLearning.Arcade;
import ArcadeLearning.Neurd.Reproductive;

import java.util.Collection;
import java.util.Collections;
import java.util.PriorityQueue;

public class Hierarchy<P extends Reproductive> {
    private P[] population;
    private Selector<P> selector;

    public Hierarchy(Collection<P> population, Selector selector) {
        PriorityQueue<P> order = new PriorityQueue<>(population.size(), Arcade::compare);
        order.addAll(population);
        this.population = (P[]) order.toArray();
        this.selector = selector;
    }

    public Hierarchy(P[] population, Selector selector) {
        PriorityQueue<P> order = new PriorityQueue<>(population.length, Arcade::compare);
        Collections.addAll(order, population);
        this.population = (P[]) order.toArray();
        this.selector = selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public void generation() {
        population = selector.hypergamy(population);
    }

    public void play() {
        PriorityQueue<P> order = new PriorityQueue<>(population.length, Arcade::play);
        Collections.addAll(order, population);
        population = (P[]) order.toArray();
    }

    public void play(int times) {
        PriorityQueue<P> order = new PriorityQueue<>(population.length, Arcade::play);
        for (int i = 0; i < times; i++) {
            Collections.addAll(order, population);
            population = (P[]) order.toArray();
            order.clear();
        }
    }

    public P[] topHalf() {
        P[] output = (P[]) new Reproductive[population.length / 2];
        System.arraycopy(population, 0, output, 0, output.length);
        return output;
    }

    public P[] top(int howMany) {
        P[] output = (P[]) new Reproductive[howMany];
        System.arraycopy(population, 0, output, 0, output.length);
        return output;
    }

    public P best() {
        return population[0];
    }
}
