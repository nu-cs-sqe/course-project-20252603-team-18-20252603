package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
