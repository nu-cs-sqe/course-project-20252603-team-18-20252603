package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
