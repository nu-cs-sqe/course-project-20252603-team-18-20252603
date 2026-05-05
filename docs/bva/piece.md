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

## Method under test: `Piece(Color color, PieceType type)`
- **TC1: Constructor creates valid white queen piece** ( :x: )
  - **State of the system**: System instantiates a concrete test subclass of `Piece` with `color = WHITE`, `type = QUEEN`.
  - **Expected output**: Piece is created successfully; `getColor()` returns `WHITE`; `getType()` returns `QUEEN`; `hasMoved()` returns `false`.
  - **Test name**: `constructor_validWhiteQueen_createsPiece`

- **TC2: Constructor creates valid black pawn piece** ( :x: )
  - **State of the system**: System instantiates a concrete test subclass of `Piece` with `color = BLACK`, `type = PAWN`.
  - **Expected output**: Piece is created successfully; `getColor()` returns `BLACK`; `getType()` returns `PAWN`; `hasMoved()` returns `false`.
  - **Test name**: `constructor_validBlackPawn_createsPiece`

- **TC3: Constructor with null color throws exception** ( :x: )
  - **State of the system**: System instantiates a concrete test subclass of `Piece` with `color = null`, `type = QUEEN`.
  - **Expected output**: Throws `IllegalArgumentException`.
  - **Test name**: `constructor_nullColor_throwsException`

- **TC4: Constructor with null type throws exception** ( :x: )
  - **State of the system**: System instantiates a concrete test subclass of `Piece` with `color = WHITE`, `type = null`.
  - **Expected output**: Throws `IllegalArgumentException`.
  - **Test name**: `constructor_nullType_throwsException`

- **TC5: Constructor with null color and null type throws exception** ( :x: )
  - **State of the system**: System instantiates a concrete test subclass of `Piece` with `color = null`, `type = null`.
  - **Expected output**: Throws `IllegalArgumentException`.
  - **Test name**: `constructor_nullColorAndNullType_throwsException`



## Method under test: `getColor()`
- **TC6: Get color on white piece returns white** ( :x: )
  - **State of the system**: A concrete test subclass of `Piece` exists with `color = WHITE`, `type = ROOK`.
  - **Expected output**: `getColor()` returns `WHITE`.
  - **Test name**: `getColor_whitePiece_returnsWhite`

- **TC7: Get color on black piece returns black** ( :x: )
  - **State of the system**: A concrete test subclass of `Piece` exists with `color = BLACK`, `type = BISHOP`.
  - **Expected output**: `getColor()` returns `BLACK`.
  - **Test name**: `getColor_blackPiece_returnsBlack`


## Method under test: `getType()`
- **TC8: Get type on king piece returns queen** ( :x: )
  - **State of the system**: A concrete test subclass of `Piece` exists with `color = WHITE`, `type = QUEEN`.
  - **Expected output**: `getType()` returns `QUEEN`.
  - **Test name**: `getType_queenPiece_returnsQueen`

- **TC9: Get type on pawn piece returns pawn** ( :x: )
  - **State of the system**: A concrete test subclass of `Piece` exists with `color = BLACK`, `type = PAWN`.
  - **Expected output**: `getType()` returns `PAWN`.
  - **Test name**: `getType_pawnPiece_returnsPawn`



## Method under test: `hasMoved()`
- **TC10: New piece has not moved** ( :x: )
  - **State of the system**: A concrete test subclass of `Piece` has just been created.
  - **Expected output**: `hasMoved()` returns `false`.
  - **Test name**: `hasMoved_newPiece_returnsFalse`

- **TC11: Piece returns moved after markMoved is called** ( :x: )
  - **State of the system**: A concrete test subclass of `Piece` exists and `markMoved()` has been called once.
  - **Expected output**: `hasMoved()` returns `true`.
  - **Test name**: `hasMoved_afterMarkMoved_returnsTrue`



## Method under test: `markMoved()`
- **TC12: Mark new piece as moved** ( :x: )
  - **State of the system**: A newly created concrete test subclass of `Piece` exists with `hasMoved() = false`. System calls `markMoved()`.
  - **Expected output**: The piece's moved state is updated; `hasMoved()` returns `true`.
  - **Test name**: `markMoved_newPiece_updatesHasMovedToTrue`

- **TC13: Mark already moved piece as moved again** ( :x: )
  - **State of the system**: A concrete test subclass of `Piece` already has `hasMoved() = true`. System calls `markMoved()` again.
  - **Expected output**: The piece's moved state remains `true`.
  - **Test name**: `markMoved_alreadyMovedPiece_remainsTrue`