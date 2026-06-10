package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

	/**
	 * IT-PM-02: Double-step from starting rank — both squares empty.
	 *
	 * A white pawn on e2 that has not yet moved may advance to e4 when both
	 * e3 and e4 are empty.
	 *
	 * <p>Boundary: the two-square advance is only possible when hasMoved == false
	 * and the intermediate square is clear.
	 */
	@Test
	void whitePawnDoubleStep_fromStartingRank_isLegal() {
		Pawn pawn = new Pawn(Color.WHITE);
		place(pawn, 'e', 2);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move move = Move.create(pawn, board.getSquare('e', 2), board.getSquare('e', 4));

		assertTrue(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}

	/**
	 * IT-PM-03: Double-step blocked by hasMoved flag.
	 *
	 * A white pawn on e3 with hasMoved == true attempts to advance two squares
	 * to e5.  RulesEngine must reject this even when both squares are empty.
	 *
	 * <p>Boundary: hasMoved gates the double-step path inside
	 * isNormalPawnMoveLegal.
	 */
	@Test
	void whitePawnDoubleStep_afterHavingMoved_isIllegal() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();
		place(pawn, 'e', 3);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move move = Move.create(pawn, board.getSquare('e', 3), board.getSquare('e', 5));

		assertFalse(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}

	/**
	 * IT-PM-04: Double-step unavailable after pawn has already moved via applyMove.
	 *
	 * Drives a white pawn from e2 to e3 through GameModel.applyMove, then
	 * asserts that e5 is not offered as a legal destination on white's next turn.
	 *
	 * Collaborators: GameModel.applyMove → Board.movePiece → (missing markMoved())
	 * → Pawn.getLegalMoveDestinationSquares → RulesEngine.getLegalMoves.
	 */
	@Test
	void whitePawn_afterMovingViaApplyMove_cannotDoubleStep() {
		Board board = new Board();
		RulesEngine rulesEngine = new RulesEngine();
		GameModel model = new GameModel(board, rulesEngine);

		for (char f = 'a'; f <= 'h'; f++) {
			for (int r = 1; r <= 8; r++) {
				board.getSquare(f, r).setOccupant(null);
			}
		}

		Pawn whitePawn = new Pawn(Color.WHITE);
		board.getSquare('e', 2).setOccupant(whitePawn);
		board.getSquare('e', 1).setOccupant(new King(Color.WHITE));
		board.getSquare('e', 8).setOccupant(new King(Color.BLACK));

		// Turn 1 — white moves pawn e2→e3.
		Move firstMove = Move.create(whitePawn, board.getSquare('e', 2), board.getSquare('e', 3));
		model.applyMove(firstMove);

		// Turn 2 — give black a dummy king move so it's white's turn again.
		King blackKing = (King) board.getSquare('e', 8).getOccupant();
		Move blackKingMove = Move.create(blackKing, board.getSquare('e', 8), board.getSquare('d', 8));
		model.applyMove(blackKingMove);

		// e5 must NOT appear in the legal moves.
		List<Square> legalMoves = model.getLegalMoves(board.getSquare('e', 3));

		assertFalse(
				legalMoves.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5),
				"Pawn must not be able to double-step after already moving via applyMove"
		);
	}

	// =========================================================================
	// PAWN CAPTURES
	// =========================================================================

	/**
	 * IT-PC-01: Diagonal capture — opponent present.
	 *
	 * A white pawn on e4 capturing a black pawn on f5 diagonally must be legal.
	 *
	 * <p>Collaborators: RulesEngine.isNormalPawnMoveLegal diagonal branch reads
	 * toBoardSquare.isEmpty() and getOccupant().getColor() from the live Board.
	 */
	@Test
	void whitePawnDiagonalCapture_opponentPresent_isLegal() {
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();
		place(whitePawn, 'e', 4);
		place(new Pawn(Color.BLACK), 'f', 5);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move move = Move.create(whitePawn, board.getSquare('e', 4), board.getSquare('f', 5));

		assertTrue(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}
}