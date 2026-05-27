package domain;

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
}
