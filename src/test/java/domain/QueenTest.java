package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueenTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nullFrom_throwsException() {
		Queen queen = new Queen(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			queen.getLegalMoveDestinationSquares(null);
		});
	}
}