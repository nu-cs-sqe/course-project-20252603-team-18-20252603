package domain;

public interface GameModel {

	Board getBoard();

	Player[] getPlayers();

	Color getCurrentTurn();

	GameStatus getStatus();

	Move[] getMoveHistory();

	void initGame();

	Square[] getLegalMoves(Square square);

	void applyMove(Move move);

	void setBoard(Board board);

	void setPlayers(Player[] players);

	void setCurrentTurn(Color currentTurn);

	void setStatus(GameStatus status);

	void setMoveHistory(Move[] moveHistory);
}