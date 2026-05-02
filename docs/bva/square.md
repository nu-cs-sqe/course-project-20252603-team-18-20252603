# Square.md

## Intermediate Analysis (Steps 1-3)

**Input Domains & Variables:**
* `file` (char): Represents the column on the chessboard.
    * Valid range: `['a'..'h']`
* `rank` (int): Represents the row on the chessboard.
    * Valid range: `[1..8]`
* `occupant` (Piece): Represents the chess piece currently on the square.
    * Valid values: A valid `Piece` object or `null` (empty).

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

---

## Step 4: Test Cases

### Method under test: `Square(char file, int rank)`
- **TC1: Valid Minimum Boundaries** (x)
    - **State of the system**: System instantiates `Square` with `file = 'a'`, `rank = 1`.
    - **Expected output**: `Square` created successfully. `occupant` defaults to `null`.

- **TC2: Valid Maximum Boundaries** (x)
    - **State of the system**: System instantiates `Square` with `file = 'h'`, `rank = 8`.
    - **Expected output**: `Square` created successfully. `occupant` defaults to `null`.

- **TC3: Invalid File (Out of bounds)** (x)
    - **State of the system**: System instantiates `Square` with `file = 'i'`, `rank = 1`.
    - **Expected output**: Throws `IllegalArgumentException`.

- **TC4: Invalid Rank (Out of bounds)** (x)
    - **State of the system**: System instantiates `Square` with `file = 'a'`, `rank = 9`.
    - **Expected output**: Throws `IllegalArgumentException`.

### Method under test: `setOccupant(Piece piece)`
- **TC5: Valid Occupant (Add Piece)** (x)
    - **State of the system**: Empty `Square` ('a', 1) exists. System calls `setOccupant` with a valid mock or skeleton `Piece`.
    - **Expected output**: `Square.occupant` is updated to the provided `Piece`.

- **TC6: Empty Occupant (Clear Square)** (x)
    - **State of the system**: Occupied `Square` ('a', 1) exists. System calls `setOccupant` with `null`.
    - **Expected output**: `Square.occupant` is updated to `null`.