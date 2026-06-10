# pawnIntegrationTests.md

## Test Cases

### Normal Pawn Moves

- **IT-PM-01: Single-step forward — empty square ahead**
    - **State of the system**: White pawn on `e2`, `e3` empty, both kings
      present and non-interfering.
    - **Expected output**: `RulesEngine.isLegalMove` returns `true`.
    - **Implemented at**: `whitePawnSingleStep_emptySquareAhead_isLegal`

- **IT-PM-02: Double-step from starting rank — both squares empty**
    - **State of the system**: White pawn on `e2`, `hasMoved == false`, `e3`
      and `e4` empty.
    - **Expected output**: `RulesEngine.isLegalMove` returns `true`.
    - **Implemented at**: `whitePawnDoubleStep_fromStartingRank_isLegal`

- **IT-PM-03: Double-step blocked by hasMoved flag**
    - **State of the system**: White pawn on `e3`, `hasMoved == true`, `e4`
      and `e5` empty.
    - **Expected output**: `RulesEngine.isLegalMove` returns `false`.
    - **Implemented at**: `whitePawnDoubleStep_afterHavingMoved_isIllegal`

- **IT-PM-04: Double-step unavailable after pawn has moved via applyMove**
    - **State of the system**: White pawn advanced from `e2` to `e3` through
      `GameModel.applyMove`. Black plays a filler king move. It is now white's
      turn with the pawn on `e3`.
    - **Expected output**: `GameModel.getLegalMoves` does not include `e5`.
    - **Implemented at**:
      `whitePawn_afterMovingViaApplyMove_cannotDoubleStep`

---

### Pawn Captures

- **IT-PC-01: Diagonal capture — opponent present**
    - **State of the system**: White pawn on `e4` (`hasMoved == true`), black
      pawn on `f5`.
    - **Expected output**: `RulesEngine.isLegalMove` returns `true`.
    - **Implemented at**: `whitePawnDiagonalCapture_opponentPresent_isLegal`

- **IT-PC-02: Diagonal move — destination empty, no en passant**
    - **State of the system**: White pawn on `e4` (`hasMoved == true`), `f5`
      empty, no en passant target.
    - **Expected output**: `RulesEngine.isLegalMove` returns `false`.
    - **Implemented at**: `whitePawnDiagonalMove_squareEmpty_isIllegal`

- **IT-PC-03: Forward move into occupied square**
    - **State of the system**: White pawn on `e4` (`hasMoved == true`), black
      pawn on `e5`.
    - **Expected output**: `RulesEngine.isLegalMove` returns `false`.
    - **Implemented at**: `whitePawnForwardMove_squareOccupied_isIllegal`

---

### En Passant

- **IT-EP-01: White captures black en passant — board state correct**
    - **State of the system**: White pawn on `e5`, black pawn on `d5`.
      `GameState.lastMove` records black's `d7→d5` double step. `d6` is empty.
    - **Expected output**: `RulesEngine.isEnpassantLegal` returns `true`.
      After simulating `applyMove` board mutations: white pawn occupies `d6`,
      `e5` is empty, `d5` is empty.
    - **Implemented at**:
      `enPassant_whiteCapturesBlack_boardStateCorrectAfterMove`

- **IT-EP-02: Black captures white en passant — board state correct**
    - **State of the system**: Black pawn on `d4`, white pawn on `e4`.
      `GameState.lastMove` records white's `e2→e4` double step. `e3` is empty.
    - **Expected output**: `RulesEngine.isEnpassantLegal` returns `true`.
      After simulating `applyMove` board mutations: black pawn occupies `e3`,
      `d4` is empty, `e4` is empty.
    - **Implemented at**:
      `enPassant_blackCapturesWhite_boardStateCorrectAfterMove`

- **IT-EP-03: Opportunity expired — last move was not a pawn double-step**
    - **State of the system**: White pawn on `e5`, black pawn on `d5`.
      `GameState.lastMove` records a white king step (`e1→d1`), not a pawn
      double advance.
    - **Expected output**: `RulesEngine.isEnpassantLegal` returns `false`.
    - **Implemented at**: `enPassant_opportunityExpired_isIllegal`

- **IT-EP-04: En passant would expose own king**
    - **State of the system**: White king on `e5`, white pawn on `d5`, black
      pawn on `c5` (just double-stepped `c7→c5`), black rook on `a5`. Capturing
      `dxc6` would vacate `d5` and `c5`, exposing the king to the rook.
    - **Expected output**: `RulesEngine.isEnpassantLegal` returns `false`.
    - **Implemented at**: `enPassant_wouldExposeOwnKing_isIllegal`

---

### Promotion

- **IT-PR-01: Pawn reaches final rank — board state correct after promotion**
    - **State of the system**: White pawn on `e7` (`hasMoved == true`), `e8`
      empty, `promotionPiece = Queen(WHITE)`.
    - **Expected output**: `RulesEngine.isPromotionLegal` returns `true`.
      After simulating `applyMove` board mutations: `e7` is empty, `e8` is
      occupied by a white Queen.
    - **Implemented at**:
      `promotion_whitePawnReachesFinalRank_replacedByQueen`

- **IT-PR-02: promotionPiece is null on the final rank**
    - **State of the system**: White pawn on `e7`, destination `e8`,
      `promotionPiece = null`.
    - **Expected output**: `RulesEngine.isPromotionLegal` returns `false`.
    - **Implemented at**:
      `promotion_nullPromotionPiece_isPromotionLegalReturnsFalse`

- **IT-PR-03: Promotion to King is forbidden**
    - **State of the system**: White pawn on `e7`, destination `e8`,
      `promotionPiece = King(WHITE)`.
    - **Expected output**: `RulesEngine.isPromotionLegal` returns `false`.
    - **Implemented at**: `promotion_toKing_isPromotionLegalReturnsFalse`