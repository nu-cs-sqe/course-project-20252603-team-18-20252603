package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest {

	// -------------------------------------------------------------------------
	// TC1: Constructor creates white human player
	// -------------------------------------------------------------------------
	@Test
	void constructor_validWhiteHumanPlayer_createsPlayer() {
		Player player = new Player(Color.WHITE, true);

		assertEquals(Color.WHITE, player.getColor());
		assertTrue(player.isHuman());
		assertTrue(player.getCapturedPieces().isEmpty());
	}

	// -------------------------------------------------------------------------
	// TC2: Constructor creates black computer player
	// -------------------------------------------------------------------------
	@Test
	void constructor_validBlackComputerPlayer_createsPlayer() {
		Player player = new Player(Color.BLACK, false);

		assertEquals(Color.BLACK, player.getColor());
		assertFalse(player.isHuman());
		assertTrue(player.getCapturedPieces().isEmpty());
	}

	// -------------------------------------------------------------------------
	// TC3: Constructor with null color throws exception
	// -------------------------------------------------------------------------
	@Test
	void constructor_nullColor_throwsException() {
		Color color = null;
		boolean isHuman = true;

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			new Player(color, isHuman);
		});

		assertEquals("Color can't be null.", exception.getMessage());
	}
}