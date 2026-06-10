package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PawnIntegrationTest {

	private Board board;
	private RulesEngine rulesEngine;

	@BeforeEach
	void setUp() {
		board = new Board();
		rulesEngine = new RulesEngine();
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------
	private Square place(Piece piece, char file, int rank) {
		Square square = board.getSquare(file, rank);
		square.setOccupant(piece);
		return square;
	}

	private GameState stateFor(Color turn) {
		return GameState.create(board, turn, null);
	}

	// =========================================================================
	// NORMAL PAWN MOVES
	// =========================================================================

	/**
	 * IT-PM-01: Single-step forward — empty square ahead.
	 *
	 * A white pawn on e2 moving to e3 (one square forward, destination empty)
	 * must be accepted as legal by RulesEngine.
	 *
	 * <p>Collaborators: Board → Square → Pawn → Move → RulesEngine
	 * (isNormalPawnMoveLegal straight-one-forward branch → isEmpty check).
	 */
	@Test
	void whitePawnSingleStep_emptySquareAhead_isLegal() {
		Pawn pawn = new Pawn(Color.WHITE);
		place(pawn, 'e', 2);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move move = Move.create(pawn, board.getSquare('e', 2), board.getSquare('e', 3));

		assertTrue(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}
}