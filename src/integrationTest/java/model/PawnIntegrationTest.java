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

	/** Build a GameState with the given turn and a recorded last move. */
	private GameState stateFor(Color turn, Move lastMove) {
		return GameState.create(board, turn, lastMove);
	}

	/**
	 * Build a Move that records a pawn traveling from {@code fromFile/fromRank}
	 * to {@code toFile/toRank} without permanently altering the board.
	 *
	 * <p>Move.create() requires the piece to be the occupant of the from-square
	 * at the moment of creation.  This helper temporarily swaps occupants, builds
	 * the Move, then restores the board to its original state.
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

	/**
	 * IT-PC-02: Diagonal move — destination empty, no en passant.
	 *
	 * A white pawn on e4 must not move diagonally to f5 when f5 is empty and
	 * no en passant target exists.
	 *
	 * <p>Boundary: the diagonal path requires an opponent piece on the target
	 * square; an empty diagonal is not a valid pawn destination.
	 */
	@Test
	void whitePawnDiagonalMove_squareEmpty_isIllegal() {
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();
		place(whitePawn, 'e', 4);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move move = Move.create(whitePawn, board.getSquare('e', 4), board.getSquare('f', 5));

		assertFalse(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}

	/**
	 * IT-PC-03: Forward move into occupied square — blocked.
	 *
	 * A white pawn on e4 must not advance to e5 when e5 is occupied by a black
	 * pawn.  Pawns cannot capture straight ahead.
	 *
	 * <p>Boundary: isNormalPawnMoveLegal straight-one-forward branch returns
	 * toBoardSquare.isEmpty(), which is false here.
	 */
	@Test
	void whitePawnForwardMove_squareOccupied_isIllegal() {
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();
		place(whitePawn, 'e', 4);
		place(new Pawn(Color.BLACK), 'e', 5);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move move = Move.create(whitePawn, board.getSquare('e', 4), board.getSquare('e', 5));

		assertFalse(rulesEngine.isLegalMove(move, stateFor(Color.WHITE)));
	}

	// =========================================================================
	// EN PASSANT
	// =========================================================================

	/**
	 * IT-EP-01: Happy path — white captures black en passant.
	 *
	 * Setup: black pawn just double-stepped from d7 to d5; white pawn is on e5.
	 * White plays exd6 en passant.
	 *
	 * <p>Assertions after simulating applyMove's board mutations:
	 * <ul>
	 *   <li>White pawn occupies d6 (the landing square).</li>
	 *   <li>e5 is empty (white pawn departed).</li>
	 *   <li>d5 is empty (captured black pawn removed by the en passant branch).</li>
	 * </ul>
	 *
	 * <p>Key integration path: GameState.lastMove → isEnpassantLegal reads
	 * previousMove.getPiece() with reference equality (line 508 of RulesEngine),
	 * so the exact same Pawn object must be both on the board at d5 and stored
	 * in the last-move record.  buildMoveWithoutApplying() ensures this.
	 */
	@Test
	void enPassant_whiteCapturesBlack_boardStateCorrectAfterMove() {
		Pawn blackPawn = new Pawn(Color.BLACK);
		blackPawn.markMoved();
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();

		place(whitePawn, 'e', 5);
		place(blackPawn, 'd', 5);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move blackDoubleStep = buildMoveWithoutApplying(blackPawn, 'd', 7, 'd', 5);

		GameState state = stateFor(Color.WHITE, blackDoubleStep);

		Square fromSquare = board.getSquare('e', 5);
		Square toSquare = board.getSquare('d', 6);
		Move enPassantMove = Move.create(whitePawn, fromSquare, toSquare);
		enPassantMove.setEnPassant(true);

		assertTrue(rulesEngine.isEnpassantLegal(enPassantMove, state),
				"En passant should be legal in this position");

		// Simulate the board mutations that GameModel.applyMove performs
		board.movePiece(fromSquare, toSquare);
		board.getSquare('d', 5).setOccupant(null);

		assertNull(board.getSquare('e', 5).getOccupant(),
				"e5 must be empty after white pawn departs");
		assertNull(board.getSquare('d', 5).getOccupant(),
				"d5 must be empty — captured black pawn must be removed");
		assertSame(whitePawn, board.getSquare('d', 6).getOccupant(),
				"White pawn must occupy d6 after en passant");
	}

	/**
	 * IT-EP-02: Happy path — black captures white en passant.
	 *
	 * Setup: white pawn just double-stepped from e2 to e4; black pawn is on d4.
	 * Black plays dxe3 en passant.
	 *
	 * <p>Assertions after simulating applyMove's board mutations:
	 * <ul>
	 *   <li>Black pawn occupies e3.</li>
	 *   <li>d4 is empty.</li>
	 *   <li>e4 is empty (captured white pawn removed).</li>
	 * </ul>
	 *
	 * <p>Mirrors IT-EP-01 for the black side.  Direction-dependent rank
	 * calculations in isEnpassantLegal differ: enPassantTargetRank == 3 for
	 * black, and the GameState has currentTurn == BLACK.
	 */
	@Test
	void enPassant_blackCapturesWhite_boardStateCorrectAfterMove() {
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();
		Pawn blackPawn = new Pawn(Color.BLACK);
		blackPawn.markMoved();

		place(whitePawn, 'e', 4);
		place(blackPawn, 'd', 4);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move whiteDoubleStep = buildMoveWithoutApplying(whitePawn, 'e', 2, 'e', 4);

		GameState state = stateFor(Color.BLACK, whiteDoubleStep);

		Square fromSquare = board.getSquare('d', 4);
		Square toSquare = board.getSquare('e', 3);
		Move enPassantMove = Move.create(blackPawn, fromSquare, toSquare);
		enPassantMove.setEnPassant(true);

		assertTrue(rulesEngine.isEnpassantLegal(enPassantMove, state),
				"En passant should be legal for black in this position");

		// Simulate applyMove board mutations for black's en passant:
		board.movePiece(fromSquare, toSquare);
		board.getSquare('e', 4).setOccupant(null);

		assertNull(board.getSquare('d', 4).getOccupant(),
				"d4 must be empty after black pawn departs");
		assertNull(board.getSquare('e', 4).getOccupant(),
				"e4 must be empty — captured white pawn must be removed");
		assertSame(blackPawn, board.getSquare('e', 3).getOccupant(),
				"Black pawn must occupy e3 after en passant");
	}

	/**
	 * IT-EP-03: Opportunity expired — en passant window has closed.
	 *
	 * Setup: black pawn is on d5, white pawn is on e5, but the most recent
	 * move recorded is a white king step rather than the black pawn's double
	 * advance.
	 *
	 * <p>isEnpassantLegal checks gameState.getLastMove().  When the last move
	 * is not a pawn double-step by the opponent, the method returns false at the
	 * previousPiece.getType() != PAWN check (line 460 of RulesEngine).
	 */
	@Test
	void enPassant_opportunityExpired_isIllegal() {
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();
		Pawn blackPawn = new Pawn(Color.BLACK);
		blackPawn.markMoved();
		King whiteKing = new King(Color.WHITE);
		whiteKing.markMoved();

		place(whitePawn, 'e', 5);
		place(blackPawn, 'd', 5);
		place(whiteKing, 'e', 1);
		place(new King(Color.BLACK), 'e', 8);

		Move kingMove = buildMoveWithoutApplying(whiteKing, 'e', 1, 'd', 1);

		GameState state = stateFor(Color.WHITE, kingMove);

		Square fromSquare = board.getSquare('e', 5);
		Square toSquare = board.getSquare('d', 6);
		Move enPassantAttempt = Move.create(whitePawn, fromSquare, toSquare);
		enPassantAttempt.setEnPassant(true);

		assertFalse(rulesEngine.isEnpassantLegal(enPassantAttempt, state),
				"En passant must be illegal when the last move was not a pawn double-step");
	}

	/**
	 * IT-EP-04: En passant would expose own king — discovered check.
	 *
	 * Setup:
	 * <pre>
	 *   a5: black rook    (pins the entire 5th rank)
	 *   d5: white pawn    (the capturing pawn)
	 *   c5: black pawn    (would-be en passant target; just double-stepped c7→c5)
	 *   e5: white king
	 *   e8: black king
	 * </pre>
	 *
	 * Executing dxc6 en passant would remove both the white pawn from d5 and
	 * the black pawn from c5, leaving the white king on e5 with a clear line
	 * to the black rook on a5 (b5 is empty; c5 and d5 would be vacated).
	 *
	 * <p>isEnpassantLegal's final guard temporarily clears the pawn squares,
	 * calls isInCheck, and must return false when the king is exposed.
	 */
	@Test
	void enPassant_wouldExposeOwnKing_isIllegal() {
		King whiteKing = new King(Color.WHITE);
		whiteKing.markMoved();
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();
		Pawn blackPawn = new Pawn(Color.BLACK);
		blackPawn.markMoved();

		place(whiteKing, 'e', 5);
		place(whitePawn, 'd', 5);
		place(blackPawn, 'c', 5);
		place(new Rook(Color.BLACK), 'a', 5);
		place(new King(Color.BLACK), 'e', 8);

		Move blackDoubleStep = buildMoveWithoutApplying(blackPawn, 'c', 7, 'c', 5);

		GameState state = stateFor(Color.WHITE, blackDoubleStep);

		Square fromSquare = board.getSquare('d', 5);
		Square toSquare = board.getSquare('c', 6);
		Move enPassantAttempt = Move.create(whitePawn, fromSquare, toSquare);
		enPassantAttempt.setEnPassant(true);

		assertFalse(rulesEngine.isEnpassantLegal(enPassantAttempt, state),
				"En passant that exposes the king to the rook on a5 must be illegal");
	}

	// =========================================================================
	// PROMOTION
	// =========================================================================

	/**
	 * IT-PR-01: Pawn reaches final rank — board state correct after promotion.
	 *
	 * A white pawn on e7 advances to e8 with promotionPiece = Queen.
	 *
	 * <p>Assertions after simulating applyMove's promotion board-swap
	 * (movePiece → removePiece → placePiece):
	 * <ul>
	 *   <li>e7 is empty (pawn departed).</li>
	 *   <li>e8 is occupied by a white Queen.</li>
	 * </ul>
	 *
	 * <p>Collaborators exercised: RulesEngine.isPromotionLegal (approves the
	 * move), then Board.movePiece / removePiece / placePiece (the swap logic
	 * mirrored from GameModel.applyMove).
	 */
	@Test
	void promotion_whitePawnReachesFinalRank_replacedByQueen() {
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();
		place(whitePawn, 'e', 7);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'h', 8);

		Square fromSquare = board.getSquare('e', 7);
		Square toSquare = board.getSquare('e', 8);

		Queen promotionQueen = new Queen(Color.WHITE);
		Move promotionMove = Move.create(whitePawn, fromSquare, toSquare);
		promotionMove.setPromotionPiece(promotionQueen);

		GameState state = stateFor(Color.WHITE);
		assertTrue(rulesEngine.isPromotionLegal(promotionMove, state),
				"Promotion to queen must be considered legal");

		// Simulate GameModel.applyMove's promotion branch:
		board.movePiece(fromSquare, toSquare);
		board.removePiece(toSquare);
		board.placePiece(promotionQueen, toSquare);

		assertNull(board.getSquare('e', 7).getOccupant(),
				"e7 must be empty after promotion");

		Piece occupant = board.getSquare('e', 8).getOccupant();
		assertNotNull(occupant,
				"e8 must be occupied by the promoted piece");
		assertEquals(PieceType.QUEEN, occupant.getType(),
				"Promoted piece must be a queen");
		assertEquals(Color.WHITE, occupant.getColor(),
				"Promoted piece must be white");
	}

	/**
	 * IT-PR-02: promotionPiece is null on the final rank.
	 *
	 * A white pawn on e7 moves to e8 but the move carries no promotionPiece.
	 * isPromotionLegal must return false — a promotion piece is mandatory when
	 * a pawn reaches the back rank.
	 *
	 * <p>Boundary: the null check at RulesEngine line 395
	 * ({@code promotionPiece == null → return false}).
	 */
	@Test
	void promotion_nullPromotionPiece_isPromotionLegalReturnsFalse() {
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();
		place(whitePawn,            'e', 7);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'h', 8);

		Square fromSquare = board.getSquare('e', 7);
		Square toSquare   = board.getSquare('e', 8);
		Move move = Move.create(whitePawn, fromSquare, toSquare);

		assertFalse(rulesEngine.isPromotionLegal(move, stateFor(Color.WHITE)),
				"isPromotionLegal must return false when promotionPiece is null");
	}

	/**
	 * IT-PR-03: Promotion to King is forbidden.
	 *
	 * A white pawn on e7 moves to e8 with promotionPiece = King.
	 * isPromotionLegal must return false.
	 *
	 * <p>Boundary: the King/Pawn exclusion check at RulesEngine line 397
	 * ({@code promotionPiece.getType() == PieceType.KING → return false}).
	 */
	@Test
	void promotion_toKing_isPromotionLegalReturnsFalse() {
		Pawn whitePawn = new Pawn(Color.WHITE);
		whitePawn.markMoved();
		place(whitePawn,            'e', 7);
		place(new King(Color.WHITE), 'e', 1);
		place(new King(Color.BLACK), 'h', 8);

		Square fromSquare = board.getSquare('e', 7);
		Square toSquare   = board.getSquare('e', 8);
		Move move = Move.create(whitePawn, fromSquare, toSquare);
		move.setPromotionPiece(new King(Color.WHITE));

		assertFalse(rulesEngine.isPromotionLegal(move, stateFor(Color.WHITE)),
				"isPromotionLegal must return false when promotionPiece is a King");
	}
}