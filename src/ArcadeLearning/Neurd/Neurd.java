package ArcadeLearning.Neurd;

import ArcadeLearning.*;
import ArcadeLearning.Structures.GameRecord;
import ArcadeLearning.Structures.MoveSet;

import java.util.Collection;
import java.util.NoSuchElementException;

public class Neurd implements Reproductive {
    private final String name;
    private final Brain brain;
    private final MoveSet DNA;
    private final Brain newStatePredictor;
    private final Memory memory = new Memory();

    public Neurd(String name, int[] layers) {
        this.name = name;
        this.brain = new Brain(layers);
        DNA = new MoveSet();
        int stateSize = layers[0];
        int moveSize = layers[layers.length - 1];
        int[] nspLayers = {stateSize + moveSize, ((stateSize * 2) + moveSize) / 2, stateSize};
        newStatePredictor = new Brain(nspLayers);
    }

    public Neurd(String name, Brain brain) {
        this.name = name;
        this.brain = brain;
        DNA = new MoveSet();
        int stateSize = brain.inputSize();
        int moveSize = brain.outputSize();
        int[] nspLayers = {stateSize + moveSize, ((stateSize * 2) + moveSize) / 2, stateSize};
        newStatePredictor = new Brain(nspLayers);
    }

    public void add(String name, GameRecord gr) {
        try {
            memory.add(name, gr);
        } catch (NoSuchElementException e) {
            memory.add(name, brain.getLayerGuide(), gr);
        }
    }

    @Override
    public Move move(State state) throws ArrayLengthException {
        double[] dMove = brain.move(state.ints);
        int[] iMove = new int[dMove.length];
        for (int i = 0; i < dMove.length; i++)
            iMove[i] = (int) dMove[i];

        Move move = new Move(iMove, state);
        if (!Arcade.game.legal(move))
            move = Arcade.game.randomLegalMove(state);

        DNA.add(move);
        return move;
    }

    @Override
    public void learn(Collection<Move> moves) {
        brain.learn(moves);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Neurd child(String name) {
        return new Neurd(name, brain.child(DNA));
    }
}