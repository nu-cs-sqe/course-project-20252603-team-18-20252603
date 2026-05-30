package domain;

public class GameState {
	private final Board board;
	private final Color currentTurn;
	private final Move lastMove;

	public GameState(Board board, Color currentTurn, Move lastMove) {
		this.board = board;
		this.currentTurn = currentTurn;
		this.lastMove = lastMove;
	}

	public Board getBoard() {
		return board;
	}

	public Color getCurrentTurn() {
		return currentTurn;
	}

	public Move getLastMove() {
		return lastMove;
	}
}
