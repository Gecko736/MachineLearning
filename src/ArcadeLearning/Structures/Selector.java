package ArcadeLearning.Structures;

import ArcadeLearning.Neurd.Reproductive;

public interface Selector<P extends Reproductive> {
    P[] hypergamy(P[] population);
}
