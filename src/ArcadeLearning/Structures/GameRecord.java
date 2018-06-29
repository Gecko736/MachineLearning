package ArcadeLearning.Structures;

import ArcadeLearning.Move;
import ArcadeLearning.Neurd.Memory;
import ArcadeLearning.Neurd.Neurd;
import ArcadeLearning.Neurd.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class GameRecord {
    private final String p1;
    private final Move[] p1moves;
    private final String p2;
    private final Move[] p2moves;
    private final int winner;
    private boolean learned = false;

    public GameRecord(String p1, String p2, Collection<Move> moves, int winner) {
        this.p1 = p1;
        this.p2 = p2;
        this.winner = winner;
        if (moves.size() % 2 == 1)
            p1moves = new Move[(moves.size() / 2) + 1];
        else
            p1moves = new Move[moves.size() / 2];
        p2moves = new Move[moves.size() / 2];
        Iterator<Move> it = moves.iterator();
        for (int i = 0; it.hasNext(); i++) {
            p1moves[i] = it.next();
            if (it.hasNext())
                p2moves[i] = it.next();
        }
    }

    public GameRecord(String p1, Collection<Move> p1moves, String p2, Collection<Move> p2moves, int winner) {
        this.p1 = p1;
        this.p1moves = p1moves.toArray(new Move[0]);
        this.p2 = p2;
        this.p2moves = p2moves.toArray(new Move[0]);
        this.winner = winner;
    }

    public GameRecord(Player p1, LinkedList<Move> p1moves, Player p2, LinkedList<Move> p2moves, int winner) {
        this.p1 = p1.getName();
        this.p1moves = p1moves.toArray(new Move[0]);
        this.p2 = p2.getName();
        this.p2moves = p2moves.toArray(new Move[0]);
        this.winner = winner;

        if (!(p1 instanceof Memory.Friend)) {
            if (winner == 1)
                p1.learn(p1moves);
            else
                p1.learn(p2moves);
            if (p1 instanceof Neurd)
                ((Neurd) p1).add(this.p2, this);
        }
        if (!(p2 instanceof Memory.Friend)) {
            if (winner == 2)
                p2.learn(p2moves);
            else
                p2.learn(p1moves);
            if (p2 instanceof Neurd)
                ((Neurd) p2).add(this.p1, this);
        }
        learned = true;
    }

    public Move[] getMoves(String name) {
        if (name.equals(p1))
            return p1moves;
        else if (name.equals(p2))
            return p2moves;
        else
            throw new IllegalArgumentException("Given: " + name + " Expected: " + p1 + " or " + p2);
    }

    public Move[] getMoves(boolean ofWinner) {
        if (winner == 0)
            throw new IllegalStateException(toString());
        if (winner == 1)
            return p1moves;
        return p2moves;
    }

    public Move[] getMoves() {
        Move[] output = new Move[p1moves.length + p2moves.length];
        int j = 0;
        for (int i = 0; i < p1moves.length; i++) {
            output[j++] = p1moves[i];
            if (i < p2moves.length)
                output[j++] = p2moves[i];
        }
        return output;
    }

    public String winner() {
        if (winner == 1)
            return p1;
        if (winner == 2)
            return p2;
        return "tie";
    }

    public String loser() {
        if (winner == 2)
            return p1;
        if (winner == 1)
            return p2;
        return "tie";
    }

    public void endGame(Neurd p1, Neurd p2) {
        if (learned)
            throw new IllegalStateException("Game record already learned");
        if (!p1.getName().equals(this.p1) && !p2.getName().equals(this.p2))
            throw new IllegalArgumentException("Expected: " + this.p1 + ", " + this.p2 +
                    " Given: " + p1.getName() + ", " + p2.getName());
        if (!p1.getName().equals(this.p1))
            throw new IllegalArgumentException("Expected: " + this.p1 + " Given: " + p1.getName());
        if (!p2.getName().equals(this.p2))
            throw new IllegalArgumentException("Expected: " + this.p2 + " Given: " + p2.getName());

        p1.add(this.p2, this);
        p2.add(this.p1, this);

        LinkedList<Move> moves1 = new LinkedList<>();
        Collections.addAll(moves1, p1moves);
        LinkedList<Move> moves2 = new LinkedList<>();
        Collections.addAll(moves2, p2moves);

        if (winner == 0) {
            p1.learn(moves2);
            p2.learn(moves1);
        } else if (winner == 1) {
            p1.learn(moves1);
            p2.learn(moves1);
        } else {
            p1.learn(moves2);
            p2.learn(moves2);
        }
        learned = true;
    }

    @Override
    public String toString() {
        return p1 + " vs " + p2 + " = " + winner();
    }
}