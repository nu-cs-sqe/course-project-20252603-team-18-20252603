# Move.md

## Intermediate Analysis (Steps 1–3)

**Input Domains & Variables:**

* `piece` (Piece): The piece being moved.
    * Valid values: Any non-null instantiated `Piece` object (King, Queen, Rook, Bishop, Knight, or Pawn).
    * Invalid values: `null`.

* `from` (Square): The square the piece is moving from.
    * Valid values: Any valid, non-null `Square` on the board (file `'a'`–`'h'`, rank `1`–`8`) that is occupied by `piece`.
    * Invalid values: `null`; a `Square` whose `occupant` does not match `piece`.

* `to` (Square): The destination square.
    * Valid values: Any valid, non-null `Square` on the board (file `'a'`–`'h'`, rank `1`–`8`) that is reachable by a legal move for `piece`, and is not the same square as `from`.
    * Invalid values: `null`; same square as `from`; a square that is out of bounds or unreachable by a legal move.

* `capturedPiece` (Piece): The opponent's piece removed during the move.
    * Valid values: An instantiated opponent `Piece` object (when a capture occurs), or `null` (when no capture occurs).
    * Note: Cannot be a piece of the same color as `piece`. Cannot be a King (per use case 3).

* `isEnPassant` (boolean): Flags the move as an en passant capture.
    * Valid values: `true`, `false`.
    * Constraint: Can only be `true` if `piece` is a Pawn, `capturedPiece` is a non-null opponent Pawn, and the destination square (`to`) does not contain `capturedPiece` (the captured pawn is on an adjacent square, not `to`).

* `isCastle` (boolean): Flags the move as a castling move.
    * Valid values: `true`, `false`.
    * Constraint: Can only be `true` if `piece` is a King, neither the King nor the involved Rook has previously moved (`hasMoved == false`), and there are no pieces between them.

* `promotionPiece` (Piece): The `Piece` instance a Pawn is promoted to upon reaching the final rank.
    * Valid values: A non-null `Piece` object of type Queen, Rook, Bishop, or Knight (when promotion is applicable), or `null` (when the move is not a promotion).
    * Invalid values: A `Piece` of type King or Pawn, or any non-null value when `piece` is not a Pawn reaching the final rank.

* `causedCheck` (boolean): Indicates that this move left the opponent's King in check.
    * Valid values: `true`, `false`.
    * Note: Evaluated by the system post-move; not a caller-supplied input per se, but must be consistent with the resulting board state.

* `causedCheckmate` (boolean): Indicates that this move resulted in checkmate.
    * Valid values: `true`, `false`.
    * Constraint: Can only be `true` if `causedCheck` is also `true`. Cannot be `true` if `causedCheck` is `false`.

**Boundary Values Identified:**

* `piece` Boundaries:
    * Valid state: Any non-null instantiated `Piece` subclass.
    * Invalid state: `null`.

* `from` / `to` (Square) Boundaries:
    * Min file: `'a'` | Max file: `'h'`
    * Min rank: `1` | Max rank: `8`
    * Invalid (just outside): file `'`'` (ASCII 96) or `'i'`; rank `0` or `9`.
    * Invalid structural: `null`; `from` == `to` (zero-distance move).

* `capturedPiece` Boundaries:
    * Valid non-capture state: `null`.
    * Valid capture state: Non-null opponent `Piece`.
    * Invalid: Same-color piece as `piece`; a King.

* `isEnPassant` / `isCastle` / `causedCheck` / `causedCheckmate` Boundaries:
    * Both boolean fields: `true`, `false`.
    * `causedCheckmate = true` requires `causedCheck = true`.

* `promotionPiece` Boundaries:
    * Valid promotion values: A non-null `Piece` instance of type Queen, Rook, Bishop, or Knight.
    * Valid non-promotion value: `null`.
    * Invalid values: A `Piece` instance of type King or Pawn.

---

## Step 4: Test Cases

### Method under test: `Move.create(Piece piece, Square from, Square to)`

- **TC1: Valid Standard Move (Non-capture, Non-special)** (x)
    - **State of the system**: A non-null `Piece` occupies `from` ('e', 2). `to` is ('e', 4) — a legal destination. No capture, no special flags.
    - **Expected output**: `Move` created successfully. `capturedPiece` defaults to `null`, `isEnPassant` defaults to `false`, `isCastle` defaults to `false`, `promotionPiece` defaults to `null`, `causedCheck` defaults to `false`, `causedCheckmate` defaults to `false`.
    - **Implemented at**: constructor_validStandardMove_createsMove

- **TC2: Null Piece** (x)
    - **State of the system**: System calls `Move.create(...)` with `piece = null`, valid `from` ('a', 1), valid `to` ('a', 2).
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: constructor_nullPiece_throwsException

- **TC3: Null From Square** (x)
    - **State of the system**: System calls `Move.create(...)` with a valid `Piece`, `from = null`, valid `to` ('a', 2).
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: constructor_nullFromSquare_throwsException

- **TC4: Null To Square** (x)
    - **State of the system**: System calls `Move.create(...)` with a valid `Piece`, valid `from` ('a', 1), `to = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: constructor_nullToSquare_throwsException

- **TC5: From and To Are the Same Square** (x)
    - **State of the system**: System calls `Move.create(...)` with a valid `Piece`, `from` = ('d', 4), `to` = ('d', 4) (identical square references or equivalent file/rank).
    - **Expected output**: Throws `IllegalArgumentException` (a zero-distance move is illegal).
    - **Implemented at**: constructor_fromAndToSameSquare_throwsException

- **TC6: Piece Does Not Match From Square Occupant** (x)
    - **State of the system**: System calls `Move.create(...)` where `piece` is a valid `Piece` but `from.occupant` is a different `Piece` (or `null`).
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: constructor_pieceMismatchFromOccupant_throwsException