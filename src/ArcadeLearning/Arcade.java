package ArcadeLearning;

import ArcadeLearning.Neurd.Player;
import ArcadeLearning.Structures.GameRecord;

import java.util.LinkedList;

public class Arcade {
    public static Game game;

    public static int compare(Player p1, Player p2) {
        try {
            int score;
            State state = game.initialState();

            for (int g = -1; g == -1; g = game.gameOver(state)) {
                state = game.newState(p1.move(state));
                if (game.gameOver(state) == -1)
                    state = game.newState(p2.move(state));
            }
            if (game.gameOver(state) == 1)
                score = 1;
            else
                score = 0;

            state = game.initialState();
            for (int g = -1; g == -1; g = game.gameOver(state)) {
                state = game.newState(p1.move(state));
                if (game.gameOver(state) == -1)
                    state = game.newState(p2.move(state));
            }
            if (game.gameOver(state) == 2)
                score++;
            else
                score--;

            return score;
        } catch (ArrayLengthException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public static int play(Player p1, Player p2) {
        try {
            LinkedList<Move> p1moves = new LinkedList<>();
            LinkedList<Move> p2moves = new LinkedList<>();
            int score = 0;
            State state = game.initialState();
            for (int g = -1; g == -1; g = game.gameOver(state)) {
                p1moves.addLast(p1.move(state));
                state = game.newState(p1moves.peekLast());
                if (game.gameOver(state) == -1) {
                    p2moves.addLast(p2.move(state));
                    state = game.newState(p2moves.peekLast());
                }
            }
            int over = game.gameOver(state);
            if (over == 1)
                score = 1;
            else if (over == 2)
                score = -1;
            GameRecord gr = new GameRecord(p1, p1moves, p2, p2moves, over);

            p1moves.clear();
            p2moves.clear();
            state = game.initialState();
            for (int g = -1; g == -1; g = game.gameOver(state)) {
                p2moves.addLast(p2.move(state));
                state = game.newState(p2moves.peekLast());
                if (game.gameOver(state) == -1) {
                    p1moves.addLast(p1.move(state));
                    state = game.newState(p1moves.peekLast());
                }
            }
            over = game.gameOver(state);
            if (over == 1)
                score++;
            else if (over == 2)
                score--;
            gr = new GameRecord(p1, p1moves, p2, p2moves, over);

            if (score == 0)
                return play(p1, p2);
            else
                return score;
        } catch (ArrayLengthException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
}