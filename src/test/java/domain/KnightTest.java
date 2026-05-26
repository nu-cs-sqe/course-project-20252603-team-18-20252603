package domain;

import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
