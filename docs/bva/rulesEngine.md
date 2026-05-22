# RulesEngine.md

## Intermediate Analysis (Steps 1-3)

**Input Domains & Variables:**
* `color` (Color): Side being evaluated.
    * Valid values: `WHITE`, `BLACK`
    * Invalid value: `null`

* `board` (Board): Current chessboard state.
    * Valid value: A valid `Board` object
    * Invalid value: `null`
    * Important states:
        * King safe
        * King in check
        * King checkmated
        * King has legal escape

* `move` (Move): Proposed chess move.
    * Valid value: A valid `Move` object
    * Invalid value: `null`
    * Important states:
        * Legal movement
        * Illegal movement
        * Capture move
        * Move from/to board boundary such as `a1` or `h8`
        * Move that leaves king in check

* `history` (List<Move>): Previous moves in the game.
    * Valid values: Empty or non-empty move history
    * Invalid value: `null`
    * Important states:
        * Empty history
        * Last move is eligible for en passant
        * Last move is not eligible for en passant

* `king` (King): King used for castling validation.
    * Valid value: Valid `King` object
    * Invalid value: `null`

* `rook` (Rook): Rook used for castling validation.
    * Valid value: Valid `Rook` object
    * Invalid value: `null`

---

**Boundary Values Identified:**

* `color` Boundaries:
    * Valid: `WHITE`, `BLACK`
    * Invalid: `null`

* `board` Boundaries:
    * Valid board
    * `null` board
    * King not in check
    * King in check
    * King in checkmate
    * King in check with escape available

* `move` Boundaries:
    * Valid legal move
    * Invalid movement pattern
    * Boundary square involved: `a1` or `h8`
    * Move captures opponent
    * Move leaves own king in check
    * Invalid: `null`

* `history` Boundaries:
    * Empty history
    * Last move creates en passant target
    * Last move does not create en passant target
    * Invalid: `null`

* `castling` Boundaries:
    * King and rook unmoved, path clear
    * King or rook has already moved
    * Piece exists between king and rook
    * King is in check, passes through check, or lands in check
    * Invalid: `king`, `rook`, or `board` is `null`

## Step 4: Test Cases

### Method under test: `isInCheck(color, board)`

- **TC1: King Not In Check** (V)
    - **State of the system**: Board contains a king that is not attacked.
    - **Expected output**: Returns `false`.
    - **Implemented at** isInCheck_kingSafe_returnsFalse

- **TC2: King In Check** (V)
    - **State of the system**: Board contains a king attacked by an opposing piece.
    - **Expected output**: Returns `true`.
    - **Implemented at** isInCheck_kingAttacked_returnsTrue

- **TC3: Null Input** (x)
    - **State of the system**: `color = null` or `board = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at** isInCheck_nullInput_throwsException

---

### Method under test: `isCheckmate(color, board)`

- **TC4: King Not In Check** (V)
    - **State of the system**: King is not currently in check.
    - **Expected output**: Returns `false`.
    - **Implemented at** isCheckmate_kingNotInCheck_returnsFalse

- **TC5: King In Check But Escape Exists** (V)
    - **State of the system**: King is in check but can move, block, or capture to escape.
    - **Expected output**: Returns `false`.
    - **Implemented at** isCheckmate_escapeExists_returnsFalse

- **TC6: King Checkmated** (V)
    - **State of the system**: King is in check and has no legal escape.
    - **Expected output**: Returns `true`.
    - **Implemented at** isCheckmate_noEscape_returnsTrue

- **TC7: Null Input** (x)
    - **State of the system**: `color = null` or `board = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at** isCheckmate_nullInput_throwsException

---

### Method under test: `isLegalMove(move, board)`

- **TC8: Valid Normal Move** (V)
    - **State of the system**: A piece moves according to its rules to an empty square.
    - **Expected output**: Returns `true`.
    - **Implemented at** isLegalMove_validNormalMove_returnsTrue

- **TC9: Valid Capture Move** (V)
    - **State of the system**: A piece legally captures an opposing piece.
    - **Expected output**: Returns `true`.
    - **Implemented at** isLegalMove_validCapture_returnsTrue

- **TC10: Boundary Square Move** (V)
    - **State of the system**: A piece legally moves from or to a boundary square such as `a1` or `h8`.
    - **Expected output**: Returns `true`.
    - **Implemented at** isLegalMove_boundarySquare_returnsTrue

- **TC11: Illegal Piece Movement** (V)
    - **State of the system**: A piece attempts to move in a way not allowed by its movement rules.
    - **Expected output**: Returns `false`.
    - **Implemented at** isLegalMove_invalidPieceMovement_returnsFalse

- **TC12: Move Leaves King In Check** (V)
    - **State of the system**: Move follows piece rules but exposes the moving side’s king to check.
    - **Expected output**: Returns `false`.
    - **Implemented at** isLegalMove_exposesKing_returnsFalse

- **TC13: Null Input** (x)
    - **State of the system**: `move = null` or `board = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at** isLegalMove_nullInput_throwsException

---

### Method under test: `getEnPassantTarget(history)`

- **TC14: Empty History** (V)
    - **State of the system**: Move history is empty.
    - **Expected output**: Returns `null`.
    - **Implemented at** getEnPassantTarget_emptyHistory_returnsNull

- **TC15: Last Move Is Eligible Pawn Double Move** (V)
    - **State of the system**: Last move was a pawn moving two squares from its starting rank.
    - **Expected output**: Returns the skipped square.
    - **Implemented at** getEnPassantTarget_doublePawnMove_returnsTarget

- **TC16: Last Move Not Eligible** (V)
    - **State of the system**: Last move was not a two-square pawn move.
    - **Expected output**: Returns `null`.
    - **Implemented at** getEnPassantTarget_notEligible_returnsNull

- **TC17: Null History** (x)
    - **State of the system**: `history = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at** getEnPassantTarget_nullHistory_throwsException

---

### Method under test: `isCastlingLegal(king, rook, board)`

- **TC18: Legal Castling** (V)
    - **State of the system**: King and rook have not moved, path is clear, king is not in check, and king does not pass through or land in check.
    - **Expected output**: Returns `true`.
    - **Implemented at** isCastlingLegal_validCastling_returnsTrue

- **TC19: King Or Rook Has Moved** (V)
    - **State of the system**: King or rook has previously moved.
    - **Expected output**: Returns `false`.
    - **Implemented at** isCastlingLegal_pieceMoved_returnsFalse

- **TC20: Piece Between King And Rook** (V)
[    - **State of the system**: At least one square between the king and rook is occupied.
    - **Expected output**: Returns `false`.
    - **Implemented at** isCastlingLegal_pathBlocked_returnsFalse
]()
- **TC21: King In Or Passes Through Check** (V)
    - **State of the system**: King is currently in check, passes through check, or lands in check.
    - **Expected output**: Returns `false`.
    - **Implemented at** isCastlingLegal_checkInPath_returnsFalse

- **TC22: Null Input** (x)
    - **State of the system**: `king = null`, `rook = null`, or `board = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at** isCastlingLegal_nullInput_throwsException