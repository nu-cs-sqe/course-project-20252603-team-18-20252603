package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BishopTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nullFrom_throwsException() {
		Bishop bishop = new Bishop(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			bishop.getLegalMoveDestinationSquares(null);
		});
	}

	// -------------------------------------------------------------------------
	// TC2: From Square Not Occupied By This Bishop
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fromNotOccupiedByThisBishop_throwsException() {
		Bishop bishop = new Bishop(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(null);
		replay(from);

		assertThrows(IllegalArgumentException.class, () -> {
			bishop.getLegalMoveDestinationSquares(from);
		});

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC3: Bishop From Center Contains All Four Diagonal Directions
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_bishopFromCenter_containsAllFourDiagonalDirections() {
		Bishop bishop = new Bishop(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(bishop);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = bishop.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'e', 5));
		assertTrue(containsSquare(candidates, 'e', 3));
		assertTrue(containsSquare(candidates, 'c', 5));
		assertTrue(containsSquare(candidates, 'c', 3));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC4: Bishop From Center Excludes Orthogonal Squares
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_bishopFromCenter_excludesOrthogonalSquares() {
		Bishop bishop = new Bishop(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(bishop);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = bishop.getLegalMoveDestinationSquares(from);

		assertFalse(containsSquare(candidates, 'd', 5));
		assertFalse(containsSquare(candidates, 'd', 3));
		assertFalse(containsSquare(candidates, 'e', 4));
		assertFalse(containsSquare(candidates, 'c', 4));

		verify(from);
	}

	private boolean containsSquare(List<Square> squares, char file, int rank) {
		return squares.stream().anyMatch(s -> s.getFile() == file && s.getRank() == rank);
	}
}