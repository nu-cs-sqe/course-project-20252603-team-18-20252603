# GameController BVA

## Intermediate Analysis (Steps 1–3)

### Architectural Role of GameController

`GameController` is the single orchestration hub of the entire chess application. It sits at the center of the MVC triangle, owning one instance of every other major component: the `GameModel` (the entire game state and rules), `BoardView`, `NotificationView`, `PromotionView`, and `CapturedPiecesView`. Nothing in the View layer communicates with the Model directly — every user interaction flows into the controller, and every visual update flows back out from it.

The controller's private field `selectedSquare` is the key piece of transient UI state it owns. It tracks whether the player has already clicked a piece (selection phase) or is now clicking a destination (execution phase). All of `GameController`'s logic is fundamentally a two-phase state machine driven by that single field.

The overall flow of a single turn is:

```
BoardView emits onSquareClick
  → onSquareClick() [dispatcher]
      → if nothing selected: handlePieceSelection()
      → if piece already selected: handleMoveExecution()
          → if move causes promotion: handlePromotion()
  → refreshViews() [called after any state change]
```

---

**Input Domains & Variables:**

* `selectedSquare` (Square — internal state): The controller's core state variable. Drives the dispatcher logic in `onSquareClick()`.
    * Valid values: `null` (no piece selected; next click is a selection attempt); a non-null `Square` occupied by a current-player piece whose legal moves have already been computed and highlighted.
    * Invalid values: A `Square` occupied by an opponent piece (must never be set by `handlePieceSelection()`); a non-null reference pointing to a square that has since been vacated (stale reference after a move is applied).
    * Boundary: The transition from `null` → non-null (piece selected) and non-null → `null` (move executed or deselection) are the two critical state transitions the dispatcher depends on.

* `square` / `target` (Square — parameter to click handlers): The square emitted by `BoardView.onSquareClick` representing the player's click.
    * Valid values: Any non-null `Square` with file `'a'`–`'h'` and rank `1`–`8`.
    * Invalid values: `null` (should not be emitted by a well-behaved `BoardView`, but must be handled defensively); a square with out-of-bounds coordinates (structurally prevented by `Square.create()`, but the controller must not assume this).
    * Boundary: The same square as `selectedSquare` (deselection / re-click boundary); a corner square (`a1`, `a8`, `h1`, `h8`) representing the extremes of valid board coordinates.

* `GameStatus` (returned by `model.getStatus()`): Drives the notification and input-locking behavior inside `refreshViews()`.
    * Valid values: `ONGOING`, `CHECK`, `CHECKMATE`, `STALEMATE`, `RESIGNED`.
    * Invalid values: `null` (would indicate a model-layer bug; the controller must not crash).
    * Boundary: The transition from `ONGOING` → `CHECK` (check indicator must appear); `CHECK` → `ONGOING` (check indicator must be cleared); `CHECK` or `ONGOING` → `CHECKMATE`, `STALEMATE`, or `RESIGNED` (input must be permanently disabled).

* Resign action (event emitted by the resign button): Requests that the player whose turn it currently is concede the game.
    * Valid state: `model.getStatus()` is `ONGOING` or `CHECK`.
    * Invalid state: The game is already terminal (`CHECKMATE`, `STALEMATE`, or `RESIGNED`), or another controller operation has temporarily locked input.
    * Boundary: Resignation on White's turn makes Black the winner; resignation on Black's turn makes White the winner. These winner boundaries are defined and tested at the model layer in `gameModel.md` TC38 and TC39.

* `promotionPiece` (Piece — resolved from `PromotionView.show()`): The player's chosen promotion piece, returned asynchronously.
    * Valid values: An instantiated `Piece` of type Queen, Rook, Bishop, or Knight, with the color matching the current player.
    * Invalid values: `null` (player dismissed without choosing — controller must re-prompt and keep `PromotionView` open); a `Piece` of type King or Pawn (invalid promotion target); a `Piece` of the wrong color.
    * Boundary: The exact moment of promise resolution separates the suspended move state from the finalized move state; the board is in a partially-committed intermediate state while `PromotionView` is open.

* `legalMoves` (List\<Square\> — returned by `model.getLegalMoves()`): The set of legal destination squares computed at selection time and used to validate the subsequent click in `handleMoveExecution()`.
    * Valid values: A non-null list of zero or more valid `Square` objects.
    * Invalid values: `null` (the controller must treat this as an empty list).
    * Boundary: An empty list (piece is fully pinned or has no legal moves — selection may be accepted but no highlights appear); a list containing the `target` square (legal move); a list not containing the `target` square (illegal move, must be rejected without calling `model.applyMove()`).

---

**Boundary Values Identified:**

* `selectedSquare` State Boundaries:
    * `null` (no selection active) — next click routes to `handlePieceSelection()`.
    * Non-null (selection active) — next click routes to `handleMoveExecution()`.
    * Non-null but pointing to a now-empty square (stale reference) — structurally prevented by correct flow, but represents the most dangerous internal inconsistency if it arises.

* Click Target Boundaries:
    * Empty square clicked during selection phase — no-op; `selectedSquare` must not be set.
    * Opponent piece clicked during selection phase — rejected; `selectedSquare` must not be set.
    * Friendly piece clicked during execution phase — re-selection or rejection (implementation decision boundary).
    * Same square as `selectedSquare` clicked during either phase — deselection; `selectedSquare` cleared, highlights cleared.
    * Square outside legal moves list clicked during execution phase — rejected move; `model.applyMove()` must not be called.

* Game Status Boundaries:
    * `ONGOING` → `CHECK` — check indicator and notification must appear.
    * `CHECK` → `ONGOING` (check resolved) — check indicator must be cleared.
    * `CHECK` → `CHECKMATE` — game locks; all input disabled.
    * `ONGOING` → `STALEMATE` — game locks; no check indicator shown.
    * `ONGOING` or `CHECK` → `RESIGNED` — the opponent is shown as the winner and all game input is disabled.
    * Terminal state → resignation request — no additional state change; the model rejection boundary is covered by `gameModel.md` TC41.

* Promotion Boundaries:
    * Pawn on rank `7` (White) or rank `2` (Black) moving to the final rank — promotion flow triggers.
    * Pawn not on pre-promotion rank — promotion flow must not trigger.
    * `PromotionView` resolved with valid piece — move finalizes.
    * `PromotionView` dismissed without selection — controller blocks and re-prompts; turn does not advance.

---

**Method Responsibilities:**

* `init()`: Bootstraps the application. Creates the `GameModel` via `GameModel.newGame()`, wires `BoardView.onSquareClick` to `onSquareClick()`, and calls `refreshViews()` to render the initial board state. This is the entry point of the application; nothing works until it runs. The primary boundary risk is double-registering the event listener if `init()` is ever called more than once.

* `onSquareClick(Square square)`: Acts as the central dispatcher for all player input. Reads `selectedSquare` to determine whether the player is in the selection phase or execution phase, then delegates to `handlePieceSelection()` or `handleMoveExecution()` accordingly. As the entry point for every user action, any misclassification here (e.g., failing to handle the deselect case) corrupts the entire interaction model.

* `handlePieceSelection(Square square)`: Handles the first click of a two-click move sequence. Validates that the clicked square is occupied by a current-player piece, calls `model.getLegalMoves(square)`, sets `selectedSquare`, and calls `boardView.highlightSquares()` with the result. Enforces the "you can only select your own pieces" rule at the UI layer before any `Move` is constructed.

* `handleMoveExecution(Square target)`: Handles the second click of a two-click move sequence. Validates that `target` is within the legal moves list, constructs a `Move`, sets any applicable special flags (`isEnPassant`, `isCastle`), calls `model.applyMove()`, clears `selectedSquare` and highlights, and calls `refreshViews()`. If the move is a promotion, delegates to `handlePromotion()` before finalizing. This is the most consequential method — the only place where game state actually changes and all five special move types must be correctly handled.

* `handlePromotion(Square square)`: Pauses the move flow to collect an asynchronous promotion choice. Calls `promotionView.show(color)`, awaits the resolved `Piece`, and sets it as `promotionPiece` on the pending `Move` before finalization. Uniquely, this is the only method in the controller that must handle async execution, which creates a temporarily suspended game state while `PromotionView` is open.

* `onResign()`: Handles the resign-button action. If the game is not locked, delegates the state change to `model.resign()`, clears any active selection and highlights, and calls `refreshViews()`. The model owns winner calculation and terminal-state validation, as covered by `gameModel.md` TC38–TC42.

* `refreshViews()`: Synchronizes all four views with the current model state after any state-changing operation. Calls `boardView.render()`, `capturedView.update()`, `notificationView` based on `model.getStatus()`, and clears or sets check indicators as appropriate. By centralizing all view updates here, the design avoids scattered partial-update calls across the other methods. The primary risk is missing a branch — particularly forgetting to clear the check indicator when check is resolved — which produces a persistent visual bug.

---

## Step 4: Test Cases

### Method under test: `GameController.init()`

- **TC1: Valid Initialization**
    - **State of the system**: Two valid `Player` objects are available. All views are non-null. `GameModel.newGame()` succeeds.
    - **Expected output**: `boardView.render()` is called with the starting board. `notificationView.showTurn(WHITE)` is called. The `onSquareClick` listener is registered on `boardView`. `selectedSquare` is `null`.
    - **Implemented at**: `init_validPlayers_rendersStartingBoard`

- **TC2: Game Model Initialization Failure**
    - **State of the system**: `GameModel.newGame()` throws due to an invalid player configuration (e.g., both players assigned the same color).
    - **Expected output**: The exception propagates; the application does not reach a partially-initialized state with a wired event listener.
    - **Implemented at**: `init_invalidPlayers_throwsException`

---

### Method under test: `GameController.onSquareClick(Square square)`

- **TC3: Click With No Selection Active — Delegates to handlePieceSelection**
    - **State of the system**: `selectedSquare == null`. Player clicks a square occupied by a current-player piece.
    - **Expected output**: `handlePieceSelection()` is invoked with the clicked square. `selectedSquare` becomes non-null after the call.
    - **Implemented at**: `onSquareClick_noSelectionActive_delegatesToPieceSelection`

- **TC4: Click With Selection Active — Delegates to handleMoveExecution**
    - **State of the system**: `selectedSquare != null`. Player clicks a destination square.
    - **Expected output**: `handleMoveExecution()` is invoked with the clicked square.
    - **Implemented at**: `onSquareClick_selectionActive_delegatesToMoveExecution`

- **TC5: Null Square Click**
    - **State of the system**: `BoardView` emits a `null` square (defensive boundary case).
    - **Expected output**: Method returns without throwing or corrupting `selectedSquare`.
    - **Implemented at**: `onSquareClick_nullSquare_noOp`

---

### Method under test: `GameController.handlePieceSelection(Square square)`

- **TC6: Valid Piece Selection — Highlights Legal Moves**
    - **State of the system**: The clicked square is occupied by a current-player piece with at least one legal move.
    - **Expected output**: `model.getLegalMoves(square)` is called. `boardView.highlightSquares()` is called with the returned list. `selectedSquare` is set to `square`.
    - **Implemented at**: `handlePieceSelection_validPiece_highlightsLegalMoves`

- **TC7: Empty Square Selected**
    - **State of the system**: The clicked square has no occupant (`square.isEmpty() == true`).
    - **Expected output**: `selectedSquare` remains `null`. No highlights are applied. `model.getLegalMoves()` is not called.
    - **Implemented at**: `handlePieceSelection_emptySquare_noSelectionMade`

- **TC8: Opponent Piece Selected**
    - **State of the system**: The clicked square contains a piece whose color does not match `currentTurn`.
    - **Expected output**: `selectedSquare` remains `null`. No highlights are applied. `model.getLegalMoves()` is not called.
    - **Implemented at**: `handlePieceSelection_opponentPiece_selectionRejected`

- **TC9: Valid Piece With Zero Legal Moves Selected (Pinned Piece)**
    - **State of the system**: The clicked square contains a current-player piece, but `model.getLegalMoves()` returns an empty list (e.g., piece is fully pinned).
    - **Expected output**: `boardView.highlightSquares()` is called with an empty list. No crash occurs. `selectedSquare` is set or remains `null` per implementation decision.
    - **Implemented at**: `handlePieceSelection_piecePinned_zeroLegalMoves`

- **TC10: Already-Selected Square Re-clicked (Deselection)**
    - **State of the system**: `selectedSquare != null`. Player clicks the already-selected square again.
    - **Expected output**: `selectedSquare` is cleared to `null`. `boardView.clearHighlights()` is called. `model.getLegalMoves()` is not called again.
    - **Implemented at**: `handlePieceSelection_reClickSelectedSquare_deselects`

---

### Method under test: `GameController.handleMoveExecution(Square target)`

- **TC11: Valid Standard Move Executed**
    - **State of the system**: `selectedSquare` is set. `target` is within the legal moves list. No special flags apply. `model.getStatus()` returns `ONGOING` after the move.
    - **Expected output**: `model.applyMove()` is called. `selectedSquare` is cleared to `null`. `boardView.clearHighlights()` is called. `refreshViews()` is called.
    - **Implemented at**: `handleMoveExecution_validStandardMove_appliesMove`

- **TC12: Target Not In Legal Moves List**
    - **State of the system**: `selectedSquare` is set. `target` is a valid square that was not in the legal moves list returned during selection.
    - **Expected output**: `model.applyMove()` is not called. `selectedSquare` is not corrupted. No state change occurs.
    - **Implemented at**: `handleMoveExecution_illegalTarget_moveRejected`

- **TC13: Target Is Same Square As Selected Square (Deselection During Execution Phase)**
    - **State of the system**: `selectedSquare` is set. Player clicks the same square again.
    - **Expected output**: Treated as a deselection. `selectedSquare` is cleared to `null`. Highlights are cleared. `model.applyMove()` is not called.
    - **Implemented at**: `handleMoveExecution_targetSameAsSelected_deselects`

- **TC14: Move Causes Check**
    - **State of the system**: A legal move is executed. After `model.applyMove()`, `model.getStatus()` returns `CHECK`.
    - **Expected output**: `refreshViews()` calls `notificationView.showCheck()` and `boardView.showCheckIndicator()` on the opponent King's square.
    - **Implemented at**: `handleMoveExecution_moveCausesCheck_showsCheckIndicator`

- **TC15: Move Causes Checkmate**
    - **State of the system**: A legal move is executed. After `model.applyMove()`, `model.getStatus()` returns `CHECKMATE`.
    - **Expected output**: `refreshViews()` calls `notificationView.showCheckmate()` with the winner's color. All further board input is disabled.
    - **Implemented at**: `handleMoveExecution_moveCausesCheckmate_locksGame`

- **TC16: Move Is A Capture**
    - **State of the system**: `target` is occupied by an opponent piece and is within the legal moves list.
    - **Expected output**: `model.applyMove()` is called. `refreshViews()` causes `capturedView.update()` to reflect the newly captured piece. The captured piece is absent from the rendered board.
    - **Implemented at**: `handleMoveExecution_captureMove_updatesCapturedPieces`

- **TC17: Move Is A Pawn Promotion**
    - **State of the system**: `selectedSquare` contains a White `Pawn` on rank `7` (or Black `Pawn` on rank `2`). `target` is on the final rank and within the legal moves list.
    - **Expected output**: `handlePromotion()` is invoked before the move is finalized. `model.applyMove()` is called only after a valid `promotionPiece` is received. The promoted piece appears on the board in `refreshViews()`.
    - **Implemented at**: `handleMoveExecution_pawnPromotion_invokesHandlePromotion`

- **TC18: Move Is Castling**
    - **State of the system**: `selectedSquare` is the King's square. `target` is two squares horizontally away. Castling conditions are met.
    - **Expected output**: `model.applyMove()` is called with a `Move` where `isCastle == true`. After `refreshViews()`, both the King and Rook appear in their post-castle positions.
    - **Implemented at**: `handleMoveExecution_castlingMove_appliesCastleMove`

- **TC19: Move Is En Passant**
    - **State of the system**: `selectedSquare` contains a `Pawn`. `target` is the en passant destination diagonal. The en passant target matches the current `GameState`.
    - **Expected output**: `model.applyMove()` is called with a `Move` where `isEnPassant == true`. The opponent pawn on the adjacent square (not `target`) is removed from the board. `capturedView` is updated.
    - **Implemented at**: `handleMoveExecution_enPassant_capturesAdjacentPawn`

- **TC20: Target Is A Friendly Piece (Re-selection Attempt During Execution Phase)**
    - **State of the system**: `selectedSquare != null`. Player clicks a square occupied by another friendly piece.
    - **Expected output**: Either treated as a re-selection of the new piece (delegates to `handlePieceSelection()`) or rejected silently. `model.applyMove()` is not called in either case.
    - **Implemented at**: `handleMoveExecution_targetIsFriendlyPiece_reSelectsOrRejects`

---

### Method under test: `GameController.handlePromotion(Square square)`

- **TC21: Valid Promotion — Queen Selected**
    - **State of the system**: `PromotionView.show()` resolves with a `Queen` of the current player's color.
    - **Expected output**: `Move.setPromotionPiece()` is called with the `Queen`. `promotionView.hide()` is called. The move is finalized via `model.applyMove()`.
    - **Implemented at**: `handlePromotion_queenSelected_setsPromotionPiece`

- **TC22: Valid Promotion — Knight Selected**
    - **State of the system**: `PromotionView.show()` resolves with a `Knight` of the current player's color.
    - **Expected output**: `Move.setPromotionPiece()` is called with the `Knight`. The move is finalized via `model.applyMove()`.
    - **Implemented at**: `handlePromotion_knightSelected_setsPromotionPiece`

- **TC23: Player Attempts to Dismiss PromotionView Without Selecting**
    - **State of the system**: The player clicks outside the `PromotionView` menu without selecting a piece (per Use Case 6, alternate flow 3.a).
    - **Expected output**: `PromotionView` remains open. The turn does not advance. `model.applyMove()` is not called. The controller re-prompts the player.
    - **Implemented at**: `handlePromotion_dismissedWithoutSelection_remainsOpen`

- **TC24: Promotion Results In Checkmate**
    - **State of the system**: `PromotionView.show()` resolves with a valid `Piece`. After the promotion move is finalized, `model.getStatus()` returns `CHECKMATE`.
    - **Expected output**: `refreshViews()` calls `notificationView.showCheckmate()`. All board input is disabled.
    - **Implemented at**: `handlePromotion_promotionCausesCheckmate_locksGame`

- **TC25: Promotion Results In Check**
    - **State of the system**: `PromotionView.show()` resolves with a valid `Piece`. After finalization, `model.getStatus()` returns `CHECK`.
    - **Expected output**: `refreshViews()` calls `notificationView.showCheck()` and `boardView.showCheckIndicator()` on the opponent King's square.
    - **Implemented at**: `handlePromotion_promotionCausesCheck_showsCheck`

---

### Method under test: `GameController.refreshViews()`

- **TC26: Refresh After Standard Move — Status ONGOING**
    - **State of the system**: `model.getStatus()` returns `ONGOING`. No check is active.
    - **Expected output**: `boardView.render()` is called. `boardView.clearCheckIndicator()` is called. `notificationView.showTurn()` is called with the new `currentTurn` color. `capturedView.update()` is called.
    - **Implemented at**: `refreshViews_ongoingStatus_rendersNormally`

- **TC27: Refresh After Move Causing Check**
    - **State of the system**: `model.getStatus()` returns `CHECK`.
    - **Expected output**: `boardView.showCheckIndicator()` is called on the opponent King's square. `notificationView.showCheck()` is called with the color of the player now in check.
    - **Implemented at**: `refreshViews_checkStatus_showsCheckIndicator`

- **TC28: Refresh After Checkmate**
    - **State of the system**: `model.getStatus()` returns `CHECKMATE`.
    - **Expected output**: `notificationView.showCheckmate()` is called with the winner's color. Board input is permanently disabled. No further `onSquareClick` events are processed.
    - **Implemented at**: `refreshViews_checkmateStatus_locksGame`

- **TC29: Refresh After Stalemate**
    - **State of the system**: `model.getStatus()` returns `STALEMATE`.
    - **Expected output**: A stalemate notification is displayed. Board input is permanently disabled. `boardView.showCheckIndicator()` is not called.
    - **Implemented at**: `refreshViews_stalemateStatus_noCheckIndicatorInputLocked`

- **TC30: Refresh After Check Is Resolved**
    - **State of the system**: The previous game status was `CHECK`. The current `model.getStatus()` returns `ONGOING` (the defending player moved out of check).
    - **Expected output**: `boardView.clearCheckIndicator()` is called. `notificationView` does not display a check notification. Normal turn notification is shown.
    - **Implemented at**: `refreshViews_checkResolved_clearsCheckIndicator`

- **TC31: Refresh At Game Start (Called From init())**
    - **State of the system**: No moves have been made. `model.getStatus()` returns `ONGOING`. Captured pieces lists are empty.
    - **Expected output**: `boardView.render()` is called with the standard starting board. `capturedView.update()` is called with two empty lists. `notificationView.showTurn(WHITE)` is called. No check indicator is shown.
    - **Implemented at**: `refreshViews_calledFromInit_rendersStartingState`

---

### Method under test: `GameController.onResign()`

- **TC32: White Resigns At Game Start** (:white_check_mark:)
    - **State of the system**: The game is unlocked, `model.getStatus()` is `ONGOING`, and `model.getCurrentTurn()` is `WHITE`.
    - **Expected output**: `model.resign()` is called once, the model enters `RESIGNED`, Black is recorded as the winner, any selection and highlights are cleared, and `refreshViews()` is called. Model winner behavior is covered by `gameModel.md` TC38.
    - **Implemented at**: `onResign_whiteResignsAtGameStart_resignsAndRefreshesViews`

- **TC33: Black Resigns On Black's Turn** (:white_check_mark:)
    - **State of the system**: White has completed a legal move, the game is unlocked, and `model.getCurrentTurn()` is `BLACK`.
    - **Expected output**: `model.resign()` is called once, the model enters `RESIGNED`, White is recorded as the winner, and `refreshViews()` is called. Model winner behavior is covered by `gameModel.md` TC39.
    - **Implemented at**: `onResign_blackResignsOnBlackTurn_resignsAndRefreshesViews`

- **TC34: Resign With A Piece Selected** (:white_check_mark:)
    - **State of the system**: The game is ongoing and `selectedSquare` and `selectedLegalMoves` are non-null when the resign action occurs.
    - **Expected output**: The resignation is applied, `selectedSquare` and `selectedLegalMoves` are cleared, and `boardView.clearHighlights()` is called so stale move highlights are not left visible.
    - **Implemented at**: `onResign_pieceSelected_clearsSelectionAndHighlights`

- **TC35: Resign While Controller Input Is Locked** (:x:)
    - **State of the system**: `gameLocked == true`, such as while promotion selection is pending or after a terminal game state.
    - **Expected output**: The request is ignored. `model.resign()` and `refreshViews()` are not called, and the current state remains unchanged.

- **TC36: Duplicate Resign Request After Resignation** (:x:)
    - **State of the system**: A previous resignation has completed and `model.getStatus()` is already `RESIGNED`.
    - **Expected output**: No second resignation is applied and the original winner remains unchanged. The model's terminal-state rejection is covered by `gameModel.md` TC41.
