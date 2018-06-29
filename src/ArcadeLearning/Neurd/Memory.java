package ArcadeLearning.Neurd;

import ArcadeLearning.ArrayLengthException;
import ArcadeLearning.Move;
import ArcadeLearning.State;
import ArcadeLearning.Structures.GameRecord;

import java.util.*;

public class Memory {
    private final PriorityQueue<String> names = new PriorityQueue<>();
    private final HashMap<String, Friend> friends = new HashMap<>();
    private final int gameLimit;

    public Memory() {
        gameLimit = 0;
    }

    public Memory(int gameLimit) {
        this.gameLimit = gameLimit;
    }

    public void add(String name, GameRecord gr) {
        if (!names.contains(name))
            throw new NoSuchElementException();
        friends.get(name).add(gr);
    }

    public void add(String name, int[] layers, GameRecord gr) {
        names.add(name);
        Friend newFriend = new Friend(name, layers);
        newFriend.add(gr);
        friends.put(name, newFriend);
    }

    public Friend get(String name) {
        return friends.get(name);
    }

    public class Friend implements Player {
        private final String name;
        private final Brain brain;
        private final LinkedList<GameRecord> games = new LinkedList<>();

        private Friend(String name, int[] layers) {
            this.name = name;
            brain = new Brain(layers);
        }

        private Friend(String name, Brain brain) {
            this.name = name;
            this.brain = brain;
        }

        public void add(GameRecord gr) {
            games.addFirst(gr);
            if (gameLimit != 0 && games.size() >= gameLimit)
                games.removeLast();

            LinkedList<Move> moves = new LinkedList<>();
            for (GameRecord g : games)
                Collections.addAll(moves, g.getMoves(name));
            brain.learn(moves);
        }

        @Override
        public Move move(State state) throws ArrayLengthException {
            double[] dMove = brain.move(state.ints);
            int[] iMove = new int[dMove.length];
            for (int i = 0; i < dMove.length; i++)
                iMove[i] = (int) dMove[i];
            return new Move(iMove, state);
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
        public boolean equals(Object o) {
            return o != null && o instanceof Friend && ((Friend) o).name.equals(name);
        }
    }
}