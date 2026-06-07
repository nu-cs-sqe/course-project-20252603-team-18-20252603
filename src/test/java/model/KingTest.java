package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class KingTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nullFrom_throwsException() {
		King king = new King(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			king.getLegalMoveDestinationSquares(null);
		});
	}

	// -------------------------------------------------------------------------
	// TC2: From Square Not Occupied By This King
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fromNotOccupiedByThisKing_throwsException() {
		King king = new King(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(null);
		replay(from);

		assertThrows(IllegalArgumentException.class, () -> {
			king.getLegalMoveDestinationSquares(from);
		});

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC3: King at Interior Square — All Eight One-Step Candidates Generated
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorSquare_returnsEightCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(8, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC4: King at Minimum-File Edge — Left Candidates Absent
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fileA_interiorRank_returnsFiveCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(5, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 5));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() < 'a'));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC5: King at Maximum-File Edge — Right Candidates Absent
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fileH_interiorRank_returnsFiveCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(5, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'h' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'h' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 5));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() > 'h'));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC6: King at Minimum-Rank Edge — Below Candidates Absent
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rank1_interiorFile_returnsFiveCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(5, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 2));
		assertFalse(candidates.stream().anyMatch(s -> s.getRank() < 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC7: King at Maximum-Rank Edge — Above Candidates Absent
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_rank8_interiorFile_returnsFiveCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(5, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 8));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 8));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 7));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 7));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 7));
		assertFalse(candidates.stream().anyMatch(s -> s.getRank() > 8));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC8: King at Corner a1 — Minimum Candidate Count
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_cornerA1_returnsThreeCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(3, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 2));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC9: King at Corner a8 — Minimum Candidate Count
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_cornerA8_returnsThreeCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(3, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 8));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 7));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 7));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC10: King at Corner h1 — Minimum Candidate Count
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_cornerH1_returnsThreeCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(3, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'h' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 2));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC11: King at Corner h8 — Minimum Candidate Count
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_cornerH8_returnsThreeCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(3, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 8));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'h' && s.getRank() == 7));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 7));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC12: from Square Is Not A Candidate
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_interiorSquare_excludesFromSquare() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC13: All Returned Squares Are Within Board Bounds
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_cornerA1_allCandidatesInBounds() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		for (Square candidate : candidates) {
			assertTrue(candidate.getFile() >= 'a' && candidate.getFile() <= 'h');
			assertTrue(candidate.getRank() >= 1 && candidate.getRank() <= 8);
		}

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC14: White King — Castling Destination Candidates Present When Not Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whiteKingHasNotMoved_containsCastlingDestinations() {
		King king = new King(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC15: Black King — Castling Destination Candidates Present When Not Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_blackKingHasNotMoved_containsCastlingDestinations() {
		King king = new King(Color.BLACK);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 8));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 8));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC16: White King — Castling Destination Candidates Absent After Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whiteKingHasMoved_excludesCastlingDestinations() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 1));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC17: Black King — Castling Destination Candidates Absent After Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_blackKingHasMoved_excludesCastlingDestinations() {
		King king = new King(Color.BLACK);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(8).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 8));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 8));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC18: White King — Correct Total Candidate Count From Starting Square Without Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whiteKingHasNotMoved_returnsTenCandidates() {
		King king = new King(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(7, candidates.size());
		// One-step candidates
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 2));
		// Castling destinations
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC19: White King — Correct Total Candidate Count From Starting Square After Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whiteKingHasMoved_returnsFiveCandidates() {
		King king = new King(Color.WHITE);
		king.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(king);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(1).anyTimes();
		replay(from);

		List<Square> candidates = king.getLegalMoveDestinationSquares(from);

		assertEquals(5, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 2));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 2));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'c' && s.getRank() == 1));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 1));

		verify(from);
	}
}
