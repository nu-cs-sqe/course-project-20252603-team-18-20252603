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
}
