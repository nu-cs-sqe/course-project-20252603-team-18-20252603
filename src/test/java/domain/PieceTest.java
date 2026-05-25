package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PieceTest {

	@Test
	void create_validWhiteQueen_createsPiece() {
		Piece piece = Piece.create(Color.WHITE, PieceType.QUEEN);

		assertEquals(Color.WHITE, piece.getColor());
		assertEquals(PieceType.QUEEN, piece.getType());
		assertFalse(piece.hasMoved());
	}

	@Test
	void create_validBlackPawn_createsPiece() {
		Piece piece = Piece.create(Color.BLACK, PieceType.PAWN);

		assertEquals(Color.BLACK, piece.getColor());
		assertEquals(PieceType.PAWN, piece.getType());
		assertFalse(piece.hasMoved());
	}

	@Test
	void create_nullColor_throwsException() {
		Color color = null;
		PieceType type = PieceType.QUEEN;

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Piece.create(color, type);
		});

		assertEquals("Color can't be null.", exception.getMessage());
	}

	@Test
	void create_nullType_throwsException() {
		Color color = Color.WHITE;
		PieceType type = null;

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Piece.create(color, type);
		});

		assertEquals("Piece type can't be null.", exception.getMessage());
	}

	@Test
	void create_nullColorAndNullType_throwsException() {
		Color color = null;
		PieceType type = null;

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			Piece.create(color, type);
		});

		assertEquals("Color can't be null.", exception.getMessage());
	}

	@Test
	void getColor_whitePiece_returnsWhite() {
		Piece piece = Piece.create(Color.WHITE, PieceType.ROOK);

		assertEquals(Color.WHITE, piece.getColor());
	}

	@Test
	void getColor_blackPiece_returnsBlack() {
		Piece piece = Piece.create(Color.BLACK, PieceType.BISHOP);

		assertEquals(Color.BLACK, piece.getColor());
	}

	@Test
	void getType_queenPiece_returnsQueen() {
		Piece piece = Piece.create(Color.WHITE, PieceType.QUEEN);

		assertEquals(PieceType.QUEEN, piece.getType());
	}

	@Test
	void getType_pawnPiece_returnsPawn() {
		Piece piece = Piece.create(Color.BLACK, PieceType.PAWN);

		assertEquals(PieceType.PAWN, piece.getType());
	}

	@Test
	void hasMoved_newPiece_returnsFalse() {
		Piece piece = Piece.create(Color.WHITE, PieceType.KNIGHT);

		assertFalse(piece.hasMoved());
	}

	@Test
	void hasMoved_afterMarkMoved_returnsTrue() {
		Piece piece = Piece.create(Color.WHITE, PieceType.KNIGHT);

		piece.markMoved();

		assertTrue(piece.hasMoved());
	}

	@Test
	void markMoved_newPiece_updatesHasMovedToTrue() {
		Piece piece = Piece.create(Color.BLACK, PieceType.QUEEN);

		piece.markMoved();

		assertTrue(piece.hasMoved());
	}

	@Test
	void markMoved_alreadyMovedPiece_remainsTrue() {
		Piece piece = Piece.create(Color.BLACK, PieceType.QUEEN);

		piece.markMoved();
		piece.markMoved();

		assertTrue(piece.hasMoved());
	}
}