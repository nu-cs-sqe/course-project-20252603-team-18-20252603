# GameModel Boundary Value Analysis (BVA)

## Intermediate Analysis (Steps 1–3)

### Input Domains & Variables

---

#### `white` (`Player`) — parameter to `GameModel.newGame(Player white, Player black)`

The player assigned the White pieces.

**Valid values:**

* A non-null `Player` object whose `color` is `Color.WHITE`.
* May be human or a computer opponent (`isHuman` is not inspected by `GameModel`).

**Invalid values:**

* `null`.
* A `Player` whose `color` is `Color.BLACK`.
* A `Player` whose `color` is `null`.

**Boundary values:**

* Valid: non-null `Player` with `color = WHITE`.
* Invalid: `null`.
* Invalid: non-null `Player` with `color = BLACK` (color mismatch).

---

#### `black` (`Player`) — parameter to `GameModel.newGame(Player white, Player black)`

The player assigned the Black pieces.

**Valid values:**

* A non-null `Player` object whose `color` is `Color.BLACK`.
* May be human or a computer opponent.

**Invalid values:**

* `null`.
* A `Player` whose `color` is `Color.WHITE`.
* A `Player` whose `color` is `null`.

**Boundary values:**

* Valid: non-null `Player` with `color = BLACK`.
* Invalid: `null`.
* Invalid: non-null `Player` with `color = WHITE` (color mismatch).

---

#### `currentTurn` (`Color`) — internal field, initialised by `newGame()`

The color of the player whose turn it currently is.

**Valid values:**

* `WHITE` (always the initial value after construction).
* `BLACK` (after the first legal move has been applied).

**Invalid values:**

* `null`.

**Constraints:**

* Must be initialised to `WHITE` by the constructor.
* Must alternate between `WHITE` and `BLACK` after each successfully applied move.
* A move submitted by a piece whose color does not match `currentTurn` must be rejected before any other check is performed.

**Boundary values:**

* Initial state: `currentTurn = WHITE` — a wrong-colour move is rejected immediately.
* After first legal move: `currentTurn = BLACK` — a second same-colour move is now rejected.

---

#### `status` (`GameStatus`) — internal field, initialised by `newGame()`

The current state of the game.

**Valid values:**

* `ONGOING` — neither player is in check; both have legal moves.
* `CHECK` — the current player's king is in check but at least one legal move exists.
* `CHECKMATE` — the current player's king is in check and no legal move resolves it.
* `STALEMATE` — the current player is not in check but has no legal moves.

**Invalid values:**

* `null`.
* `ONGOING` or `CHECK` after checkmate or stalemate has been reached.

**Constraints:**

* Must be initialised to `ONGOING` by the constructor.
* Updated by `applyMove` by calling `rulesEngine.getGameStatus(snapshot())` after each legal move.
* Once terminal (`CHECKMATE` or `STALEMATE`), all subsequent `applyMove` calls must throw `IllegalStateException` before performing any other work.

**Boundary values:**

* Initial state: `ONGOING`.
* Transition to `CHECK`: engine returns `CHECK` after a move.
* Transition to `CHECKMATE`: engine returns `CHECKMATE`; game locks.
* Transition to `STALEMATE`: engine returns `STALEMATE`; game locks.

---

#### `board` (`Board`) — internal collaborator, injected or created by constructor

The 8×8 board owned by the model.

**Constraints from `GameModel`'s perspective:**

* `GameModel` calls `board.getSquare(file, rank)` and `board.placePiece(piece, square)` exactly 32 times during initialisation — once per piece in the standard starting position.
* `GameModel` calls `board.movePiece(from, to)` for every legal non-special move.
* `GameModel` is responsible for the order and type of placement calls; it is not responsible for `Board`'s internal correctness.

**Boundary values for placement:**

* White pawns: 8 calls with `new Pawn(Color.WHITE)` on rank 2 files `'a'`–`'h'`.
* Black pawns: 8 calls with `new Pawn(Color.BLACK)` on rank 7 files `'a'`–`'h'`.
* White back rank: Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook on rank 1.
* Black back rank: Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook on rank 8.
* Total: exactly 32 `placePiece` calls, no more, no less.

---

#### `moveHistory` (`List<Move>`) — internal field, initialised to empty

The ordered list of all moves applied during the game.

**Constraints from `GameModel`'s perspective:**

* Initialised to an empty `ArrayList` by the constructor.
* `GameModel` appends the `Move` object to the list after every successful `applyMove` call.
* The last element is exposed to `snapshot()` as `lastMove`; when the list is empty, `lastMove` is `null`.

**Boundary values:**

* `0` entries: initial state — `snapshot()` passes `null` as `lastMove`.
* `1` entry after the first legal move: list contains exactly that `Move` instance.

---

#### `square` (`Square`) — parameter to `getLegalMoves(Square square)`

**Constraints from `GameModel`'s perspective:**

* `GameModel`'s only responsibility is to pass the square directly to `rulesEngine.getLegalMoves(snapshot(), square)` and return the result unchanged.
* Validation (null check, empty square, opponent piece) is delegated entirely to `RulesEngine`.

---

#### `move` (`Move`) — parameter to `applyMove(Move move)`

**Valid values:**

* Non-null `Move` whose piece's color matches `currentTurn`, deemed legal by `RulesEngine`.

**Invalid values:**

* `null`.
* Move whose piece's color does not match `currentTurn`.
* Move deemed illegal by `RulesEngine.isLegalMove`.
* Any move when `status` is `CHECKMATE` or `STALEMATE`.

**Constraints from `GameModel`'s perspective:**

`GameModel` applies the following guards in order, each resulting in an exception if violated:

1. `null` check — `IllegalArgumentException`.
2. Terminal status check — `IllegalStateException` if `status` is `CHECKMATE` or `STALEMATE`.
3. Wrong-colour check — `IllegalArgumentException` if `move.getPiece().getColor() != currentTurn`.
4. Legality check — `IllegalArgumentException` if `rulesEngine.isLegalMove(snapshot(), move)` returns `false`.

On success, `GameModel` performs: `board.movePiece(from, to)`, appends to `moveHistory`, flips `currentTurn`, calls `rulesEngine.getGameStatus(snapshot())` and stores the result in `status`.

---

#### `color` (`Color`) — parameter to `getCapturedPieces(Color color)`

Identifies whose captured-piece list to return.

**Convention:** `Color.WHITE` returns the pieces White has captured (Black
pieces removed from the board); `Color.BLACK` returns the pieces Black has
captured (White pieces removed from the board).

**Valid values:**
* `Color.WHITE`
* `Color.BLACK`

**Invalid values:**
* `null` → `IllegalArgumentException`

**`capturedByWhite` / `capturedByBlack` — internal `List<Piece>` fields:**

Two separate lists, initialised to empty `ArrayList` instances at
construction. Populated by `applyMove` when `move.getCapturedPiece()` is
non-null: if the moving piece's colour is `WHITE`, the captured piece is
appended to `capturedByWhite`; if `BLACK`, to `capturedByBlack`.

**Constraints:**
* Both lists must be empty immediately after construction.
* Each successful `applyMove` with a non-null `capturedPiece` appends exactly one entry to the correct list.
* A move with `capturedPiece == null` must not append anything to either list.
* A rejected `applyMove` must not modify either list.

**Boundary values:**
* 0 captures (either side): both lists empty at construction.
* 1 White capture: `getCapturedPieces(WHITE)` has 1 entry; `getCapturedPieces(BLACK)` empty.
* Non-capturing move: neither list grows.
* Rejected move: neither list changes.
* Multiple captures: list accumulates in chronological order.
* `null` colour: `IllegalArgumentException`.

---

### Boundary Values Summary

#### Player Boundaries

* Both parameters non-null with correct and distinct colours → valid.
* Either parameter `null` → `IllegalArgumentException`.
* Either parameter with wrong colour → `IllegalArgumentException`.

#### Board Placement Boundaries

* Exactly 32 `placePiece` calls during construction, in the fixed standard-position order.

#### Turn Order Boundaries

* Initial `currentTurn = WHITE`; wrong-colour move rejected immediately.
* Flips on every successful `applyMove`; rejected move leaves turn unchanged.

#### Move History Boundaries

* Empty at construction; each successful `applyMove` appends exactly one entry.

#### Game Status Boundaries

* Initial `status = ONGOING`.
* Updated to whatever `rulesEngine.getGameStatus` returns after each legal move.
* Terminal statuses lock `applyMove` permanently.

#### Captured Pieces Boundaries

* Both lists empty at construction.
* Appended only on successful `applyMove` with non-null `capturedPiece`.
* Rejected moves leave both lists unchanged.
* Accumulates in chronological order.
* `null` colour argument → `IllegalArgumentException`.

---

## Step 4: Test Cases

### Method under test: `GameModel.newGame(Player white, Player black)`

#### TC1: Valid Construction Returns Non-Null Model With ONGOING Status

* **State of the system**: `white` is a non-null `Player` with `color = WHITE`; `black` is a non-null `Player` with `color = BLACK`.
* **Expected output**: Returns a non-null `GameModel`. `getStatus()` returns `GameStatus.ONGOING`.
* **Implemented at**: `newGame_validPlayers_returnsNonNullModelWithOngoingStatus`

#### TC2: Null White Player

* **State of the system**: `white = null`, `black` is a valid `Player`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `newGame_nullWhitePlayer_throwsException`

#### TC3: Null Black Player

* **State of the system**: `white` is a valid `Player`, `black = null`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `newGame_nullBlackPlayer_throwsException`

#### TC4: Both Players Null

* **State of the system**: `white = null`, `black = null`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `newGame_bothPlayersNull_throwsException`

#### TC5: White Player Has Wrong Color

* **State of the system**: `white` is a non-null `Player` with `color = BLACK`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `newGame_whitePlayerHasBlackColor_throwsException`

#### TC6: Black Player Has Wrong Color

* **State of the system**: `black` is a non-null `Player` with `color = WHITE`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `newGame_blackPlayerHasWhiteColor_throwsException`

#### TC7: Initial Status Is ONGOING

* **State of the system**: Mocked `Board` and `RulesEngine` injected via package-private constructor.
* **Expected output**: `getStatus()` returns `GameStatus.ONGOING` with no calls to either collaborator.
* **Implemented at**: `newGame_initialStatus_isOngoing`

#### TC8: White Pawns Placed On Rank 2

* **State of the system**: Mocked `Board` injected. Constructor is invoked via `new GameModel(board, engine)`, triggering `placeStartingPieces()`.
* **Expected output**: `board.placePiece(whitePawn, sq)` is called exactly once for each file `'a'`–`'h'` on rank 2.
* **Implemented at**: `newGame_whitePawnsPlacedOnRank2`

#### TC9: Black Pawns Placed On Rank 7

* **State of the system**: Mocked `Board` injected.
* **Expected output**: `board.placePiece(blackPawn, sq)` is called exactly once for each file `'a'`–`'h'` on rank 7.
* **Implemented at**: `newGame_blackPawnsPlacedOnRank7`

#### TC10: White Back Rank Placed On Rank 1 In Correct Order

* **State of the system**: Mocked `Board` injected.
* **Expected output**: `board.placePiece` is called with White Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook on files `'a'`–`'h'` rank 1, in that order.
* **Implemented at**: `newGame_whiteBackRankPlacedOnRank1`

#### TC11: Black Back Rank Placed On Rank 8 In Correct Order

* **State of the system**: Mocked `Board` injected.
* **Expected output**: `board.placePiece` is called with Black Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook on files `'a'`–`'h'` rank 8, in that order.
* **Implemented at**: `newGame_blackBackRankPlacedOnRank8`

#### TC12: Exactly 32 Pieces Placed In Total

* **State of the system**: Mocked `Board` injected with a counter on `placePiece`.
* **Expected output**: `board.placePiece` is called exactly 32 times — no more, no fewer.
* **Implemented at**: `newGame_exactly32PiecesPlaced`

#### TC13: Initial Turn Is WHITE — Wrong-Colour Move Is Rejected

* **State of the system**: `GameModel` constructed with mocked collaborators. A mocked `Move` whose piece's color is `BLACK` is submitted.
* **Expected output**: Throws `IllegalArgumentException`. No call to `board` or `rulesEngine` is made.
* **Implemented at**: `newGame_initialTurn_isWhite_wrongColourMoveRejected`

---

### Method under test: `GameModel.getLegalMoves(Square square)`

#### TC14: Delegates To RulesEngine And Returns Its Result Unchanged

* **State of the system**: Mocked `RulesEngine` injected. `rulesEngine.getLegalMoves(snapshot(), square)` is stubbed to return a specific list.
* **Expected output**: `getLegalMoves` returns exactly the same list reference the engine returned. No other logic applied.
* **Implemented at**: `getLegalMoves_delegatesToRulesEngine_returnsEngineResult`

---

### Method under test: `GameModel.applyMove(Move move)`

#### TC15: Null Move

* **State of the system**: Mocked collaborators. `applyMove(null)` called.
* **Expected output**: Throws `IllegalArgumentException`. No call to `board` or `rulesEngine`.
* **Implemented at**: `applyMove_nullMove_throwsException`

#### TC16: Wrong-Colour Move Is Rejected; Turn Does Not Change

* **State of the system**: `currentTurn = WHITE`. Mocked `Move` whose piece's color is `BLACK`.
* **Expected output**: Throws `IllegalArgumentException`. A second wrong-colour move is also rejected, proving turn is unchanged.
* **Implemented at**: `applyMove_wrongColorMove_throwsAndTurnUnchanged`

#### TC17: Illegal Move (Engine Returns False) Is Rejected

* **State of the system**: `currentTurn = WHITE`. Mocked `Move` with correct colour. `rulesEngine.isLegalMove` stubbed to return `false`.
* **Expected output**: Throws `IllegalArgumentException`. `board.movePiece` is never called.
* **Implemented at**: `applyMove_illegalMove_throwsException`

#### TC18: Legal Move Calls `board.movePiece`

* **State of the system**: `currentTurn = WHITE`. Mocked `Move` with correct colour. Engine returns `true` for `isLegalMove` and `ONGOING` for `getGameStatus`.
* **Expected output**: `board.movePiece(from, to)` is called exactly once. No exception.
* **Implemented at**: `applyMove_legalMove_callsBoardMovePiece`

#### TC19: Legal Move Appends To Move History

* **State of the system**: `currentTurn = WHITE`. Legal move applied with mocked collaborators.
* **Expected output**: `moveHistory` is empty before the call and contains exactly the applied `Move` instance after.
* **Implemented at**: `applyMove_legalMove_appendsToMoveHistory`

#### TC20: Legal Move Flips `currentTurn` To BLACK

* **State of the system**: `currentTurn = WHITE`. Legal White move applied.
* **Expected output**: A subsequent White move is rejected, proving `currentTurn` flipped to `BLACK`.
* **Implemented at**: `applyMove_legalMove_flipsTurnToBlack`

#### TC21: Legal Move Updates Status From Engine

* **State of the system**: `currentTurn = WHITE`. Legal move applied. `rulesEngine.getGameStatus` stubbed to return `CHECK`.
* **Expected output**: `model.getStatus()` returns `GameStatus.CHECK` after the move.
* **Implemented at**: `applyMove_legalMove_updatesStatusFromEngine`

#### TC22: Promotion Move Places Promotion Piece

* **State of the system**: `currentTurn = WHITE`. A legal move is applied where `move.getPromotionPiece()` returns a non-null promotion piece.
* **Expected output**: `board.movePiece(from, to)` is called, then `board.placePiece(promotionPiece, to)` is called.
* **Implemented at**: `applyMove_promotionMove_placesPromotionPiece`

#### TC23: White En Passant Removes Captured Pawn

* **State of the system**: `currentTurn = WHITE`. A legal move is applied where `move.isEnPassant()` returns `true`, and the destination square is on rank `6`.
* **Expected output**: `board.getSquare(move.getTo().getFile(), move.getTo().getRank() - 1)` is called, and that square's occupant is set to `null`.
* **Implemented at**: `applyMove_whiteEnPassant_removesCapturedPawn`

#### TC24: Black En Passant Removes Captured Pawn

* **State of the system**: `currentTurn = BLACK`. A prior legal White move has flipped the turn. A legal Black move is applied where `move.isEnPassant()` returns `true`, and the destination square is on rank `3`.
* **Expected output**: `board.getSquare(move.getTo().getFile(), move.getTo().getRank() + 1)` is called, and that square's occupant is set to `null`.
* **Implemented at**: `applyMove_blackEnPassant_removesCapturedPawn`

#### TC25: White Kingside Castle Moves Rook

* **State of the system**: `currentTurn = WHITE`. A legal castling move is applied where `move.isCastle()` returns `true` and `move.getTo().getFile() == 'g'`.
* **Expected output**: After the King move, `board.movePiece()` is called to move the rook from `h1` to `f1`.
* **Implemented at**: `applyMove_whiteKingsideCastle_movesRook`

#### TC26: Black Queenside Castle Moves Rook

* **State of the system**: `currentTurn = BLACK`. A prior legal White move has flipped the turn. A legal castling move is applied where `move.isCastle()` returns `true` and `move.getTo().getFile() == 'c'`.
* **Expected output**: After the King move, `board.movePiece()` is called to move the rook from `a8` to `d8`.
* **Implemented at**: `applyMove_blackQueensideCastle_movesRook`

#### TC27: Move After CHECKMATE Is Rejected

* **State of the system**: Model driven to `CHECKMATE` status via one legal move with engine stub. A subsequent move is submitted.
* **Expected output**: Throws `IllegalStateException`. Engine and board are not consulted for the rejected move.
* **Implemented at**: `applyMove_afterCheckmate_throwsIllegalStateException`

#### TC28: Move After STALEMATE Is Rejected

* **State of the system**: Model driven to `STALEMATE` status via one legal move with engine stub. A subsequent move is submitted.
* **Expected output**: Throws `IllegalStateException`. Engine and board are not consulted for the rejected move.
* **Implemented at**: `applyMove_afterStalemate_throwsIllegalStateException`

---

### Method under test: `GameModel.getStatus()`

#### TC29: Returns ONGOING At Construction

* **State of the system**: `GameModel` constructed with mocked collaborators. No moves applied.
* **Expected output**: `getStatus()` returns `GameStatus.ONGOING` with no calls to any collaborator.
* **Implemented at**: `getStatus_afterConstruction_returnsOngoing`

#### TC30: Returns And Retains Terminal Status

* **State of the system**: Engine stubbed to return `CHECKMATE` after one legal move. `getStatus()` called twice after the move.
* **Expected output**: Both calls return `GameStatus.CHECKMATE`, confirming the value is stored and not recomputed.
* **Implemented at**: `getStatus_afterTerminalMove_remainsTerminal`

---

### Method under test: `GameModel.getCapturedPieces(Color color)`

#### TC31: Both Lists Empty At Construction

* **State of the system**: `GameModel` constructed with mocked collaborators. No moves applied.
* **Expected output**: `getCapturedPieces(Color.WHITE)` returns an empty list. `getCapturedPieces(Color.BLACK)` returns an empty list.
* **Implemented at**: `getCapturedPieces_atConstruction_bothListsEmpty`

#### TC32: Null Color Throws IllegalArgumentException

* **State of the system**: `GameModel` constructed with mocked collaborators.
* **Expected output**: `getCapturedPieces(null)` throws `IllegalArgumentException`. No call to any collaborator.
* **Implemented at**: `getCapturedPieces_nullColor_throwsException`

#### TC33: White Capture Appends To White's List Only

* **State of the system**: A legal White move is applied. The mocked `Move` returns a non-null `Piece` from `getCapturedPiece()`. The mocked engine accepts the move.
* **Expected output**: `getCapturedPieces(Color.WHITE)` contains exactly that one captured piece. `getCapturedPieces(Color.BLACK)` remains empty.
* **Implemented at**: `getCapturedPieces_whiteCaptures_addsToWhiteList`

#### TC34: Black Capture Appends To Black's List Only

* **State of the system**: A legal Black move is applied (turn flipped to `BLACK` via a prior White move). The mocked `Move` returns a non-null `Piece` from `getCapturedPiece()`.
* **Expected output**: `getCapturedPieces(Color.BLACK)` contains exactly that one captured piece. `getCapturedPieces(Color.WHITE)` is unchanged.
* **Implemented at**: `getCapturedPieces_blackCaptures_addsToBlackList`

#### TC35: Non-Capturing Move Does Not Append To Either List

* **State of the system**: A legal White move is applied. The mocked `Move` returns `null` from `getCapturedPiece()`.
* **Expected output**: Both `getCapturedPieces(Color.WHITE)` and `getCapturedPieces(Color.BLACK)` remain empty after the move.
* **Implemented at**: `getCapturedPieces_nonCapturingMove_listsUnchanged`

#### TC36: Rejected Move Does Not Modify Either List

* **State of the system**: A White move is submitted but the mocked engine returns `false` for `isLegalMove`. `applyMove` throws `IllegalArgumentException`.
* **Expected output**: Both lists remain empty after the rejection.
* **Implemented at**: `getCapturedPieces_rejectedMove_listsUnchanged`

#### TC37: Multiple Captures Accumulate In Chronological Order

* **State of the system**: Two separate legal White capturing moves are applied (with a Black non-capturing move in between to alternate turns). Each White move returns a distinct `Piece` from `getCapturedPiece()`.
* **Expected output**: `getCapturedPieces(Color.WHITE)` contains both pieces in the order they were captured. `getCapturedPieces(Color.BLACK)` remains empty throughout.
* **Implemented at**: `getCapturedPieces_multipleCaptures_accumulatesInOrder`