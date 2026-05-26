package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
