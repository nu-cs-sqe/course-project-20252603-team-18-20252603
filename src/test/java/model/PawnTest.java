package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class PawnTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nullFrom_throwsException() {
		Pawn pawn = new Pawn(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			pawn.getLegalMoveDestinationSquares(null);
		});
	}

	// -------------------------------------------------------------------------
	// TC2: From Square Not Occupied By This Pawn
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_fromNotOccupiedByThisPawn_throwsException() {
		Pawn pawn = new Pawn(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(null);
		replay(from);

		assertThrows(IllegalArgumentException.class, () -> {
			pawn.getLegalMoveDestinationSquares(from);
		});

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC3: White Pawn — Single-Square Forward Candidate
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawnHasMoved_containsOneSquareForward() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 3));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC4: Black Pawn — Single-Square Forward Candidate
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_blackPawnHasMoved_containsOneSquareForward() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 6));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC5: White Pawn — Two-Square Advance Candidate From Starting Rank
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawnHasNotMoved_containsTwoSquareForward() {
		Pawn pawn = new Pawn(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC6: Black Pawn — Two-Square Advance Candidate From Starting Rank
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_blackPawnHasNotMoved_containsTwoSquareForward() {
		Pawn pawn = new Pawn(Color.BLACK);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC7: White Pawn — Two-Square Advance Candidate Absent After Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawnHasMoved_excludesTwoSquareForward() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(3).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC8: Black Pawn — Two-Square Advance Candidate Absent After Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_blackPawnHasMoved_excludesTwoSquareForward() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(6).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC9: White Pawn — Both Diagonal Candidates Present (Interior File)
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawnInteriorFile_containsBothDiagonals() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC10: Black Pawn — Both Diagonal Candidates Present (Interior File)
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_blackPawnInteriorFile_containsBothDiagonals() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 4));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC11: White Pawn On Minimum File ('a') — Only Right Diagonal Candidate Exists
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawnOnFileA_onlyRightDiagonalCandidate() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 5));
		for (Square candidate : candidates) {
			assertTrue(candidate.getFile() >= 'a' && candidate.getFile() <= 'h');
			assertTrue(candidate.getRank() >= 1 && candidate.getRank() <= 8);
		}

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC12: White Pawn On Maximum File ('h') — Only Left Diagonal Candidate Exists
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawnOnFileH_onlyLeftDiagonalCandidate() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'h' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 5));
		for (Square candidate : candidates) {
			assertTrue(candidate.getFile() >= 'a' && candidate.getFile() <= 'h');
			assertTrue(candidate.getRank() >= 1 && candidate.getRank() <= 8);
		}

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC13: Black Pawn On Minimum File ('a') — Only Right Diagonal Candidate Exists
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_blackPawnOnFileA_onlyRightDiagonalCandidate() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'a' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'b' && s.getRank() == 4));
		for (Square candidate : candidates) {
			assertTrue(candidate.getFile() >= 'a' && candidate.getFile() <= 'h');
			assertTrue(candidate.getRank() >= 1 && candidate.getRank() <= 8);
		}

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC14: Black Pawn On Maximum File ('h') — Only Left Diagonal Candidate Exists
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_blackPawnOnFileH_onlyLeftDiagonalCandidate() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'h' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 4));
		for (Square candidate : candidates) {
			assertTrue(candidate.getFile() >= 'a' && candidate.getFile() <= 'h');
			assertTrue(candidate.getRank() >= 1 && candidate.getRank() <= 8);
		}

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC15: White Pawn — Forward Candidate Reaches Promotion Rank
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawnOnRank7_containsPromotionRankCandidates() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 8));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 8));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 8));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC16: Black Pawn — Forward Candidate Reaches Promotion Rank
	// -------------------------------------------------------------------------

	@Test
	void getLegalMoveDestinationSquares_blackPawnOnRank2_containsPromotionRankCandidates() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 1));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 1));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC17: White Pawn — Backward Square Not A Candidate
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawn_excludesBackwardSquare() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 3));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC18: Black Pawn — Backward Square Not A Candidate
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_blackPawn_excludesBackwardSquare() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 6));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC19: White Pawn — Correct Total Candidate Count From Interior File After Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawnInteriorFileHasMoved_returnsThreeCandidates() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertEquals(3, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC20: White Pawn — Correct Total Candidate Count From Interior File Without Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_whitePawnInteriorFileHasNotMoved_returnsFourCandidates() {
		Pawn pawn = new Pawn(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoveDestinationSquares(from);

		assertEquals(4, candidates.size());
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 3));

		verify(from);
	}
}
