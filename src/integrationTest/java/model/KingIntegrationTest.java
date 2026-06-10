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

	// =========================================================================
	// CHECK DETECTION
	// =========================================================================

	/**
	 * IT-CH-01: King in check by rook.
	 *
	 * White king on e1, black rook on e8, clear file between them.
	 *
	 * Collaborators: RulesEngine.isInCheck → isSquareAttacked finds the rook
	 * via the isStraight check with an unblocked path.
	 */
	@Test
	void isInCheck_kingAttackedByRook_returnsTrue() {
		place(new King(Color.WHITE), 'e', 1);
		place(new Rook(Color.BLACK), 'e', 8);
		place(new King(Color.BLACK), 'a', 8);

		assertTrue(rulesEngine.isInCheck(stateFor(Color.WHITE), Color.WHITE));
	}

	/**
	 * IT-CH-02: King in check by knight.
	 *
	 * White king on e1, black knight on f3.  Knights jump over pieces — no
	 * path-clear check is needed.
	 *
	 * Collaborators: isSquareAttacked isLShape branch.
	 */
	@Test
	void isInCheck_kingAttackedByKnight_returnsTrue() {
		place(new King(Color.WHITE), 'e', 1);
		place(new Knight(Color.BLACK), 'f', 3);
		place(new King(Color.BLACK), 'a', 8);

		assertTrue(rulesEngine.isInCheck(stateFor(Color.WHITE), Color.WHITE));
	}

	/**
	 * IT-CH-03: Sliding attack blocked — king not in check.
	 *
	 * White king on e1, white rook on e4, black rook on e8.  The white rook
	 * on e4 breaks the black rook's line of attack.
	 *
	 * Boundary: isPathBlocked returns true for the segment e8→e4 (passes
	 * through e4 where the white rook sits).
	 */
	@Test
	void isInCheck_slidingAttackBlocked_returnsFalse() {
		place(new King(Color.WHITE), 'e', 1);
		place(new Rook(Color.WHITE), 'e', 4);
		place(new Rook(Color.BLACK), 'e', 8);
		place(new King(Color.BLACK), 'a', 8);

		assertFalse(rulesEngine.isInCheck(stateFor(Color.WHITE), Color.WHITE));
	}

	/**
	 * IT-CH-04: Pinned piece — moving it exposes own king (discovered check).
	 *
	 * White king on e1, white rook on e4, black rook on e8.  White attempts to
	 * move the rook from e4 to a4, which removes the blocker and exposes the
	 * white king to the black rook.
	 *
	 * Collaborators: RulesEngine.isLegalMove simulates the move, calls
	 * isInCheck, and finds the king in check.
	 */
	@Test
	void pinnedPiece_movementExposesKing_isIllegal() {
		Rook whiteRook = new Rook(Color.WHITE);
		whiteRook.markMoved();
		place(new King(Color.WHITE), 'e', 1);
		place(whiteRook, 'e', 4);
		place(new Rook(Color.BLACK), 'e', 8);
		place(new King(Color.BLACK), 'a', 8);

		Move move = buildMoveWithoutApplying(whiteRook, 'e', 4, 'a', 4);

		assertFalse(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}

	/**
	 * IT-CH-05: GameModel.getStatus returns CHECK after a move that delivers check.
	 *
	 * Setup: drive GameModel to a state where white's move places the black
	 * king in check.  White queen on d1 moves to d8, checking the black king
	 * on e8.
	 *
	 * Collaborators: GameModel.applyMove → RulesEngine.getGameStatus →
	 * isInCheck.
	 */
	@Test
	void applyMove_deliversCheck_statusIsCheck() {
		Board board = new Board();
		RulesEngine rulesEngine = new RulesEngine();
		GameModel model = new GameModel(board, rulesEngine);

		for (char f = 'a'; f <= 'h'; f++) {
			for (int r = 1; r <= 8; r++) {
				board.getSquare(f, r).setOccupant(null);
			}
		}

		King whiteKing = new King(Color.WHITE);
		King blackKing = new King(Color.BLACK);
		Queen whiteQueen = new Queen(Color.WHITE);
		whiteQueen.markMoved();

		board.getSquare('e', 1).setOccupant(whiteKing);
		board.getSquare('e', 8).setOccupant(blackKing);
		board.getSquare('d', 1).setOccupant(whiteQueen);

		// White: Qd1-d8+
		Move queenMove = Move.create(whiteQueen, board.getSquare('d', 1), board.getSquare('d', 8));
		model.applyMove(queenMove);

		assertEquals(GameStatus.CHECK, model.getStatus());
	}

	// =========================================================================
	// CHECKMATE
	// =========================================================================

	/**
	 * IT-CM-01: Corner checkmate — two queens and king.
	 *
	 * White king on a1, black queen on a3 (delivers check on the a-file and
	 * covers b2/b3), black queen on c1 (covers b1/b2), black king on c3
	 * (covers b2/b3/c2/d2/d3). The white king has no legal moves: b1 is
	 * covered by Qc1; b2 is covered by both queens and the black king.  No
	 * white pieces exist to block or capture.
	 *
	 * Collaborators: RulesEngine.isCheckmate → isInCheck (true) →
	 * getLegalMoves on all white pieces (all empty lists).
	 */
	@Test
	void isCheckmate_backRankMate_returnsTrue() {
		King whiteKing = new King(Color.WHITE);
		whiteKing.markMoved();
		place(whiteKing, 'a', 1);
		place(new Queen(Color.BLACK), 'a', 3);
		place(new Queen(Color.BLACK), 'c', 1);
		place(new King(Color.BLACK), 'c', 3);

		GameState state = stateFor(Color.WHITE);

		assertTrue(rulesEngine.isInCheck(state, Color.WHITE));
		assertTrue(rulesEngine.isCheckmate(state, Color.WHITE));
	}

	/**
	 * IT-CM-02: King in check but can escape — not checkmate.
	 *
	 * White king on e1 is attacked by black queen on e8 (clear file), but d1
	 * is empty and not covered.
	 *
	 * Boundary: at least one legal move exists → isCheckmate returns false.
	 */
	@Test
	void isCheckmate_kingCanEscapeToD1_returnsFalse() {
		King whiteKing = new King(Color.WHITE);
		whiteKing.markMoved();
		place(whiteKing, 'e', 1);
		place(new Queen(Color.BLACK), 'e', 8);
		place(new King(Color.BLACK), 'h', 6);

		GameState state = stateFor(Color.WHITE);

		assertTrue(rulesEngine.isInCheck(state, Color.WHITE));
		assertFalse(rulesEngine.isCheckmate(state, Color.WHITE));
	}

	/**
	 * IT-CM-03: Check can be blocked — not checkmate.
	 *
	 * White king on e1 attacked by black rook on e8.  White rook on a4 can
	 * interpose on e4.
	 */
	@Test
	void isCheckmate_checkCanBeBlocked_returnsFalse() {
		King whiteKing = new King(Color.WHITE);
		whiteKing.markMoved();
		Rook whiteRook = new Rook(Color.WHITE);
		whiteRook.markMoved();

		place(whiteKing, 'e', 1);
		place(whiteRook, 'a', 4);
		place(new Rook(Color.BLACK), 'e', 8);
		place(new King(Color.BLACK), 'a', 8);

		GameState state = stateFor(Color.WHITE);

		assertTrue(rulesEngine.isInCheck(state, Color.WHITE));
		assertFalse(rulesEngine.isCheckmate(state, Color.WHITE));
	}

	/**
	 * IT-CM-04: Checking piece can be captured — not checkmate.
	 *
	 * White king on e1, black knight on f3 (checking), white bishop on g4 can
	 * capture the knight.
	 */
	@Test
	void isCheckmate_attackerCanBeCaptured_returnsFalse() {
		King whiteKing = new King(Color.WHITE);
		whiteKing.markMoved();
		Bishop whiteBishop = new Bishop(Color.WHITE);
		whiteBishop.markMoved();

		place(whiteKing, 'e', 1);
		place(whiteBishop, 'g', 4);
		place(new Knight(Color.BLACK), 'f', 3);
		place(new King(Color.BLACK), 'a', 8);

		GameState state = stateFor(Color.WHITE);

		assertTrue(rulesEngine.isInCheck(state, Color.WHITE));
		assertFalse(rulesEngine.isCheckmate(state, Color.WHITE));
	}

	/**
	 * IT-CM-05: GameModel locks the game and throws on further move after checkmate.
	 *
	 * White king on a2 steps to a1 (filler move), then black queen slides from
	 * b4 to a3, delivering checkmate.  The king on a1 cannot escape: b1 is
	 * covered by the black queen on c2 (same rank), b2 is covered by Qa3
	 * (diagonal) and the black king on c3 (adjacent).  No white piece can block
	 * or capture.
	 *
	 * After the mating move, GameModel.getStatus must return CHECKMATE and a
	 * subsequent applyMove must throw IllegalStateException.
	 *
	 * Collaborators: GameModel.applyMove → RulesEngine.getGameStatus →
	 * isCheckmate; GameModel.applyMove early-exit guard on CHECKMATE status.
	 */
	@Test
	void checkmate_gameLocksAndThrowsOnFurtherMove() {
		Board board = new Board();
		GameModel model = new GameModel(board, new RulesEngine());

		for (char f = 'a'; f <= 'h'; f++) {
			for (int r = 1; r <= 8; r++) {
				board.getSquare(f, r).setOccupant(null);
			}
		}

		// White: king a2, rook h8.
		// Black: king c3, queen c2, queen b4.
		// Plan: white Ka2→a1 (filler), then black Qb4→a3#.
		// After Ka1: b1 covered by Qc2 (rank), b2 covered by Qc2+Kc3, a3 queen checks on a-file.
		King whiteKing = new King(Color.WHITE);
		whiteKing.markMoved();
		King blackKing = new King(Color.BLACK);
		blackKing.markMoved();
		Queen blackQueenA = new Queen(Color.BLACK);
		blackQueenA.markMoved();
		Queen blackQueenB = new Queen(Color.BLACK);
		blackQueenB.markMoved();

		board.getSquare('a', 2).setOccupant(whiteKing);
		board.getSquare('c', 3).setOccupant(blackKing);
		board.getSquare('c', 2).setOccupant(blackQueenA);
		board.getSquare('b', 4).setOccupant(blackQueenB);

		// White filler: Ka2→a1.
		Move filler = Move.create(whiteKing,
				board.getSquare('a', 2), board.getSquare('a', 1));
		model.applyMove(filler);

		// Black: Qb4→a3# — checks on a-file, covers b2.
		// King on a1: b1 covered by Qc2 (same rank); b2 covered by Qa3 (diagonal) + Kc3 (adjacent).
		Move matingMove = Move.create(blackQueenB,
				board.getSquare('b', 4), board.getSquare('a', 3));
		model.applyMove(matingMove);

		assertEquals(GameStatus.CHECKMATE, model.getStatus(),
				"Status must be CHECKMATE after the mating move");

		assertThrows(IllegalStateException.class, () ->
				model.applyMove(Move.create(blackQueenA,
						board.getSquare('c', 2), board.getSquare('c', 1))));
	}

	// =========================================================================
	// CASTLING
	// =========================================================================

	/**
	 * IT-CS-01: Valid white kingside castling.
	 *
	 * White king e1 (hasMoved == false), white rook h1 (hasMoved == false),
	 * f1 and g1 empty, none of e1/f1/g1 attacked.
	 *
	 * After simulating applyMove mutations: king on g1, rook on f1.
	 */
	@Test
	void castling_whiteKingside_isLegalAndBoardCorrect() {
		King king = new King(Color.WHITE);
		Rook rook = new Rook(Color.WHITE);
		place(king, 'e', 1);
		place(rook, 'h', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move castleMove = buildMoveWithoutApplying(king, 'e', 1, 'g', 1);
		castleMove.setCastle(true);

		GameState state = stateFor(Color.WHITE);

		assertTrue(rulesEngine.isCastlingLegal(castleMove, state));

		board.movePiece(board.getSquare('e', 1), board.getSquare('g', 1));
		board.movePiece(board.getSquare('h', 1), board.getSquare('f', 1));

		assertSame(king, board.getSquare('g', 1).getOccupant(), "King must be on g1");
		assertSame(rook, board.getSquare('f', 1).getOccupant(), "Rook must be on f1");
		assertNull(board.getSquare('e', 1).getOccupant(), "e1 must be empty");
		assertNull(board.getSquare('h', 1).getOccupant(), "h1 must be empty");
	}

	/**
	 * IT-CS-02: Valid white queenside castling.
	 *
	 * White king e1 (hasMoved == false), white rook a1 (hasMoved == false),
	 * b1/c1/d1 empty, none of e1/d1/c1 attacked.
	 *
	 * After simulating applyMove mutations: king on c1, rook on d1.
	 */
	@Test
	void castling_whiteQueenside_isLegalAndBoardCorrect() {
		King king = new King(Color.WHITE);
		Rook rook = new Rook(Color.WHITE);
		place(king, 'e', 1);
		place(rook, 'a', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move castleMove = buildMoveWithoutApplying(king, 'e', 1, 'c', 1);
		castleMove.setCastle(true);

		GameState state = stateFor(Color.WHITE);

		assertTrue(rulesEngine.isCastlingLegal(castleMove, state));

		board.movePiece(board.getSquare('e', 1), board.getSquare('c', 1));
		board.movePiece(board.getSquare('a', 1), board.getSquare('d', 1));

		assertSame(king, board.getSquare('c', 1).getOccupant(), "King must be on c1");
		assertSame(rook, board.getSquare('d', 1).getOccupant(), "Rook must be on d1");
		assertNull(board.getSquare('e', 1).getOccupant(), "e1 must be empty");
		assertNull(board.getSquare('a', 1).getOccupant(), "a1 must be empty");
	}

	/**
	 * IT-CS-03: Valid black kingside castling.
	 *
	 * Black king e8 (hasMoved == false), black rook h8 (hasMoved == false),
	 * f8 and g8 empty, none of e8/f8/g8 attacked.
	 */
	@Test
	void castling_blackKingside_isLegal() {
		King king = new King(Color.BLACK);
		place(king, 'e', 8);
		place(new Rook(Color.BLACK), 'h', 8);
		place(new King(Color.WHITE), 'e', 1);

		Move castleMove = buildMoveWithoutApplying(king, 'e', 8, 'g', 8);
		castleMove.setCastle(true);

		assertTrue(rulesEngine.isCastlingLegal(castleMove, stateFor(Color.BLACK)));
	}

	/**
	 * IT-CS-04: Kingside path blocked — piece on f1.
	 */
	@Test
	void castling_kingsidePathBlocked_isIllegal() {
		King king = new King(Color.WHITE);
		place(king, 'e', 1);
		place(new Bishop(Color.WHITE), 'f', 1);   // blocker
		place(new Rook(Color.WHITE), 'h', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move castleMove = buildMoveWithoutApplying(king, 'e', 1, 'g', 1);

		GameState state = stateFor(Color.WHITE);

		assertFalse(rulesEngine.isCastlingLegal(castleMove, state),
				"isCastlingLegal must return false when f1 is occupied");

		List<Square> legalMoves = rulesEngine.getLegalMoves(state, board.getSquare('e', 1));
		assertFalse(legalMoves.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 1),
				"getLegalMoves must not include g1 when the path is blocked");
	}

	/**
	 * IT-CS-05: Queenside path blocked — piece on d1.
	 */
	@Test
	void castling_queensidePathBlocked_isIllegal() {
		King king = new King(Color.WHITE);
		place(king, 'e', 1);
		place(new Queen(Color.WHITE), 'd', 1);    // blocker
		place(new Rook(Color.WHITE), 'a', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move castleMove = buildMoveWithoutApplying(king, 'e', 1, 'c', 1);

		GameState state = stateFor(Color.WHITE);

		assertFalse(rulesEngine.isCastlingLegal(castleMove, state));

		List<Square> legalMoves = rulesEngine.getLegalMoves(state, board.getSquare('e', 1));
		assertFalse(legalMoves.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 1),
				"getLegalMoves must not include c1 when the path is blocked");
	}
}
