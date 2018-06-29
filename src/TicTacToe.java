import ArcadeLearning.*;
import ArcadeLearning.Neurd.Brain;
import ArcadeLearning.Neurd.Neurd;
import ArcadeLearning.Structures.Hierarchy;
import ArcadeLearning.Structures.Selector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class TicTacToe implements Game {
    public static Hierarchy<Neurd> hierarchy;

    public static void main(String[] args) {
        Arcade.game = new TicTacToe();
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");
        Neurd[] population = new Neurd[65536];

        System.out.println(time.format(new Date()) + " initializing");
        int[] layers = {10, 5, 5, 1};
        for (int i = 0; i < 65536; i++)
            population[i] = new Neurd(Integer.toHexString(i) + "-0", new Brain(layers));

        System.out.println(time.format(new Date()) + " 0");

    }

    @Override
    public State initialState() {
        int[] state = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        return new State(state);
    }

    @Override
    public State newState(Move move) {
        int[] state = move.getState().getNums();
        state[move.getMove()[0]] = state[0];
        if (state[0] == 1)
            state[0] = 2;
        else
            state[0] = 1;
        return new State(state);
    }

    @Override
    public Move randomLegalMove(State state) {
        Move[] moves = legalMoves(state);
        return moves[(int) (Math.random() * moves.length)];
    }

    @Override
    public Move[] legalMoves(State state) {
        LinkedList<Integer> moves = new LinkedList<>();
        int[] nums = state.getDoubles();
        for (int i = 1; i < nums.length; i++)
            if (nums[i] == 0)
                moves.add(i);
        Move[] output = new Move[moves.size()];
        for (int i = 0; i < output.length; i++) {
            int[] move = {moves.poll()};
            output[i] = new Move(move, state);
        }
        return output;
    }

    @Override
    public boolean legal(Move move) {
        int[] state = move.getState().getNums();
        int m = move.getMove()[0];
        for (int i = 1; i < state.length; i++)
            if (state[i] == 0 && i == m)
                return true;
        return false;
    }

    @Override
    public int gameOver(State state) {
        int[] s = state.getDoubles();
        if ((s[1] == s[2] && s[2] == s[3]) ||
                (s[1] == s[5] && s[5] == s[9]) ||
                (s[1] == s[4] && s[4] == s[7]))
            return s[1];

        if (s[2] == s[5] && s[5] == s[8])
            return s[2];

        if ((s[3] == s[5] && s[5] == s[7]) || (s[3] == s[6] && s[6] == s[9]))
            return s[3];

        if (s[4] == s[5] && s[5] == s[6])
            return s[4];

        if (s[7] == s[8] && s[8] == s[9])
            return s[7];

        for (int i = 1; i < s.length; i++)
            if (s[i] == 0)
                return 0;

        return -1;
    }

    public class Generator implements Selector<Neurd> {
        @Override
        public Neurd[] hypergamy(Neurd[] population) {
            Neurd[] topHalf = new Neurd[population.length / 2];
            System.arraycopy(population, 0, topHalf, 0, topHalf.length / 2);
            Neurd[] btmHalf = new Neurd[topHalf.length];
            int fourth = topHalf.length / 2;
            for (int i = 0; i < fourth; i++) {

            }
            return null;
        }
    }

}