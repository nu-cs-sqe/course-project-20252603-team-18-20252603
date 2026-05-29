package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

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

	// -------------------------------------------------------------------------
	// TC4: Get color on white player returns white
	// -------------------------------------------------------------------------
	@Test
	void getColor_whitePlayer_returnsWhite() {
		Player player = new Player(Color.WHITE, true);

		assertEquals(Color.WHITE, player.getColor());
	}

	// -------------------------------------------------------------------------
	// TC5: Get color on black player returns black
	// -------------------------------------------------------------------------
	@Test
	void getColor_blackPlayer_returnsBlack() {
		Player player = new Player(Color.BLACK, false);

		assertEquals(Color.BLACK, player.getColor());
	}

	// -------------------------------------------------------------------------
	// TC6: Human player returns true
	// -------------------------------------------------------------------------
	@Test
	void isHuman_humanPlayer_returnsTrue() {
		Player player = new Player(Color.WHITE, true);

		assertTrue(player.isHuman());
	}

	// -------------------------------------------------------------------------
	// TC7: Computer player returns false
	// -------------------------------------------------------------------------
	@Test
	void isHuman_computerPlayer_returnsFalse() {
		Player player = new Player(Color.BLACK, false);

		assertFalse(player.isHuman());
	}

	// -------------------------------------------------------------------------
	// TC8: New player has no captured pieces
	// -------------------------------------------------------------------------
	@Test
	void getCapturedPieces_newPlayer_returnsEmptyList() {
		Player player = new Player(Color.WHITE, true);

		assertTrue(player.getCapturedPieces().isEmpty());
	}

	// -------------------------------------------------------------------------
	// TC9: Player with one captured piece returns list containing that piece
	// -------------------------------------------------------------------------
	@Test
	void getCapturedPieces_oneCapturedPiece_returnsListWithPiece() {
		Player player = new Player(Color.WHITE, true);
		Piece capturedPiece = new Pawn(Color.BLACK);

		player.addCapturedPiece(capturedPiece);

		List<Piece> capturedPieces = player.getCapturedPieces();

		assertEquals(1, capturedPieces.size());
		assertTrue(capturedPieces.contains(capturedPiece));
	}

	// -------------------------------------------------------------------------
	// TC10: Player with multiple captured pieces returns all captured pieces
	// -------------------------------------------------------------------------
	@Test
	void getCapturedPieces_multipleCapturedPieces_returnsAllPieces() {
		Player player = new Player(Color.WHITE, true);
		Piece firstCapturedPiece = new Pawn(Color.BLACK);
		Piece secondCapturedPiece = new Rook(Color.BLACK);

		player.addCapturedPiece(firstCapturedPiece);
		player.addCapturedPiece(secondCapturedPiece);

		List<Piece> capturedPieces = player.getCapturedPieces();

		assertEquals(2, capturedPieces.size());
		assertTrue(capturedPieces.contains(firstCapturedPiece));
		assertTrue(capturedPieces.contains(secondCapturedPiece));
	}

	// -------------------------------------------------------------------------
	// TC11: Add one captured piece
	// -------------------------------------------------------------------------
	@Test
	void addCapturedPiece_validPiece_addsPiece() {
		Player player = new Player(Color.WHITE, true);
		Piece capturedPiece = new Bishop(Color.BLACK);

		player.addCapturedPiece(capturedPiece);

		List<Piece> capturedPieces = player.getCapturedPieces();

		assertEquals(1, capturedPieces.size());
		assertEquals(capturedPiece, capturedPieces.get(0));
	}
}