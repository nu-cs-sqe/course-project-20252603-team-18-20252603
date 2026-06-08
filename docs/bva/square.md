# Square.md

## Intermediate Analysis (Steps 1-3)

**Input Domains & Variables:**
* `file` (char): Represents the column on the chessboard.
    * Valid range: `['a'..'h']`
* `rank` (int): Represents the row on the chessboard.
    * Valid range: `[1..8]`
* `occupant` (Piece): Represents the chess piece currently on the square.
    * Valid values: A valid `Piece` object or `null` (empty).
* `other` (Object): Represents the object compared with a `Square` by `equals`.
    * Equivalent value: A different `Square` with the same `file` and `rank`.
    * Non-equivalent values: A `Square` with a different `file` or `rank`, `null`, or a non-`Square` object.

**Boundary Values Identified:**
* `file` Boundaries:
    * Min: `'a'`
    * Max: `'h'`
    * Invalid (Just above/below): `'i'`, `` '`' `` (ASCII 96)
* `rank` Boundaries:
    * Min: `1`
    * Max: `8`
    * Invalid (Just above/below): `0`, `9`
* `occupant` Boundaries:
    * Valid state: Instantiated `Piece` object
    * Empty state: `null`
* Equality Boundaries:
    * Equivalent coordinates: Same `file` and same `rank`.
    * Adjacent rank: Same `file`, but rank differs by `1`.
    * Adjacent file: Same `rank`, but file differs by `1`.

---

## Step 4: Test Cases

### Method under test: `Square(char file, int rank)`
- **TC1: Valid Minimum Boundaries** (V)
    - **State of the system**: System instantiates `Square` with `file = 'a'`, `rank = 1`.
    - **Expected output**: `Square` created successfully. `occupant` defaults to `null`.
    - **Implemented at** constructor_validMinimumBoundaries_createsSquare

- **TC2: Valid Maximum Boundaries** (V)
    - **State of the system**: System instantiates `Square` with `file = 'h'`, `rank = 8`.
    - **Expected output**: `Square` created successfully. `occupant` defaults to `null`.
    - **Implemented at** constructor_validMaximumBoundaries_createsSquare

- **TC3: Invalid File (Out of bounds)** (V)
    - **State of the system**: System instantiates `Square` with `file = 'i'`, `rank = 1`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at** constructor_invalidFileAbove_throwsException

- **TC3.5: Invalid File (Out of bounds)** (V)
  - **State of the system**: System instantiates `Square` with file = `` '`' `` (backtick), `rank = 1`.
  - **Expected output**: Throws `IllegalArgumentException`.
  - **Implemented at** constructor_invalidFileBelow_throwsException

- **TC4: Invalid Rank (Out of bounds)** (x)
    - **State of the system**: System instantiates `Square` with `file = 'a'`, `rank = 9`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at** constructor_invalidRankAbove_throwsException

- **TC4.5: Invalid Rank (Out of bounds)** (V)
  - **State of the system**: System instantiates `Square` with `file = 'i'`, `rank = 0`.
  - **Expected output**: Throws `IllegalArgumentException`.
  - **Implemented at** constructor_invalidRankBelow_throwsException



### Method under test: `setOccupant(Piece piece)`
- **TC5: Valid Occupant (Add Piece)** (x)
    - **State of the system**: Empty `Square` ('a', 1) exists. System calls `setOccupant` with a valid mock or skeleton `Piece`.
    - **Expected output**: `Square.occupant` is updated to the provided `Piece`.
    - **Implemented at** setOccupant_validPiece_updatesSquareToNotEmpty

- **TC6: Empty Occupant (Clear Square)** (x)
    - **State of the system**: Occupied `Square` ('a', 1) exists. System calls `setOccupant` with `null`.
    - **Expected output**: `Square.occupant` is updated to `null`.
    - **Implemented at** setOccupant_null_updatesSquareToEmpty



### Method under test: `getOccupant()`
- **TC7: Get Occupant on Empty Square** (V)
  - **State of the system**: A `Square` ('e', 4) exists. `setOccupant` has not been called.
  - **Expected output**: Returns `null`.
  - **Implemented at** getOccupant_emptySquare_returnsNull
- **TC8: Get Occupant on Occupied Square** (V)
  - **State of the system**: A `Square` ('e', 4) exists. `setOccupant` has been called with a valid mock `Piece`.
  - **Expected output**: Returns the exact same `Piece` instance that was set (referential equality).
  - **Implemented at** getOccupant_occupiedSquare_returnsPiece



### Methods under test: `equals(Object other)` and `hashCode()`
- **TC9: Different Square Instances With Equal Coordinates** (V)
  - **State of the system**: Two separate `Square` instances are created with `file = 'e'` and `rank = 4`.
  - **Expected output**: `equals` returns `true`, and both squares return the same hash code.
  - **Implemented at** equals_sameCoordinates_returnsTrue

- **TC10: Same File With Adjacent Rank** (V)
  - **State of the system**: One `Square` is ('e', 4), and the other is ('e', 5).
  - **Expected output**: `equals` returns `false`.
  - **Implemented at** equals_differentCoordinates_returnsFalse
