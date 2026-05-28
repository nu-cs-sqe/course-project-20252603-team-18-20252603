package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

	// -------------------------------------------------------------------------
	// TC5: Rook From Center Returns Correct Candidate Count
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rookFromCenter_returnsCorrectCandidateCount() {
		Rook rook = new Rook(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(rook);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = rook.getLegalMoveDestinationSquares(from);

		assertEquals(14, candidates.size());

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC6: Rook From Minimum Corner a1 Contains Only Up And Right Lines
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rookFromA1_containsOnlyUpAndRightLines() {
		Rook rook = new Rook(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(rook);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = rook.getLegalMoveDestinationSquares(from);

		assertEquals(14, candidates.size());
		assertTrue(containsSquare(candidates, 'b', 1));
		assertTrue(containsSquare(candidates, 'c', 1));
		assertTrue(containsSquare(candidates, 'd', 1));
		assertTrue(containsSquare(candidates, 'e', 1));
		assertTrue(containsSquare(candidates, 'f', 1));
		assertTrue(containsSquare(candidates, 'g', 1));
		assertTrue(containsSquare(candidates, 'h', 1));
		assertTrue(containsSquare(candidates, 'a', 2));
		assertTrue(containsSquare(candidates, 'a', 3));
		assertTrue(containsSquare(candidates, 'a', 4));
		assertTrue(containsSquare(candidates, 'a', 5));
		assertTrue(containsSquare(candidates, 'a', 6));
		assertTrue(containsSquare(candidates, 'a', 7));
		assertTrue(containsSquare(candidates, 'a', 8));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC7: Rook From Maximum Corner h8 Contains Only Down And Left Lines
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rookFromH8_containsOnlyDownAndLeftLines() {
		Rook rook = new Rook(Color.BLACK);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(rook);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = rook.getLegalMoveDestinationSquares(from);

		assertEquals(14, candidates.size());
		assertTrue(containsSquare(candidates, 'g', 8));
		assertTrue(containsSquare(candidates, 'f', 8));
		assertTrue(containsSquare(candidates, 'e', 8));
		assertTrue(containsSquare(candidates, 'd', 8));
		assertTrue(containsSquare(candidates, 'c', 8));
		assertTrue(containsSquare(candidates, 'b', 8));
		assertTrue(containsSquare(candidates, 'a', 8));
		assertTrue(containsSquare(candidates, 'h', 7));
		assertTrue(containsSquare(candidates, 'h', 6));
		assertTrue(containsSquare(candidates, 'h', 5));
		assertTrue(containsSquare(candidates, 'h', 4));
		assertTrue(containsSquare(candidates, 'h', 3));
		assertTrue(containsSquare(candidates, 'h', 2));
		assertTrue(containsSquare(candidates, 'h', 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC8: Rook From Minimum File Interior Square Has No Left Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rookFromFileA_hasNoLeftCandidates() {
		Rook rook = new Rook(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(rook);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = rook.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'b', 4));
		assertTrue(containsSquare(candidates, 'a', 5));
		assertTrue(containsSquare(candidates, 'a', 3));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() < 'a'));
		assertEquals(14, candidates.size());

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC9: Rook From Maximum File Interior Square Has No Right Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rookFromFileH_hasNoRightCandidates() {
		Rook rook = new Rook(Color.BLACK);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(rook);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = rook.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'g', 4));
		assertTrue(containsSquare(candidates, 'h', 5));
		assertTrue(containsSquare(candidates, 'h', 3));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() > 'h'));
		assertEquals(14, candidates.size());

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC10: Rook From Minimum Rank Interior Square Has No Downward Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rookFromRank1_hasNoDownwardCandidates() {
		Rook rook = new Rook(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(rook);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = rook.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'e', 1));
		assertTrue(containsSquare(candidates, 'c', 1));
		assertTrue(containsSquare(candidates, 'd', 2));
		assertFalse(candidates.stream().anyMatch(s -> s.getRank() < 1));
		assertEquals(14, candidates.size());

		verify(from);
	}

	private boolean containsSquare(List<Square> squares, char file, int rank) {
		return squares.stream().anyMatch(s -> s.getFile() == file && s.getRank() == rank);
	}
}