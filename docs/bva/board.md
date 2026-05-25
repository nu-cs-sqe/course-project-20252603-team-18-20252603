# Board.md

## Intermediate Analysis (Steps 1–3)

**Input Domains & Variables:**

### `getSquare(file: char, rank: int)`

* `file` (char): The column identifier of the target square.
    * Valid values: `'a'` through `'h'` (inclusive).
    * Invalid values: Any character outside this range (e.g., `` '`' `` (ASCII 96), `'i'`); non-alphabetic characters; uppercase letters (`'A'`–`'H'`).

* `rank` (int): The row identifier of the target square.
    * Valid values: `1` through `8` (inclusive).
    * Invalid values: `0`, any negative integer, any integer greater than `8`.

---

### `placePiece(piece: Piece, square: Square)`

* `piece` (Piece): The piece to be placed on the board.
    * Valid values: Any non-null instantiated `Piece` subclass (King, Queen, Rook, Bishop, Knight, or Pawn).
    * Invalid values: `null`.

* `square` (Square): The target square where the piece will be placed.
    * Valid values: Any non-null `Square` within the grid (file `'a'`–`'h'`, rank `1`–`8`) that is currently empty (`isEmpty() == true`).
    * Invalid values: `null`; a `Square` already occupied by another piece (`isEmpty() == false`).

---

### `removePiece(square: Square)`

* `square` (Square): The square from which the piece will be removed.
    * Valid values: Any non-null `Square` within the grid (file `'a'`–`'h'`, rank `1`–`8`) that is currently occupied (`isEmpty() == false`).
    * Invalid values: `null`; a `Square` that is already empty (`isEmpty() == true`).

---

### `movePiece(from: Square, to: Square)`

* `from` (Square): The square the piece is moving from.
    * Valid values: Any non-null `Square` within the grid (file `'a'`–`'h'`, rank `1`–`8`) that is currently occupied (`isEmpty() == false`).
    * Invalid values: `null`; a `Square` that is empty (`isEmpty() == true`).

* `to` (Square): The square the piece is moving to.
    * Valid values: Any non-null `Square` within the grid (file `'a'`–`'h'`, rank `1`–`8`) that is not the same square as `from`.
    * Invalid values: `null`; the same square as `from` (identical file and rank); a `Square` occupied by a friendly piece (same color as the piece on `from`).
    * Note: A `Square` occupied by an opponent piece is a valid destination (capture); the existing occupant is displaced.

---

**Boundary Values Identified:**

* `file` (char) Boundaries:
    * Min valid: `'a'` (ASCII 97)
    * Max valid: `'h'` (ASCII 104)
    * Just below min: `` '`' `` (ASCII 96)
    * Just above max: `'i'` (ASCII 105)

* `rank` (int) Boundaries:
    * Min valid: `1`
    * Max valid: `8`
    * Just below min: `0`
    * Just above max: `9`

* `piece` (Piece) Boundaries:
    * Valid state: Any non-null instantiated `Piece` subclass.
    * Invalid state: `null`.

* `square` / `from` / `to` (Square) Boundaries:
    * Valid occupied state: `isEmpty() == false`.
    * Valid empty state: `isEmpty() == true`.
    * Invalid structural: `null`; `from` == `to` (same file and rank).
    * Invalid occupant: `to` occupied by a friendly piece (same color).

---

## Step 4: Test Cases

### Method under test: `Board.getSquare(char file, int rank)`

- **TC1: Valid Square — Interior** (x)
    - **State of the system**: Board is initialized. `file` = `'d'`, `rank` = `4` (a central square).
    - **Expected output**: Returns the `Square` at file `'d'`, rank `4`. Square is non-null.
    - **Implemented at**: getSquare_validInteriorSquare_returnsSquare

- **TC2: Valid Square — Minimum Boundary (a1)** (x)
    - **State of the system**: Board is initialized. `file` = `'a'`, `rank` = `1`.
    - **Expected output**: Returns the `Square` at file `'a'`, rank `1`. Square is non-null.
    - **Implemented at**: getSquare_minimumBoundary_returnsSquare

- **TC3: Valid Square — Maximum Boundary (h8)** (x)
    - **State of the system**: Board is initialized. `file` = `'h'`, `rank` = `8`.
    - **Expected output**: Returns the `Square` at file `'h'`, rank `8`. Square is non-null.
    - **Implemented at**: getSquare_maximumBoundary_returnsSquare

- **TC4: Invalid File — Just Below Minimum (`'`'`)** (x)
    - **State of the system**: Board is initialized. `file` = `` '`' `` (ASCII 96), `rank` = `1`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: getSquare_fileBelowMin_throwsException

- **TC5: Invalid File — Just Above Maximum (`'i'`)** (x)
    - **State of the system**: Board is initialized. `file` = `'i'`, `rank` = `1`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: getSquare_fileAboveMax_throwsException

- **TC6: Invalid Rank — Just Below Minimum (0)** (x)
    - **State of the system**: Board is initialized. `file` = `'a'`, `rank` = `0`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: getSquare_rankBelowMin_throwsException

- **TC7: Invalid Rank — Just Above Maximum (9)** (x)
    - **State of the system**: Board is initialized. `file` = `'a'`, `rank` = `9`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: getSquare_rankAboveMax_throwsException

---

### Method under test: `Board.placePiece(Piece piece, Square square)`

- **TC8: Valid Placement on Empty Square** (x)
    - **State of the system**: Target `square` at (`'e'`, `1`) is empty. `piece` is a non-null `King`.
    - **Expected output**: `square.getOccupant()` returns `piece`; `square.isEmpty()` returns `false`.
    - **Implemented at**: placePiece_emptySquare_placesPiece

- **TC9: Null Piece** (x)
    - **State of the system**: `piece` = `null`. `square` is a valid, empty `Square`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: placePiece_nullPiece_throwsException

- **TC10: Null Square** (x)
    - **State of the system**: `piece` is a valid non-null `Piece`. `square` = `null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: placePiece_nullSquare_throwsException

- **TC11: Square Already Occupied** (x)
    - **State of the system**: Target `square` at (`'a'`, `1`) already has an occupant. `piece` is a valid non-null `Piece`.
    - **Expected output**: Throws `IllegalStateException`.
    - **Implemented at**: placePiece_occupiedSquare_throwsException

---

### Method under test: `Board.removePiece(Square square)`

- **TC12: Valid Removal from Occupied Square** (x)
    - **State of the system**: Target `square` at (`'a'`, `1`) is occupied by a `Rook`.
    - **Expected output**: `square.isEmpty()` returns `true`; `square.getOccupant()` returns `null`.
    - **Implemented at**: removePiece_occupiedSquare_removesPiece

- **TC13: Null Square** (x)
    - **State of the system**: `square` = `null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: removePiece_nullSquare_throwsException

- **TC14: Square Already Empty** (x)
    - **State of the system**: Target `square` at (`'c'`, `3`) is already empty (`isEmpty() == true`).
    - **Expected output**: Throws `IllegalStateException`.
    - **Implemented at**: removePiece_emptySquare_throwsException

---

### Method under test: `Board.movePiece(Square from, Square to)`

- **TC15: Valid Move to Empty Square** (x)
    - **State of the system**: `from` at (`'e'`, `2`) is occupied by a `Pawn`. `to` at (`'e'`, `4`) is empty.
    - **Expected output**: `to.getOccupant()` returns the `Pawn`; `from.isEmpty()` returns `true`.
    - **Implemented at**: movePiece_toEmptySquare_movesPiece

- **TC16: Valid Capture — Move to Opponent-Occupied Square** (x)
    - **State of the system**: `from` at (`'d'`, `5`) is occupied by a white `Bishop`. `to` at (`'f'`, `7`) is occupied by a black `Knight`.
    - **Expected output**: `to.getOccupant()` returns the white `Bishop`; `from.isEmpty()` returns `true`. The black `Knight` is displaced.
    - **Implemented at**: movePiece_toOpponentOccupiedSquare_captures

- **TC17: Null From Square** (x)
    - **State of the system**: `from` = `null`. `to` is a valid non-null `Square`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: movePiece_nullFrom_throwsException

- **TC18: Null To Square** (x)
    - **State of the system**: `from` is a valid, occupied `Square`. `to` = `null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: movePiece_nullTo_throwsException

- **TC19: From Square Is Empty** (x)
    - **State of the system**: `from` at (`'b'`, `3`) is empty (`isEmpty() == true`). `to` is a valid non-null `Square`.
    - **Expected output**: Throws `IllegalStateException`.
    - **Implemented at**: movePiece_fromSquareEmpty_throwsException

- **TC20: From and To Are the Same Square** (x)
    - **State of the system**: `from` and `to` reference the same square (identical file and rank, e.g., (`'d'`, `4`)).
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: movePiece_fromAndToSameSquare_throwsException

- **TC21: Move to Friendly-Occupied Square** (x)
    - **State of the system**: `from` at (`'a'`, `1`) is occupied by a white `Rook`. `to` at (`'a'`, `2`) is occupied by a white `Pawn`.
    - **Expected output**: Throws `IllegalArgumentException` (friendly fire is illegal).
    - **Implemented at**: movePiece_toFriendlyOccupiedSquare_throwsException