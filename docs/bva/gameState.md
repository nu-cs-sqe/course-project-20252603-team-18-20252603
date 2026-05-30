# gameState.md

## Intermediate Analysis (Steps 1–3)

### Input Domains & Variables

---

#### `board` (`Board`)

The board snapshot passed into the constructor.

**Valid values:**

* A non-null, fully initialized `Board` with an 8×8 grid.
* Files `'a'` through `'h'`, ranks `1` through `8`.
* Each square contains either `null` or exactly one `Piece`.

**Invalid values:**

* `null` — constructor must throw `IllegalArgumentException`.
* Board whose squares contain coordinates outside `'a'`–`'h'` or `1`–`8`.

**Boundary values:**

* Minimum valid file: `'a'`; maximum: `'h'`.
* Minimum valid rank: `1`; maximum: `8`.
* Invalid file below minimum: `` ` ``; above maximum: `'i'`.
* Invalid rank below minimum: `0`; above maximum: `9`.

---

#### `currentTurn` (`Color`)

The color of the player whose turn it is at the moment this snapshot is taken.

**Valid values:**

* `Color.WHITE`
* `Color.BLACK`

**Invalid values:**

* `null` — constructor must throw `IllegalArgumentException`.

**Boundary values:**

* `WHITE` — minimum enum value; valid.
* `BLACK` — maximum enum value; valid.
* `null` — only invalid value; constructor must reject it.

**Constraints:**

* `currentTurn` must equal the color of the piece in any `Move` that will be validated
  against this snapshot. This is enforced by `RulesEngine`, not by `GameState` itself,
  but the snapshot must faithfully reflect the turn at the moment it was taken.

---

#### `lastMove` (`Move`)

The most recently completed move prior to this snapshot, used by `RulesEngine` to
determine en passant eligibility.

**Valid values:**

* `null` — valid at game start (no move has been made yet) and at any point where
  en passant eligibility is irrelevant to the caller.
* A non-null, fully constructed `Move` object representing the last completed move.

**Invalid values:**

* A `Move` whose `piece`, `from`, or `to` fields are `null` (violates `Move.create`
  invariants, so such an object cannot legally be constructed; guarded upstream).

**Boundary values:**

* `null` — valid base case; represents the start of the game or an irrelevant history.
* A `Move` where the piece was a Pawn advancing exactly two squares — the only case
  that opens an en passant window in the next turn.
* A `Move` where the piece was a Pawn advancing exactly one square — no en passant
  window opened.
* A `Move` where the piece was not a Pawn at all — no en passant window opened.
* A `Move` where a King moved — signals loss of castling rights for that color
  (relevant if `RulesEngine` reads `lastMove` for castling validation in future).

**Constraints:**

* `lastMove` is read-only after construction. `GameModel` is responsible for providing
  the correct last move when calling `snapshot()`; `GameState` does not validate that
  `lastMove` is consistent with the board contents.

---

### Boundary Values Identified

#### Constructor Parameter Boundaries

| Parameter      | Valid boundary (min) | Valid boundary (max) | Invalid               |
|----------------|----------------------|----------------------|-----------------------|
| `board`        | Non-null `Board`     | Non-null `Board`     | `null`                |
| `currentTurn`  | `WHITE`              | `BLACK`              | `null`                |
| `lastMove`     | `null` (no history)  | Any valid `Move`     | (cannot be malformed) |

#### `lastMove` En Passant Boundary Cases

* `lastMove == null` → no en passant available.
* `lastMove.piece` is a Pawn, advance distance == 2 → en passant window open.
* `lastMove.piece` is a Pawn, advance distance == 1 → no en passant window.
* `lastMove.piece` is not a Pawn → no en passant window.

---

## Step 4: Test Cases

### Method under test: `GameState(Board board, Color currentTurn, Move lastMove)`

#### TC1: Valid Construction — White Turn, No Last Move

* **State of the system**: A non-null `Board` is provided. `currentTurn = WHITE`.
  `lastMove = null` (game start).
* **Expected output**: `GameState` is created. `getBoard()` returns the supplied board.
  `getCurrentTurn()` returns `WHITE`. `getLastMove()` returns `null`.
* **Implemented at**: `constructor_validWhiteTurnNullLastMove_createsGameState`

#### TC2: Valid Construction — Black Turn, No Last Move

* **State of the system**: A non-null `Board` is provided. `currentTurn = BLACK`.
  `lastMove = null`.
* **Expected output**: `GameState` is created. `getCurrentTurn()` returns `BLACK`.
  `getLastMove()` returns `null`.
* **Implemented at**: `constructor_validBlackTurnNullLastMove_createsGameState`

#### TC3: Valid Construction — With Last Move Supplied

* **State of the system**: A non-null `Board`, `currentTurn = BLACK`, and a valid
  non-null `Move` are provided.
* **Expected output**: `GameState` is created. `getLastMove()` returns the supplied move.
* **Implemented at**: `constructor_validWithLastMove_createsGameState`

#### TC4: Null Board

* **State of the system**: `board = null`, valid `currentTurn`, valid `lastMove`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `constructor_nullBoard_throwsException`

#### TC5: Null currentTurn

* **State of the system**: Valid non-null `Board`, `currentTurn = null`, valid `lastMove`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `constructor_nullCurrentTurn_throwsException`

#### TC6: Null lastMove Is Accepted

* **State of the system**: Valid non-null `Board`, valid `currentTurn`, `lastMove = null`.
* **Expected output**: No exception thrown. `getLastMove()` returns `null`.
* **Implemented at**: `constructor_nullLastMove_isAccepted`

---

### Method under test: `GameState.getBoard()`

#### TC7: Returns The Board Supplied At Construction

* **State of the system**: `GameState` constructed with a specific `Board` instance.
* **Expected output**: `getBoard()` returns the exact same `Board` reference that was
  passed to the constructor.
* **Implemented at**: `getBoard_returnsBoardSuppliedAtConstruction`

---

### Method under test: `GameState.getCurrentTurn()`

#### TC8: Returns WHITE When Constructed With WHITE

* **State of the system**: `GameState` constructed with `currentTurn = WHITE`.
* **Expected output**: `getCurrentTurn()` returns `Color.WHITE`.
* **Implemented at**: `getCurrentTurn_white_returnsWhite`

#### TC9: Returns BLACK When Constructed With BLACK

* **State of the system**: `GameState` constructed with `currentTurn = BLACK`.
* **Expected output**: `getCurrentTurn()` returns `Color.BLACK`.
* **Implemented at**: `getCurrentTurn_black_returnsBlack`

---

### Method under test: `GameState.getLastMove()`

#### TC10: Returns Null When No Last Move Was Supplied

* **State of the system**: `GameState` constructed with `lastMove = null`.
* **Expected output**: `getLastMove()` returns `null`.
* **Implemented at**: `getLastMove_null_returnsNull`

#### TC11: Returns Last Move When One Was Supplied

* **State of the system**: `GameState` constructed with a valid non-null `Move`.
* **Expected output**: `getLastMove()` returns the exact same `Move` reference supplied
  at construction.
* **Implemented at**: `getLastMove_validMove_returnsSuppliedMove`

#### TC12: Last Move Was A Two-Square Pawn Advance

* **State of the system**: `GameState` constructed with a `lastMove` where the piece is
  a Pawn that advanced two squares.
* **Expected output**: `getLastMove()` returns the move. `getLastMove().getPiece().getType()`
  equals `PieceType.PAWN`. The rank difference between `getLastMove().getFrom()` and
  `getLastMove().getTo()` equals 2 (White) or -2 (Black).
* **Implemented at**: `getLastMove_twoSquarePawnAdvance_enPassantWindowDetectable`

#### TC13: Last Move Was A One-Square Pawn Advance

* **State of the system**: `GameState` constructed with a `lastMove` where the piece is
  a Pawn that advanced one square.
* **Expected output**: `getLastMove()` returns the move. Rank difference between
  `from` and `to` equals 1 (White) or -1 (Black).
* **Implemented at**: `getLastMove_oneSquarePawnAdvance_noEnPassantWindow`

#### TC14: Last Move Was Not A Pawn Move

* **State of the system**: `GameState` constructed with a `lastMove` where the piece is
  a Rook.
* **Expected output**: `getLastMove()` returns the move. `getLastMove().getPiece().getType()`
  does not equal `PieceType.PAWN`.
* **Implemented at**: `getLastMove_nonPawnMove_noEnPassantWindow`

---

### Immutability Verification

#### TC15: getBoard Returns Same Reference On Repeated Calls

* **State of the system**: `GameState` is constructed and `getBoard()` is called twice.
* **Expected output**: Both calls return the same object reference. No defensive copy
  is required since `Board` mutation is `GameModel`'s responsibility; `GameState` simply
  must not replace or null out its reference between calls.
* **Implemented at**: `getBoard_calledTwice_returnsSameReference`

#### TC16: getCurrentTurn Returns Same Value On Repeated Calls

* **State of the system**: `GameState` is constructed with `WHITE` and `getCurrentTurn()`
  is called twice.
* **Expected output**: Both calls return `WHITE`. The value cannot change after
  construction.
* **Implemented at**: `getCurrentTurn_calledTwice_returnsSameValue`