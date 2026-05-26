package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class PawnTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_nullFrom_throwsException() {
		Pawn pawn = new Pawn(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			pawn.getLegalMoves(null);
		});
	}

	// -------------------------------------------------------------------------
	// TC2: From Square Not Occupied By This Pawn
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_fromNotOccupiedByThisPawn_throwsException() {
		Pawn pawn = new Pawn(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(null);
		replay(from);

		assertThrows(IllegalArgumentException.class, () -> {
			pawn.getLegalMoves(from);
		});

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC3: White Pawn — Single-Square Forward Candidate
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_whitePawnHasMoved_containsOneSquareForward() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 3));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC4: Black Pawn — Single-Square Forward Candidate
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_blackPawnHasMoved_containsOneSquareForward() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 6));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC5: White Pawn — Two-Square Advance Candidate From Starting Rank
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_whitePawnHasNotMoved_containsTwoSquareForward() {
		Pawn pawn = new Pawn(Color.WHITE);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(2).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 3));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC6: Black Pawn — Two-Square Advance Candidate From Starting Rank
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_blackPawnHasNotMoved_containsTwoSquareForward() {
		Pawn pawn = new Pawn(Color.BLACK);

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(7).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 6));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC7: White Pawn — Two-Square Advance Candidate Absent After Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_whitePawnHasMoved_excludesTwoSquareForward() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(3).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC8: Black Pawn — Two-Square Advance Candidate Absent After Having Moved
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_blackPawnHasMoved_excludesTwoSquareForward() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(6).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 5));
		assertFalse(candidates.stream().anyMatch(s -> s.getFile() == 'e' && s.getRank() == 4));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC9: White Pawn — Both Diagonal Candidates Present (Interior File)
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_whitePawnInteriorFile_containsBothDiagonals() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 5));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 5));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC10: Black Pawn — Both Diagonal Candidates Present (Interior File)
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_blackPawnInteriorFile_containsBothDiagonals() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('e').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'd' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'f' && s.getRank() == 4));

		verify(from);
	}

	// -------------------------------------------------------------------------
	// TC11: White Pawn On Minimum File ('a') — Only Right Diagonal Candidate Exists
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoves_whitePawnOnFileA_onlyRightDiagonalCandidate() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

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
	void getLegalMoves_whitePawnOnFileH_onlyLeftDiagonalCandidate() {
		Pawn pawn = new Pawn(Color.WHITE);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(4).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

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
	void getLegalMoves_blackPawnOnFileA_onlyRightDiagonalCandidate() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('a').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

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
	void getLegalMoves_blackPawnOnFileH_onlyLeftDiagonalCandidate() {
		Pawn pawn = new Pawn(Color.BLACK);
		pawn.markMoved();

		Square from = createMock(Square.class);
		expect(from.getOccupant()).andReturn(pawn);
		expect(from.getFile()).andReturn('h').anyTimes();
		expect(from.getRank()).andReturn(5).anyTimes();
		replay(from);

		List<Square> candidates = pawn.getLegalMoves(from);

		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'h' && s.getRank() == 4));
		assertTrue(candidates.stream().anyMatch(s -> s.getFile() == 'g' && s.getRank() == 4));
		for (Square candidate : candidates) {
			assertTrue(candidate.getFile() >= 'a' && candidate.getFile() <= 'h');
			assertTrue(candidate.getRank() >= 1 && candidate.getRank() <= 8);
		}

		verify(from);
	}
}
