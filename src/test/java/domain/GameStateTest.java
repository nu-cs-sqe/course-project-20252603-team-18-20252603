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
}
