package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    @Test
    void constructor_validMinimumBoundaries_createsSquare() {
        // Act
        Square square = new Square('a', 1);

        // Assert
        assertEquals('a', square.getFile());
        assertEquals(1, square.getRank());
        assertTrue(square.isEmpty(), "A newly created square should be empty");
    }
}