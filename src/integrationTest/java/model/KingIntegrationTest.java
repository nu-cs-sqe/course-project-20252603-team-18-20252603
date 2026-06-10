package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KingIntegrationTest {

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

	/**
	 * Builds a Move recording a piece travelling from one square to another
	 * without permanently altering the board.
	 *
	 * Move.create() requires the piece to be the occupant of the from-square
	 * at call time.  This helper temporarily swaps occupants, builds the Move,
	 * then restores the board.
	 */
	private Move buildMoveWithoutApplying(Piece piece,
											char fromFile, int fromRank,
											char toFile,   int toRank) {
		Square fromSquare = board.getSquare(fromFile, fromRank);
		Square toSquare   = board.getSquare(toFile,   toRank);

		Piece originalFrom = fromSquare.getOccupant();
		Piece originalTo   = toSquare.getOccupant();

		fromSquare.setOccupant(piece);
		toSquare.setOccupant(null);
		Move move = Move.create(piece, fromSquare, toSquare);

		fromSquare.setOccupant(originalFrom);
		toSquare.setOccupant(originalTo);

		return move;
	}

	// =========================================================================
	// NORMAL KING MOVES
	// =========================================================================

	/**
	 * IT-KM-01: Single-step to an empty adjacent square.
	 *
	 * A white king on e1 (hasMoved == true) attempts Ke1-f1, which is empty
	 * and not attacked.
	 *
	 * Collaborators: King → RulesEngine.isLegalMove (normal path: not castling,
	 * not promotion, not en passant; destination empty, king not in check
	 * afterward).
	 */
	@Test
	void whiteKing_singleStepToEmptySquare_isLegal() {
		King king = new King(Color.WHITE);
		king.markMoved();
		place(king, 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move move = buildMoveWithoutApplying(king, 'e', 1, 'f', 1);

		assertTrue(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}

	/**
	 * IT-KM-02: Single-step captures an opponent piece.
	 *
	 * White king on e1 captures a black rook on f1. f1 is not defended.
	 *
	 * Collaborators: RulesEngine.isLegalMove checks destination is not
	 * same-color (it is black), path not blocked (king), then simulates the
	 * move and confirms king is not in check.
	 */
	@Test
	void whiteKing_capturesOpponentPiece_isLegal() {
		King king = new King(Color.WHITE);
		king.markMoved();
		place(king, 'e', 1);
		place(new Rook(Color.BLACK), 'f', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move move = buildMoveWithoutApplying(king, 'e', 1, 'f', 1);

		assertTrue(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}
}
