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
import static org.junit.jupiter.api.Assertions.assertEquals;

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

	// -------------------------------------------------------------------------
	// TC5: Bishop From Center Returns Correct Candidate Count
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_bishopFromCenter_returnsCorrectCandidateCount() {
		Bishop bishop = new Bishop(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(bishop);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = bishop.getLegalMoveDestinationSquares(from);

		assertEquals(13, candidates.size());

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC6: Bishop From Minimum Corner a1 Contains Only Up-Right Diagonal
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_bishopFromA1_containsOnlyUpRightDiagonal() {
		Bishop bishop = new Bishop(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(bishop);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = bishop.getLegalMoveDestinationSquares(from);

		assertEquals(7, candidates.size());
		assertTrue(containsSquare(candidates, 'b', 2));
		assertTrue(containsSquare(candidates, 'c', 3));
		assertTrue(containsSquare(candidates, 'd', 4));
		assertTrue(containsSquare(candidates, 'e', 5));
		assertTrue(containsSquare(candidates, 'f', 6));
		assertTrue(containsSquare(candidates, 'g', 7));
		assertTrue(containsSquare(candidates, 'h', 8));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC7: Bishop From Maximum Corner h8 Contains Only Down-Left Diagonal
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_bishopFromH8_containsOnlyDownLeftDiagonal() {
		Bishop bishop = new Bishop(Color.BLACK);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(bishop);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = bishop.getLegalMoveDestinationSquares(from);

		assertEquals(7, candidates.size());
		assertTrue(containsSquare(candidates, 'g', 7));
		assertTrue(containsSquare(candidates, 'f', 6));
		assertTrue(containsSquare(candidates, 'e', 5));
		assertTrue(containsSquare(candidates, 'd', 4));
		assertTrue(containsSquare(candidates, 'c', 3));
		assertTrue(containsSquare(candidates, 'b', 2));
		assertTrue(containsSquare(candidates, 'a', 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC8: Bishop From Minimum File Interior Square Has No Left Diagonals
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_bishopFromFileA_hasNoLeftDiagonals() {
		Bishop bishop = new Bishop(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(bishop);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = bishop.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'b', 5));
		assertTrue(containsSquare(candidates, 'b', 3));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() < 'a'));
		assertEquals(7, candidates.size());

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC9: Bishop From Maximum File Interior Square Has No Right Diagonals
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_bishopFromFileH_hasNoRightDiagonals() {
		Bishop bishop = new Bishop(Color.BLACK);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(bishop);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = bishop.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'g', 5));
		assertTrue(containsSquare(candidates, 'g', 3));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() > 'h'));
		assertEquals(7, candidates.size());

		verify(from);
	}

	private boolean containsSquare(List<Square> squares, char file, int rank) {
		return squares.stream().anyMatch(s -> s.getFile() == file && s.getRank() == rank);
	}
}