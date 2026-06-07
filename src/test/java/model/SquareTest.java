package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.easymock.EasyMock.createMock;

class SquareTest {

	@Test
	void constructor_validMinimumBoundaries_createsSquare() {
		// Act
		Square square = Square.create('a', 1);

		// Assert
		assertEquals('a', square.getFile());
		assertEquals(1, square.getRank());
		assertTrue(square.isEmpty(), "A newly created square should be empty");
	}

	@Test
	void constructor_validMaximumBoundaries_createsSquare() {
		Square square = Square.create('h', 8);

		assertEquals('h', square.getFile());
		assertEquals(8, square.getRank());
		assertTrue(square.isEmpty());
	}

	@Test
	void constructor_invalidFileAbove_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			Square.create('i', 1);
		}, "Should throw exception for file 'i'");
	}

	@Test
	void constructor_invalidFileBelow_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			Square.create('`', 1);
		}, "Should throw exception for file '`'");
	}

	@Test
	void constructor_invalidRankAbove_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			Square.create('a', 9);
		}, "Should throw exception for rank 9");
	}

	@Test
	void constructor_invalidRankBelow_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			Square.create('a', 0);
		}, "Should throw exception for rank 9");
	}
	//method on test: setOccupant
	@Test
	void setOccupant_validPiece_updatesSquareToNotEmpty() {
		Square square = Square.create('e', 4);
		Piece mockPiece = createMock(Piece.class);

		square.setOccupant(mockPiece);

		assertFalse(square.isEmpty(), "Square should not be empty after a piece is set.");
	}

	@Test
	void setOccupant_null_updatesSquareToEmpty() {
		Square square = Square.create('e', 4);

		Piece mockPiece = createMock(Piece.class);
		square.setOccupant(mockPiece);

		square.setOccupant(null);

		assertTrue(square.isEmpty(), "Square should be empty after occupant is set to null.");
	}

	@Test
	void getOccupant_emptySquare_returnsNull() {
		Square square = Square.create('e', 4);

		assertNull(square.getOccupant(), "getOccupant should return null for a square with no occupant set.");
	}

	@Test
	void getOccupant_occupiedSquare_returnsPiece() {
		Square square = Square.create('e', 4);
		Piece mockPiece = createMock(Piece.class);
		square.setOccupant(mockPiece);

		assertSame(mockPiece, square.getOccupant(), "getOccupant should return the exact Piece instance that was set.");
	}
}