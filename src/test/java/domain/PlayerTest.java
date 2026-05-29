package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}