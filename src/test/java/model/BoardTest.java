package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.easymock.EasyMock.*;

class BoardTest {

	private Board board;

	@BeforeEach
	void setUp() {
		board = new Board();
	}

	// ─────────────────────────────────────────
	// getSquare(char file, int rank)
	// ─────────────────────────────────────────
	// Note: getSquare returns Squares owned by Board's internal grid,
	// so these tests verify Board constructs and returns them correctly.
	// Square mocks are not applicable here — the return value IS the Square.

	@Test
	void getSquare_validInteriorSquare_returnsSquare() {
		Square square = board.getSquare('d', 4);

		assertNotNull(square, "getSquare should return a non-null Square for a valid interior coordinate.");
		assertEquals('d', square.getFile());
		assertEquals(4, square.getRank());
	}

	@Test
	void getSquare_minimumBoundary_returnsSquare() {
		Square square = board.getSquare('a', 1);

		assertNotNull(square, "getSquare should return a non-null Square for the minimum boundary (a1).");
		assertEquals('a', square.getFile());
		assertEquals(1, square.getRank());
	}

	@Test
	void getSquare_maximumBoundary_returnsSquare() {
		Square square = board.getSquare('h', 8);

		assertNotNull(square, "getSquare should return a non-null Square for the maximum boundary (h8).");
		assertEquals('h', square.getFile());
		assertEquals(8, square.getRank());
	}

	@Test
	void getSquare_fileBelowMin_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			board.getSquare('`', 1);
		}, "Should throw exception for file '`' (ASCII 96, just below 'a').");
	}

	@Test
	void getSquare_fileAboveMax_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			board.getSquare('i', 1);
		}, "Should throw exception for file 'i' (just above 'h').");
	}

	@Test
	void getSquare_rankBelowMin_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			board.getSquare('a', 0);
		}, "Should throw exception for rank 0 (just below minimum of 1).");
	}

	@Test
	void getSquare_rankAboveMax_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			board.getSquare('a', 9);
		}, "Should throw exception for rank 9 (just above maximum of 8).");
	}

	// ─────────────────────────────────────────
	// placePiece(Piece piece, Square square)
	// ─────────────────────────────────────────

	@Test
	void placePiece_emptySquare_placesPiece() {
		Square mockSquare = createMock(Square.class);
		Piece mockPiece = createMock(Piece.class);

		expect(mockSquare.isEmpty()).andReturn(true);
		mockSquare.setOccupant(mockPiece);
		expectLastCall();
		replay(mockSquare, mockPiece);

		board.placePiece(mockPiece, mockSquare);

		verify(mockSquare, mockPiece);
	}

	@Test
	void placePiece_nullPiece_throwsException() {
		Square mockSquare = createMock(Square.class);
		replay(mockSquare);

		assertThrows(IllegalArgumentException.class, () -> {
			board.placePiece(null, mockSquare);
		}, "Should throw exception when placing a null piece.");

		verify(mockSquare);
	}

	@Test
	void placePiece_nullSquare_throwsException() {
		Piece mockPiece = createMock(Piece.class);
		replay(mockPiece);

		assertThrows(IllegalArgumentException.class, () -> {
			board.placePiece(mockPiece, null);
		}, "Should throw exception when the target square is null.");

		verify(mockPiece);
	}

	@Test
	void placePiece_occupiedSquare_throwsException() {
		Square mockSquare = createMock(Square.class);
		Piece mockPiece = createMock(Piece.class);

		expect(mockSquare.isEmpty()).andReturn(false);
		replay(mockSquare, mockPiece);

		assertThrows(IllegalStateException.class, () -> {
			board.placePiece(mockPiece, mockSquare);
		}, "Should throw exception when attempting to place a piece on an already occupied square.");

		verify(mockSquare, mockPiece);
	}

	// ─────────────────────────────────────────
	// removePiece(Square square)
	// ─────────────────────────────────────────

	@Test
	void removePiece_occupiedSquare_removesPiece() {
		Square mockSquare = createMock(Square.class);

		expect(mockSquare.isEmpty()).andReturn(false);
		mockSquare.setOccupant(null);
		expectLastCall();
		replay(mockSquare);

		board.removePiece(mockSquare);

		verify(mockSquare);
	}

	@Test
	void removePiece_nullSquare_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			board.removePiece(null);
		}, "Should throw exception when the square argument is null.");
	}

	@Test
	void removePiece_emptySquare_throwsException() {
		Square mockSquare = createMock(Square.class);

		expect(mockSquare.isEmpty()).andReturn(true);
		replay(mockSquare);

		assertThrows(IllegalStateException.class, () -> {
			board.removePiece(mockSquare);
		}, "Should throw exception when attempting to remove a piece from an already empty square.");

		verify(mockSquare);
	}

	// ─────────────────────────────────────────
	// movePiece(Square from, Square to)
	// ─────────────────────────────────────────

	@Test
	void movePiece_toEmptySquare_movesPiece() {
		Square mockFrom = createMock(Square.class);
		Square mockTo = createMock(Square.class);
		Piece mockPawn = createMock(Piece.class);

		expect(mockFrom.isEmpty()).andReturn(false);
		expect(mockFrom.getFile()).andReturn('e').anyTimes();
		expect(mockFrom.getRank()).andReturn(2).anyTimes();
		expect(mockTo.getFile()).andReturn('e').anyTimes();
		expect(mockTo.getRank()).andReturn(4).anyTimes();
		expect(mockTo.isEmpty()).andReturn(true);
		expect(mockFrom.getOccupant()).andReturn(mockPawn);
		mockFrom.setOccupant(null);
		expectLastCall();
		mockTo.setOccupant(mockPawn);
		expectLastCall();
		replay(mockFrom, mockTo, mockPawn);

		board.movePiece(mockFrom, mockTo);

		verify(mockFrom, mockTo, mockPawn);
	}

	@Test
	void movePiece_toOpponentOccupiedSquare_captures() {
		Square mockFrom = createMock(Square.class);
		Square mockTo = createMock(Square.class);
		Piece mockWhiteBishop = createMock(Piece.class);
		Piece mockBlackKnight = createMock(Piece.class);

		expect(mockFrom.isEmpty()).andReturn(false);
		expect(mockFrom.getFile()).andReturn('d').anyTimes();
		expect(mockFrom.getRank()).andReturn(5).anyTimes();
		expect(mockTo.getFile()).andReturn('f').anyTimes();
		expect(mockTo.getRank()).andReturn(7).anyTimes();
		expect(mockTo.isEmpty()).andReturn(false);
		expect(mockTo.getOccupant()).andReturn(mockBlackKnight);
		expect(mockWhiteBishop.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(mockBlackKnight.getColor()).andReturn(Color.BLACK).anyTimes();
		expect(mockFrom.getOccupant()).andReturn(mockWhiteBishop);
		mockFrom.setOccupant(null);
		expectLastCall();
		mockTo.setOccupant(mockWhiteBishop);
		expectLastCall();
		replay(mockFrom, mockTo, mockWhiteBishop, mockBlackKnight);

		board.movePiece(mockFrom, mockTo);

		verify(mockFrom, mockTo, mockWhiteBishop, mockBlackKnight);
	}

	@Test
	void movePiece_nullFrom_throwsException() {
		Square mockTo = createMock(Square.class);
		replay(mockTo);

		assertThrows(IllegalArgumentException.class, () -> {
			board.movePiece(null, mockTo);
		}, "Should throw exception when the from square is null.");
	}

	@Test
	void movePiece_nullTo_throwsException() {
		Square mockFrom = createMock(Square.class);
		replay(mockFrom);

		assertThrows(IllegalArgumentException.class, () -> {
			board.movePiece(mockFrom, null);
		}, "Should throw exception when the to square is null.");
	}

	@Test
	void movePiece_fromSquareEmpty_throwsException() {
		Square mockFrom = createMock(Square.class);
		Square mockTo = createMock(Square.class);

		expect(mockFrom.isEmpty()).andReturn(true);
		replay(mockFrom, mockTo);

		assertThrows(IllegalStateException.class, () -> {
			board.movePiece(mockFrom, mockTo);
		}, "Should throw exception when attempting to move from an empty square.");

		verify(mockFrom, mockTo);
	}

	@Test
	void movePiece_fromAndToSameSquare_throwsException() {
		Square mockFrom = createMock(Square.class);

		expect(mockFrom.getFile()).andReturn('d').anyTimes();
		expect(mockFrom.getRank()).andReturn(4).anyTimes();
		replay(mockFrom);

		assertThrows(IllegalArgumentException.class, () -> {
			board.movePiece(mockFrom, mockFrom);
		}, "Should throw exception when from and to reference the same square.");

		verify(mockFrom);
	}

	@Test
	void movePiece_toFriendlyOccupiedSquare_throwsException() {
		Square mockFrom = createMock(Square.class);
		Square mockTo = createMock(Square.class);
		Piece mockWhiteRook = createMock(Piece.class);
		Piece mockWhitePawn = createMock(Piece.class);

		expect(mockFrom.isEmpty()).andReturn(false);
		expect(mockFrom.getFile()).andReturn('a').anyTimes();
		expect(mockFrom.getRank()).andReturn(1).anyTimes();
		expect(mockTo.getFile()).andReturn('a').anyTimes();
		expect(mockTo.getRank()).andReturn(2).anyTimes();
		expect(mockTo.isEmpty()).andReturn(false);
		expect(mockTo.getOccupant()).andReturn(mockWhitePawn);
		expect(mockFrom.getOccupant()).andReturn(mockWhiteRook).anyTimes();
		expect(mockWhiteRook.getColor()).andReturn(Color.WHITE).anyTimes();
		expect(mockWhitePawn.getColor()).andReturn(Color.WHITE).anyTimes();
		replay(mockFrom, mockTo, mockWhiteRook, mockWhitePawn);

		assertThrows(IllegalArgumentException.class, () -> {
			board.movePiece(mockFrom, mockTo);
		}, "Should throw exception when attempting to move to a square occupied by a friendly piece.");

		verify(mockFrom, mockTo, mockWhiteRook, mockWhitePawn);
	}
}