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

public class QueenTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nullFrom_throwsException() {
		Queen queen = new Queen(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			queen.getLegalMoveDestinationSquares(null);
		});
	}

	// -------------------------------------------------------------------------
	// TC2: From Square Not Occupied By This Queen
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fromNotOccupiedByThisQueen_throwsException() {
		Queen queen = new Queen(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(null);
		replay(from);

		assertThrows(IllegalArgumentException.class, () -> {
			queen.getLegalMoveDestinationSquares(from);
		});

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC3: Queen at Interior Square — All Eight Directions Generated
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorSquare_containsAllEightDirections() {
		Queen queen = new Queen(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(queen);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = queen.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'e', 4));
		assertTrue(containsSquare(candidates, 'c', 4));
		assertTrue(containsSquare(candidates, 'd', 5));
		assertTrue(containsSquare(candidates, 'd', 3));
		assertTrue(containsSquare(candidates, 'e', 5));
		assertTrue(containsSquare(candidates, 'e', 3));
		assertTrue(containsSquare(candidates, 'c', 5));
		assertTrue(containsSquare(candidates, 'c', 3));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC4: Queen at Interior Square — Straight Candidates Present
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorSquare_containsStraightCandidates() {
		Queen queen = new Queen(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(queen);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = queen.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'e', 4));
		assertTrue(containsSquare(candidates, 'c', 4));
		assertTrue(containsSquare(candidates, 'd', 5));
		assertTrue(containsSquare(candidates, 'd', 3));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC5: Queen at Interior Square — Diagonal Candidates Present
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorSquare_containsDiagonalCandidates() {
		Queen queen = new Queen(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(queen);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = queen.getLegalMoveDestinationSquares(from);

		assertTrue(containsSquare(candidates, 'e', 5));
		assertTrue(containsSquare(candidates, 'e', 3));
		assertTrue(containsSquare(candidates, 'c', 5));
		assertTrue(containsSquare(candidates, 'c', 3));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC6: Queen at Interior Square — Knight-Like Squares Absent
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorSquare_excludesKnightLikeSquares() {
		Queen queen = new Queen(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(queen);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = queen.getLegalMoveDestinationSquares(from);

		assertFalse(containsSquare(candidates, 'f', 5));
		assertFalse(containsSquare(candidates, 'f', 3));
		assertFalse(containsSquare(candidates, 'b', 5));
		assertFalse(containsSquare(candidates, 'b', 3));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC7: Queen at Interior Square — Correct Total Candidate Count
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorSquare_returnsTwentySevenCandidates() {
		Queen queen = new Queen(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(queen);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = queen.getLegalMoveDestinationSquares(from);

		assertEquals(27, candidates.size());

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC8: Queen at Minimum-File Edge — Left Candidates Absent
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fileA_interiorRank_returnsTwentyOneCandidates() {
		Queen queen = new Queen(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(queen);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = queen.getLegalMoveDestinationSquares(from);

		assertEquals(21, candidates.size());
		assertTrue(containsSquare(candidates, 'b', 4));
		assertTrue(containsSquare(candidates, 'a', 5));
		assertTrue(containsSquare(candidates, 'a', 3));
		assertTrue(containsSquare(candidates, 'b', 5));
		assertTrue(containsSquare(candidates, 'b', 3));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() < 'a'));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC9: Queen at Maximum-File Edge — Right Candidates Absent
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fileH_interiorRank_returnsTwentyOneCandidates() {
		Queen queen = new Queen(Color.BLACK);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(queen);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = queen.getLegalMoveDestinationSquares(from);

		assertEquals(21, candidates.size());
		assertTrue(containsSquare(candidates, 'g', 4));
		assertTrue(containsSquare(candidates, 'h', 5));
		assertTrue(containsSquare(candidates, 'h', 3));
		assertTrue(containsSquare(candidates, 'g', 5));
		assertTrue(containsSquare(candidates, 'g', 3));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() > 'h'));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC10: Queen at Minimum-Rank Edge — Downward Candidates Absent
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rank1_interiorFile_returnsTwentyOneCandidates() {
		Queen queen = new Queen(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(queen);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = queen.getLegalMoveDestinationSquares(from);

		assertEquals(21, candidates.size());
		assertTrue(containsSquare(candidates, 'e', 1));
		assertTrue(containsSquare(candidates, 'c', 1));
		assertTrue(containsSquare(candidates, 'd', 2));
		assertTrue(containsSquare(candidates, 'e', 2));
		assertTrue(containsSquare(candidates, 'c', 2));
		assertFalse(candidates.stream().anyMatch(s -> s.getRank() < 1));

		verify(from);
	}

	private boolean containsSquare(List<Square> squares, char file, int rank) {
		return squares.stream().anyMatch(s -> s.getFile() == file && s.getRank() == rank);
	}
}