package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

	/**
	 * IT-KM-03: Cannot capture own piece.
	 *
	 * White king on e1 attempts to move to f1 which is occupied by a white rook.
	 *
	 * Boundary: RulesEngine.isLegalMove returns false when destinationPiece is
	 * same color as the moving piece.
	 */
	@Test
	void whiteKing_cannotCaptureOwnPiece_isIllegal() {
		King king = new King(Color.WHITE);
		king.markMoved();
		place(king, 'e', 1);
		place(new Rook(Color.WHITE), 'f', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move move = buildMoveWithoutApplying(king, 'e', 1, 'f', 1);

		assertFalse(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}

	/**
	 * IT-KM-04: Cannot move more than one square when not castling.
	 *
	 * White king on e1 (hasMoved == true); e3 is empty.  e3 lies outside the
	 * king's one-step neighbourhood, so King.getLegalMoveDestinationSquares
	 * never includes it, and RulesEngine.getLegalMoves therefore never offers
	 * it as a legal destination.
	 *
	 */
	@Test
	void whiteKing_twoSquareNonCastle_notOfferedAsLegalMove() {
		King king = new King(Color.WHITE);
		king.markMoved();
		place(king, 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		List<Square> legalMoves = rulesEngine.getLegalMoves(
				stateFor(Color.WHITE), board.getSquare('e', 1));

		assertFalse(legalMoves.stream()
						.anyMatch(s -> s.getFile() == 'e' && s.getRank() == 3),
				"e3 must not be offered — king cannot move two squares without castling");
	}

	/**
	 * IT-KM-05: Cannot move into check.
	 *
	 * White king on e1 (hasMoved == true) attempts Ke1-f1; black rook on f8
	 * controls the entire f-file, so f1 is attacked.
	 *
	 * Collaborators: RulesEngine.isLegalMove temporarily places the king on f1,
	 * calls isInCheck, which calls isSquareAttacked and finds the black rook.
	 */
	@Test
	void whiteKing_movesIntoCheck_isIllegal() {
		King king = new King(Color.WHITE);
		king.markMoved();
		place(king, 'e', 1);
		place(new Rook(Color.BLACK), 'f', 8);
		place(new King(Color.BLACK), 'a', 8);

		Move move = buildMoveWithoutApplying(king, 'e', 1, 'f', 1);

		assertFalse(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}

	/**
	 * IT-KM-06: King at board corner — legal moves stay within bounds.
	 *
	 * White king on a1 (hasMoved == true); only a2, b1, b2 are valid adjacent
	 * squares.  No out-of-bounds square should appear.
	 */
	@Test
	void whiteKing_atCornerA1_legalMovesStayInBounds() {
		King king = new King(Color.WHITE);
		king.markMoved();
		place(king, 'a', 1);
		place(new King(Color.BLACK), 'h', 8);

		List<Square> legalMoves = rulesEngine.getLegalMoves(stateFor(Color.WHITE), board.getSquare('a', 1));

		assertEquals(3, legalMoves.size());
		assertTrue(legalMoves.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 2));
		assertTrue(legalMoves.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 1));
		assertTrue(legalMoves.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 2));
		for (Square s : legalMoves) {
			assertTrue(s.getFile() >= 'a' && s.getFile() <= 'h');
			assertTrue(s.getRank() >= 1 && s.getRank() <= 8);
		}
	}

	/**
	 * IT-KM-07: King move via applyMove sets hasMoved flag — castling no longer offered.
	 *
	 * White king on e1 advances to e2 through GameModel.applyMove.  After a
	 * black filler move the king is on e2. getLegalMoves must not include the
	 * castling squares c1 or g1, confirming hasMoved == true.
	 *
	 * Collaborators: GameModel.applyMove → Board.movePiece → Piece.markMoved
	 * → King.getLegalMoveDestinationSquares → RulesEngine.getLegalMoves.
	 */
	@Test
	void whiteKing_afterApplyMove_hasMoved_noCastlingCandidates() {
		Board board = new Board();
		RulesEngine rulesEngine = new RulesEngine();
		GameModel model = new GameModel(board, rulesEngine);

		for (char f = 'a'; f <= 'h'; f++) {
			for (int r = 1; r <= 8; r++) {
				board.getSquare(f, r).setOccupant(null);
			}
		}

		King whiteKing = new King(Color.WHITE);
		board.getSquare('e', 1).setOccupant(whiteKing);
		board.getSquare('e', 8).setOccupant(new King(Color.BLACK));

		// White: Ke1-e2
		Move whiteMove = Move.create(whiteKing, board.getSquare('e', 1), board.getSquare('e', 2));
		model.applyMove(whiteMove);

		// Black filler: Ke8-d8
		King blackKing = (King) board.getSquare('e', 8).getOccupant();
		Move blackMove = Move.create(blackKing, board.getSquare('e', 8), board.getSquare('d', 8));
		model.applyMove(blackMove);

		List<Square> moves = model.getLegalMoves(board.getSquare('e', 2));

		assertFalse(moves.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 1),
				"c1 must not be a legal destination — king has already moved");
		assertFalse(moves.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 1),
				"g1 must not be a legal destination — king has already moved");
	}
}
