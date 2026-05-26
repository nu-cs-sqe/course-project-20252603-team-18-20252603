package domain;

import org.junit.jupiter.api.Test;

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
}
