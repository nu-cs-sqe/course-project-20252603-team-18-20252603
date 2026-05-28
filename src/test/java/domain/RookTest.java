package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RookTest {

	// -------------------------------------------------------------------------
	// TC1: Null From Square
	// -------------------------------------------------------------------------
	@Test
	void getLegalMoveDestinationSquares_nullFrom_throwsException() {
		Rook rook = new Rook(Color.WHITE);

		assertThrows(IllegalArgumentException.class, () -> {
			rook.getLegalMoveDestinationSquares(null);
		});
	}
}