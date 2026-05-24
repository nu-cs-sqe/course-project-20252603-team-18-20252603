# BVA Analysis for Player

## Intermediate Analysis (Steps 1-3)

**Input Domains & Variables:**
* `color` (Color): Represents the color assigned to the player.
    * Valid values: `WHITE`, `BLACK`
    * Invalid value: `null`
* `isHuman` (boolean): Represents whether the player is controlled by a human or computer.
    * Valid values: `true`, `false`
* `capturedPieces` (List<Piece>): Represents the pieces captured by this player.
    * Initial value: empty list
    * Valid values to add: non-null `Piece` object
    * Invalid value to add: `null`

**Boundary Values Identified:**
* `color` boundaries:
    * Valid states: `WHITE`, `BLACK`
    * Invalid/null state: `null`
* `isHuman` boundaries:
    * Human player: `true`
    * Computer player: `false`
* `capturedPieces` boundaries:
    * Empty list: no captured pieces
    * One captured piece
    * Multiple captured pieces
    * Invalid add attempt: `null`

---

## Method under test: `Player(Color color, boolean isHuman)`

- **TC1: Constructor creates white human player** ( :x: )
    - **State of the system**: System instantiates `Player` with `color = WHITE`, `isHuman = true`.
    - **Expected output**: Player is created successfully; `getColor()` returns `WHITE`; `isHuman()` returns `true`; `getCapturedPieces()` returns an empty list.
    - **Test name**: `constructor_validWhiteHumanPlayer_createsPlayer`

- **TC2: Constructor creates black computer player** ( :x: )
    - **State of the system**: System instantiates `Player` with `color = BLACK`, `isHuman = false`.
    - **Expected output**: Player is created successfully; `getColor()` returns `BLACK`; `isHuman()` returns `false`; `getCapturedPieces()` returns an empty list.
    - **Test name**: `constructor_validBlackComputerPlayer_createsPlayer`

- **TC3: Constructor with null color throws exception** ( :x: )
    - **State of the system**: System instantiates `Player` with `color = null`, `isHuman = true`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Test name**: `constructor_nullColor_throwsException`

---

## Method under test: `getColor()`

- **TC4: Get color on white player returns white** ( :x: )
    - **State of the system**: A `Player` exists with `color = WHITE`, `isHuman = true`.
    - **Expected output**: `getColor()` returns `WHITE`.
    - **Test name**: `getColor_whitePlayer_returnsWhite`

- **TC5: Get color on black player returns black** ( :x: )
    - **State of the system**: A `Player` exists with `color = BLACK`, `isHuman = false`.
    - **Expected output**: `getColor()` returns `BLACK`.
    - **Test name**: `getColor_blackPlayer_returnsBlack`

---

## Method under test: `isHuman()`

- **TC6: Human player returns true** ( :x: )
    - **State of the system**: A `Player` exists with `isHuman = true`.
    - **Expected output**: `isHuman()` returns `true`.
    - **Test name**: `isHuman_humanPlayer_returnsTrue`

- **TC7: Computer player returns false** ( :x: )
    - **State of the system**: A `Player` exists with `isHuman = false`.
    - **Expected output**: `isHuman()` returns `false`.
    - **Test name**: `isHuman_computerPlayer_returnsFalse`

---

## Method under test: `getCapturedPieces()`

- **TC8: New player has no captured pieces** ( :x: )
    - **State of the system**: A newly created `Player` exists.
    - **Expected output**: `getCapturedPieces()` returns an empty list.
    - **Test name**: `getCapturedPieces_newPlayer_returnsEmptyList`

- **TC9: Player with one captured piece returns list containing that piece** ( :x: )
    - **State of the system**: A `Player` exists and one valid `Piece` has been added using `addCapturedPiece(piece)`.
    - **Expected output**: `getCapturedPieces()` returns a list of size 1 containing the captured piece.
    - **Test name**: `getCapturedPieces_oneCapturedPiece_returnsListWithPiece`

- **TC10: Player with multiple captured pieces returns all captured pieces** ( :x: )
    - **State of the system**: A `Player` exists and two valid `Piece` objects have been added using `addCapturedPiece(piece)`.
    - **Expected output**: `getCapturedPieces()` returns a list of size 2 containing both captured pieces.
    - **Test name**: `getCapturedPieces_multipleCapturedPieces_returnsAllPieces`

---

## Method under test: `addCapturedPiece(Piece piece)`

- **TC11: Add one captured piece** ( :x: )
    - **State of the system**: A newly created `Player` exists with no captured pieces. System calls `addCapturedPiece(piece)` with a valid non-null `Piece`.
    - **Expected output**: The captured pieces list contains exactly one piece, and that piece is the added piece.
    - **Test name**: `addCapturedPiece_validPiece_addsPiece`

- **TC12: Add multiple captured pieces** ( :x: )
    - **State of the system**: A `Player` exists. System calls `addCapturedPiece(piece)` twice with two different valid non-null `Piece` objects.
    - **Expected output**: The captured pieces list contains both pieces in the order they were added.
    - **Test name**: `addCapturedPiece_multiplePieces_addsAllPieces`

- **TC13: Add null captured piece throws exception** ( :x: )
    - **State of the system**: A `Player` exists. System calls `addCapturedPiece(null)`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Test name**: `addCapturedPiece_nullPiece_throwsException`