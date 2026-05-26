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
}
