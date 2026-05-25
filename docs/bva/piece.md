# BVA Analysis for Piece

## Intermediate Analysis (Steps 1-3)

**Input Domains & Variables:**
* `color` (Color): Represents the color assigned to the chess piece.
  * Valid values: `WHITE`, `BLACK`
  * Invalid value: `null`
* `type` (PieceType): Represents the type of chess piece.
  * Valid values: `KING`, `QUEEN`, `ROOK`, `BISHOP`, `KNIGHT`, `PAWN`
  * Invalid value: `null`
* `hasMoved` (boolean): Represents whether the piece has moved from its starting position.
  * Initial value: `false`
  * Updated value: `true`

**Boundary Values Identified:**
* `color` boundaries:
  * Valid states: `WHITE`, `BLACK`
  * Invalid/null state: `null`
* `type` boundaries:
  * Valid states: `KING`, `QUEEN`, `ROOK`, `BISHOP`, `KNIGHT`, `PAWN`
  * Invalid/null state: `null`
* `hasMoved` boundaries:
  * Initial state: `false`
  * Updated state: `true`

---

## Method under test: `Piece.create(Color color, PieceType type)`
- **TC1: Create valid white queen piece** ( :white_check_mark: )
  - **State of the system**: System calls `Piece.create(...)` with `color = WHITE`, `type = QUEEN`.
  - **Expected output**: Piece is created successfully; `getColor()` returns `WHITE`; `getType()` returns `QUEEN`; `hasMoved()` returns `false`.
  - **Test name**: `create_validWhiteQueen_createsPiece`

- **TC2: Create valid black pawn piece** ( :white_check_mark: )
  - **State of the system**: System calls `Piece.create(...)` with `color = BLACK`, `type = PAWN`.
  - **Expected output**: Piece is created successfully; `getColor()` returns `BLACK`; `getType()` returns `PAWN`; `hasMoved()` returns `false`.
  - **Test name**: `create_validBlackPawn_createsPiece`

- **TC3: Create with null color throws exception** ( :white_check_mark: )
  - **State of the system**: System calls `Piece.create(...)` with `color = null`, `type = QUEEN`.
  - **Expected output**: Throws `IllegalArgumentException`.
  - **Test name**: `create_nullColor_throwsException`

- **TC4: Create with null type throws exception** ( :white_check_mark: )
  - **State of the system**: System calls `Piece.create(...)` with `color = WHITE`, `type = null`.
  - **Expected output**: Throws `IllegalArgumentException`.
  - **Test name**: `create_nullType_throwsException`

- **TC5: Create with null color and null type throws exception** ( :white_check_mark: )
  - **State of the system**: System calls `Piece.create(...)` with `color = null`, `type = null`.
  - **Expected output**: Throws `IllegalArgumentException`.
  - **Test name**: `create_nullColorAndNullType_throwsException`


## Method under test: `getColor()`
- **TC6: Get color on white piece returns white** ( :white_check_mark: )
  - **State of the system**: A `Piece` exists with `color = WHITE`, `type = ROOK`.
  - **Expected output**: `getColor()` returns `WHITE`.
  - **Test name**: `getColor_whitePiece_returnsWhite`

- **TC7: Get color on black piece returns black** ( :white_check_mark: )
  - **State of the system**: A `Piece` exists with `color = BLACK`, `type = BISHOP`.
  - **Expected output**: `getColor()` returns `BLACK`.
  - **Test name**: `getColor_blackPiece_returnsBlack`


## Method under test: `getType()`
- **TC8: Get type on queen piece returns queen** ( :white_check_mark: )
  - **State of the system**: A `Piece` exists with `color = WHITE`, `type = QUEEN`.
  - **Expected output**: `getType()` returns `QUEEN`.
  - **Test name**: `getType_queenPiece_returnsQueen`

- **TC9: Get type on pawn piece returns pawn** ( :white_check_mark: )
  - **State of the system**: A `Piece` exists with `color = BLACK`, `type = PAWN`.
  - **Expected output**: `getType()` returns `PAWN`.
  - **Test name**: `getType_pawnPiece_returnsPawn`


## Method under test: `hasMoved()`
- **TC10: New piece has not moved** ( :white_check_mark: )
  - **State of the system**: A `Piece` has just been created.
  - **Expected output**: `hasMoved()` returns `false`.
  - **Test name**: `hasMoved_newPiece_returnsFalse`

- **TC11: Piece returns moved after markMoved is called** ( :white_check_mark: )
  - **State of the system**: A `Piece` exists and `markMoved()` has been called once.
  - **Expected output**: `hasMoved()` returns `true`.
  - **Test name**: `hasMoved_afterMarkMoved_returnsTrue`


## Method under test: `markMoved()`
- **TC12: Mark new piece as moved** ( :white_check_mark: )
  - **State of the system**: A newly created `Piece` exists with `hasMoved() = false`. System calls `markMoved()`.
  - **Expected output**: The piece's moved state is updated; `hasMoved()` returns `true`.
  - **Test name**: `markMoved_newPiece_updatesHasMovedToTrue`

- **TC13: Mark already moved piece as moved again** ( :white_check_mark: )
  - **State of the system**: A `Piece` already has `hasMoved() = true`. System calls `markMoved()` again.
  - **Expected output**: The piece's moved state remains `true`.
  - **Test name**: `markMoved_alreadyMovedPiece_remainsTrue`