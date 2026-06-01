package domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class GameState {
	private final Board board;
	private final Color currentTurn;
	private final Move lastMove;

	private GameState(Board board, Color currentTurn, Move lastMove) {
		this.board = board;
		this.currentTurn = currentTurn;
		this.lastMove = lastMove;
	}

	public static GameState create(Board board, Color currentTurn, Move lastMove) {
		if (board == null) {
			throw new IllegalArgumentException("Board cannot be null");
		}
		if (currentTurn == null) {
			throw new IllegalArgumentException("Color 'currentTurn' cannot be null");
		}
		return new GameState(board, currentTurn, lastMove);
	}

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP",
			justification = "GameState intentionally exposes Board."
	)
	public Board getBoard() {
		return board;
	}

	public Color getCurrentTurn() {
		return currentTurn;
	}

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP",
			justification = "GameState intentionally exposes Move."
	)
	public Move getLastMove() {
		return lastMove;
	}
}
