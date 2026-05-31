package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelTest {

	// =========================================================================
	// Helpers
	// =========================================================================
	private Player humanWhite() { return new Player(Color.WHITE, true); }
	private Player humanBlack() { return new Player(Color.BLACK, true); }

	// -------------------------------------------------------------------------
	// TC1: Valid construction returns non-null model with ONGOING status
	// -------------------------------------------------------------------------
	@Test
	void newGame_validPlayers_returnsNonNullModelWithOngoingStatus() {
		GameModel model = GameModel.newGame(humanWhite(), humanBlack());

		assertNotNull(model);
		assertEquals(GameStatus.ONGOING, model.getStatus());
	}

	// -------------------------------------------------------------------------
	// TC2: Null white player
	// -------------------------------------------------------------------------
	@Test
	void newGame_nullWhitePlayer_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(null, humanBlack()));
	}


	// -------------------------------------------------------------------------
	// TC3: Null black player
	// -------------------------------------------------------------------------
	@Test
	void newGame_nullBlackPlayer_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(humanWhite(), null));
	}

	// -------------------------------------------------------------------------
	// TC4: Both players null
	// -------------------------------------------------------------------------
	@Test
	void newGame_bothPlayersNull_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(null, null));
	}

	// -------------------------------------------------------------------------
	// TC5: White player has wrong colour
	// -------------------------------------------------------------------------
	@Test
	void newGame_whitePlayerHasBlackColor_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(new Player(Color.BLACK, true), humanBlack()));
	}

	// -------------------------------------------------------------------------
	// TC6: Black player has wrong colour
	// -------------------------------------------------------------------------
	@Test
	void newGame_blackPlayerHasWhiteColor_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				GameModel.newGame(humanWhite(), new Player(Color.WHITE, true)));
	}
}
