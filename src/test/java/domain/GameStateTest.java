package domain;

import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

	// -------------------------------------------------------------------------
	// TC1: Valid Construction — White Turn, No Last Move
	// -------------------------------------------------------------------------
	@Test
	void constructor_validWhiteTurnNullLastMove_createsGameState() {
		Board board = createMock(Board.class);
		replay(board);

		GameState state = GameState.create(board, Color.WHITE, null);

		assertSame(board, state.getBoard());
		assertEquals(Color.WHITE, state.getCurrentTurn());
		assertNull(state.getLastMove());

		verify(board);
	}

	// -------------------------------------------------------------------------
	// TC2: Valid Construction — Black Turn, No Last Move
	// -------------------------------------------------------------------------
	@Test
	void constructor_validBlackTurnNullLastMove_createsGameState() {
		Board board = createMock(Board.class);
		replay(board);

		GameState state = GameState.create(board, Color.BLACK, null);

		assertSame(board, state.getBoard());
		assertEquals(Color.BLACK, state.getCurrentTurn());
		assertNull(state.getLastMove());

		verify(board);
	}

	// -------------------------------------------------------------------------
	// TC3: Valid Construction — With Last Move Supplied
	// -------------------------------------------------------------------------
	@Test
	void constructor_validWithLastMove_createsGameState() {
		Board board = createMock(Board.class);
		Move lastMove = createMock(Move.class);
		replay(board, lastMove);

		GameState state = GameState.create(board, Color.BLACK, lastMove);

		assertSame(board, state.getBoard());
		assertEquals(Color.BLACK, state.getCurrentTurn());
		assertSame(lastMove, state.getLastMove());

		verify(board, lastMove);
	}

	// -------------------------------------------------------------------------
	// TC4: Null Board
	// -------------------------------------------------------------------------
	@Test
	void constructor_nullBoard_throwsException() {
		Move lastMove = createMock(Move.class);
		replay(lastMove);

		assertThrows(IllegalArgumentException.class, () -> {
			GameState.create(null, Color.WHITE, lastMove);
		});

		verify(lastMove);
	}

	// -------------------------------------------------------------------------
	// TC5: Null currentTurn
	// -------------------------------------------------------------------------
	@Test
	void constructor_nullCurrentTurn_throwsException() {
		Board board = createMock(Board.class);
		replay(board);

		assertThrows(IllegalArgumentException.class, () -> {
			GameState.create(board, null, null);
		});

		verify(board);
	}

	// -------------------------------------------------------------------------
	// TC6: Null lastMove Is Accepted
	// -------------------------------------------------------------------------
	@Test
	void create_nullLastMove_isAccepted() {
		Board board = createMock(Board.class);
		replay(board);

		GameState state = GameState.create(board, Color.WHITE, null);

		assertNull(state.getLastMove());

		verify(board);
	}

	// -------------------------------------------------------------------------
	// TC7: getBoard Returns The Board Supplied At Construction
	// -------------------------------------------------------------------------
	@Test
	void getBoard_returnsBoardSuppliedAtConstruction() {
		Board board = createMock(Board.class);
		replay(board);

		GameState state = GameState.create(board, Color.WHITE, null);

		assertSame(board, state.getBoard());

		verify(board);
	}

	// -------------------------------------------------------------------------
	// TC8: getCurrentTurn Returns WHITE When Constructed With WHITE
	// -------------------------------------------------------------------------
	@Test
	void getCurrentTurn_white_returnsWhite() {
		Board board = createMock(Board.class);
		replay(board);

		GameState state = GameState.create(board, Color.WHITE, null);

		assertEquals(Color.WHITE, state.getCurrentTurn());

		verify(board);
	}

	// -------------------------------------------------------------------------
	// TC9: getCurrentTurn Returns BLACK When Constructed With BLACK
	// -------------------------------------------------------------------------
	@Test
	void getCurrentTurn_black_returnsBlack() {
		Board board = createMock(Board.class);
		replay(board);

		GameState state = GameState.create(board, Color.BLACK, null);

		assertEquals(Color.BLACK, state.getCurrentTurn());

		verify(board);
	}

	// -------------------------------------------------------------------------
	// TC10: getLastMove Returns Null When No Last Move Was Supplied
	// -------------------------------------------------------------------------
	@Test
	void getLastMove_null_returnsNull() {
		Board board = createMock(Board.class);
		replay(board);

		GameState state = GameState.create(board, Color.WHITE, null);

		assertNull(state.getLastMove());

		verify(board);
	}

	// -------------------------------------------------------------------------
	// TC11: getLastMove Returns Last Move When One Was Supplied
	// -------------------------------------------------------------------------
	@Test
	void getLastMove_validMove_returnsSuppliedMove() {
		Board board = createMock(Board.class);
		Move lastMove = createMock(Move.class);
		replay(board, lastMove);

		GameState state = GameState.create(board, Color.WHITE, lastMove);

		assertSame(lastMove, state.getLastMove());

		verify(board, lastMove);
	}

	// -------------------------------------------------------------------------
	// TC12: Last Move Was A Two-Square Pawn Advance
	// -------------------------------------------------------------------------
	@Test
	void getLastMove_twoSquarePawnAdvance_enPassantWindowDetectable() {
		Board board = createMock(Board.class);
		Move lastMove = createMock(Move.class);
		Piece pawn = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square to = createMock(Square.class);

		expect(lastMove.getPiece()).andReturn(pawn);
		expect(pawn.getType()).andReturn(PieceType.PAWN);
		expect(lastMove.getFrom()).andReturn(from);
		expect(lastMove.getTo()).andReturn(to);
		expect(from.getRank()).andReturn(2);
		expect(to.getRank()).andReturn(4);

		replay(board, lastMove, pawn, from, to);

		GameState state = GameState.create(board, Color.BLACK, lastMove);

		Move retrievedMove = state.getLastMove();
		assertNotNull(retrievedMove);
		assertEquals(PieceType.PAWN, retrievedMove.getPiece().getType());
		int rankDifference = retrievedMove.getTo().getRank() - retrievedMove.getFrom().getRank();
		assertEquals(2, Math.abs(rankDifference));

		verify(board, lastMove, pawn, from, to);
	}

	// -------------------------------------------------------------------------
	// TC13: Last Move Was A One-Square Pawn Advance
	// -------------------------------------------------------------------------
	@Test
	void getLastMove_oneSquarePawnAdvance_noEnPassantWindow() {
		Board board = createMock(Board.class);
		Move lastMove = createMock(Move.class);
		Piece pawn = createMock(Piece.class);
		Square from = createMock(Square.class);
		Square to = createMock(Square.class);

		expect(lastMove.getPiece()).andReturn(pawn);
		expect(pawn.getType()).andReturn(PieceType.PAWN);
		expect(lastMove.getFrom()).andReturn(from);
		expect(lastMove.getTo()).andReturn(to);
		expect(from.getRank()).andReturn(2);
		expect(to.getRank()).andReturn(3);

		replay(board, lastMove, pawn, from, to);

		GameState state = GameState.create(board, Color.BLACK, lastMove);

		Move retrievedMove = state.getLastMove();
		assertNotNull(retrievedMove);
		assertEquals(PieceType.PAWN, retrievedMove.getPiece().getType());
		int rankDifference = retrievedMove.getTo().getRank() - retrievedMove.getFrom().getRank();
		assertEquals(1, Math.abs(rankDifference));

		verify(board, lastMove, pawn, from, to);
	}

	// -------------------------------------------------------------------------
	// TC14: Last Move Was Not A Pawn Move
	// -------------------------------------------------------------------------
	@Test
	void getLastMove_nonPawnMove_noEnPassantWindow() {
		Board board = createMock(Board.class);
		Move lastMove = createMock(Move.class);
		Piece rook = createMock(Piece.class);

		expect(lastMove.getPiece()).andReturn(rook);
		expect(rook.getType()).andReturn(PieceType.ROOK);

		replay(board, lastMove, rook);

		GameState state = GameState.create(board, Color.BLACK, lastMove);

		Move retrievedMove = state.getLastMove();
		assertNotNull(retrievedMove);
		assertEquals(PieceType.ROOK, retrievedMove.getPiece().getType());

		verify(board, lastMove, rook);
	}
}
