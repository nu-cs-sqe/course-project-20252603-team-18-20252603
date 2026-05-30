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

		GameState state = new GameState(board, Color.WHITE, null);

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

		GameState state = new GameState(board, Color.BLACK, null);

		assertSame(board, state.getBoard());
		assertEquals(Color.BLACK, state.getCurrentTurn());
		assertNull(state.getLastMove());

		verify(board);
	}
}
