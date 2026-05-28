package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RookTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nullFrom_throwsException() {
		Rook rook = new Rook(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			rook.getLegalMoveDestinationSquares(null);
		});
	}

	// -------------------------------------------------------------------------
	// TC2: From Square Not Occupied By This Rook
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fromNotOccupiedByThisRook_throwsException() {
		Rook rook = new Rook(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(null);
		replay(from);

		assertThrows(IllegalArgumentException.class, () -> {
			rook.getLegalMoveDestinationSquares(from);
		});

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC3: Rook From Center Contains All Four Straight Directions
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rookFromCenter_containsAllFourStraightDirections() {
		Rook rook = new Rook(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(rook);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = rook.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'e', 4));
		assertTrue(containsSquare(candidates, 'c', 4));
		assertTrue(containsSquare(candidates, 'd', 5));
		assertTrue(containsSquare(candidates, 'd', 3));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC4: Rook From Center Excludes Diagonal Squares
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rookFromCenter_excludesDiagonalSquares() {
		Rook rook = new Rook(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(rook);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = rook.getLegalMoveDestinationSquares(from);

		assertFalse(containsSquare(candidates, 'e', 5));
		assertFalse(containsSquare(candidates, 'e', 3));
		assertFalse(containsSquare(candidates, 'c', 5));
		assertFalse(containsSquare(candidates, 'c', 3));

		verify(from);
	}

	private boolean containsSquare(List<Square> squares, char file, int rank) {
		return squares.stream().anyMatch(s -> s.getFile() == file && s.getRank() == rank);
	}
}