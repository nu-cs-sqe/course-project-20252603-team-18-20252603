package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}