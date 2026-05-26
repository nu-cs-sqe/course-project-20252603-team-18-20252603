package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BishopTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nullFrom_throwsException() {
		Bishop bishop = new Bishop(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			bishop.getLegalMoveDestinationSquares(null);
		});
	}
}