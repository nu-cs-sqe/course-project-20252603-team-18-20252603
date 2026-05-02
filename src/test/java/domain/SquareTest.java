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

    @Test
    void constructor_validMaximumBoundaries_createsSquare() {
        Square square = new Square('h', 8);

        assertEquals('h', square.getFile());
        assertEquals(8, square.getRank());
        assertTrue(square.isEmpty());
    }

    @Test
    void constructor_invalidFile_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Square('i', 1);
        }, "Should throw exception for file 'i'");
    }

    @Test
    void constructor_invalidRank_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Square('a', 9);
        }, "Should throw exception for rank 9");
    }
}