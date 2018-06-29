package ArcadeLearning.Neurd;

import ArcadeLearning.Move;
import ArcadeLearning.ArrayLengthException;
import ArcadeLearning.State;

import java.util.Collection;

public interface Player {
    Move move(State state) throws ArrayLengthException;

    void learn(Collection<Move> moves);

    String getName();
}
