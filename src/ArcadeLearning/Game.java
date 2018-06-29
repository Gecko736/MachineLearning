package ArcadeLearning;

public interface Game {
    State initialState();

    State newState(Move move);

    Move randomLegalMove(State state);

    Move[] legalMoves(State state);

    boolean legal(Move move);

    int gameOver(State state);
}