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
}
