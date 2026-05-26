package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class KnightTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nullFrom_throwsException() {
		Knight knight = new Knight(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			knight.getLegalMoveDestinationSquares(null);
		});
	}

	// -------------------------------------------------------------------------
	// TC2: From Square Not Occupied By This Knight
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fromNotOccupiedByThisKnight_throwsException() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(null);
		replay(from);

		assertThrows(IllegalArgumentException.class, () -> {
			knight.getLegalMoveDestinationSquares(from);
		});

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC3: Fully Interior Square — All Eight Candidates Generated
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorSquare_returnsAllEightCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(8, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC4: Corner Square — Minimum File, Minimum Rank (a1) — Two Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_cornerA1_returnsTwoCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(2, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 2));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC5: Corner Square — Minimum File, Maximum Rank (a8) — Two Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_cornerA8_returnsTwoCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(2, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 7));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC6: Corner Square — Maximum File, Minimum Rank (h1) — Two Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_cornerH1_returnsTwoCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(2, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 2));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC7: Corner Square — Maximum File, Maximum Rank (h8) — Two Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_cornerH8_returnsTwoCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(2, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 7));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC8: Near-Corner — Minimum File, One Inside Minimum Rank (a2) — Three Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nearCornerA2_returnsThreeCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(3, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC9: Near-Corner — One Inside Minimum File, Minimum Rank (b1) — Three Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nearCornerB1_returnsThreeCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('b').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(3, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 2));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC10: One Inside Each Boundary — Minimum-Side (b2) — Four Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_oneInsideEachMinBoundaryB2_returnsFourCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('b').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(4, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC11: One Inside Each Boundary — Maximum-Side (g7) — Four Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_oneInsideEachMaxBoundaryG7_returnsFourCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('g').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(4, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'h' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 8));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC12: Minimum File, Interior Rank (a4) — Four Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_minFileInteriorRankA4_returnsFourCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(4, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 5));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() < 'a'));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC13: Maximum File, Interior Rank (h5) — Four Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_maxFileInteriorRankH5_returnsFourCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(4, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 7));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 6));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() > 'h'));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC14: Interior File, Minimum Rank (d1) — Four Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorFileMinRankD1_returnsFourCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(4, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 2));
		assertFalse(candidates.stream().anyMatch(s -> s.getRank() < 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC15: Interior File, Maximum Rank (e8) — Four Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorFileMaxRankE8_returnsFourCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(4, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 7));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 7));
		assertFalse(candidates.stream().anyMatch(s -> s.getRank() > 8));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC16: Second-Ring Square — One Inside Minimum File, Interior Rank (b4) — Six Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_secondRingB4_returnsSixCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('b').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(6, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC17: Second-Ring Square — Interior File, One Inside Minimum Rank (d2) — Six Candidates
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_secondRingD2_returnsSixCandidates() {
		Knight knight = new Knight(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(knight);
		expect(from.getFile()).andReturn('d').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();
		replay(from);

		List<Square> candidates = knight.getLegalMoveDestinationSquares(from);

		assertEquals(6, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 3));

		verify(from);
	}
}
