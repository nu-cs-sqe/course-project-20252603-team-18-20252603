# kingIntegration.md

## Test Cases

### Normal King Moves

- **IT-KM-01: Single-step to empty adjacent square**
    - **State of the system**: White king on `e1` (`hasMoved == true`), all
      adjacent squares empty, no opponent threatens them. Both kings present.
    - **Expected output**: `RulesEngine.isLegalMove` returns `true`.
    - **Implemented at**: `whiteKing_singleStepToEmptySquare_isLegal`

- **IT-KM-02: Single-step captures opponent piece**
    - **State of the system**: White king on `e1` (`hasMoved == true`), black
      rook on `f1`. No other piece guards `f1`.
    - **Expected output**: `RulesEngine.isLegalMove` returns `true`; `f1`
      is occupied by the white king after the move.
    - **Implemented at**: `whiteKing_capturesOpponentPiece_isLegal`

- **IT-KM-03: Cannot capture own piece**
    - **State of the system**: White king on `e1`, white rook on `f1`.
    - **Expected output**: `RulesEngine.isLegalMove` returns `false`.
    - **Implemented at**: `whiteKing_cannotCaptureOwnPiece_isIllegal`

- **IT-KM-04: Cannot move more than one square (non-castling)**
    - **State of the system**: White king on `e1` (`hasMoved == true`), `e3`
      empty.
    - **Expected output**: `RulesEngine.isLegalMove` returns `false`.
    - **Implemented at**: `whiteKing_twoSquareNonCastle_isIllegal`

- **IT-KM-05: Cannot move into check**
    - **State of the system**: White king on `e1` (`hasMoved == true`), black
      rook on `f8` (attacks entire `f` file). King attempts `Ke1-f1`.
    - **Expected output**: `RulesEngine.isLegalMove` returns `false`.
    - **Implemented at**: `whiteKing_movesIntoCheck_isIllegal`

- **IT-KM-06: King at board corner — legal moves stay in bounds**
    - **State of the system**: White king on `a1` (`hasMoved == true`), all
      adjacent squares empty. Black king on `h8`.
    - **Expected output**: `RulesEngine.getLegalMoves` returns exactly the
      squares `a2`, `b1`, and `b2`; no out-of-bounds square is included.
    - **Implemented at**: `whiteKing_atCornerA1_legalMovesStayInBounds`

- **IT-KM-07: King move via `applyMove` sets `hasMoved` flag**
    - **State of the system**: White king on `e1` (`hasMoved == false`),
      advanced to `e2` through `GameModel.applyMove`. After the move (and a
      black filler move) white's king is on `e2`.
    - **Expected output**: `getLegalMoves` for the king's new square does not
      include castling squares (`c1`/`g1` or `c8`/`g8`), confirming
      `hasMoved == true`.
    - **Implemented at**: `whiteKing_afterApplyMove_hasMoved_noCastlingCandidates`

---

### Check Detection

- **IT-CH-01: King in check by rook — `isInCheck` returns `true`**
    - **State of the system**: White king on `e1`, black rook on `e8`, clear
      file between them.
    - **Expected output**: `RulesEngine.isInCheck(state, WHITE)` returns
      `true`.
    - **Implemented at**: `isInCheck_kingAttackedByRook_returnsTrue`

- **IT-CH-02: King in check by knight**
    - **State of the system**: White king on `e1`, black knight on `f3`.
    - **Expected output**: `RulesEngine.isInCheck(state, WHITE)` returns
      `true`.
    - **Implemented at**: `isInCheck_kingAttackedByKnight_returnsTrue`

- **IT-CH-03: King not in check — blocked sliding piece**
    - **State of the system**: White king on `e1`, white rook on `e4`, black
      rook on `e8`.
    - **Expected output**: `RulesEngine.isInCheck(state, WHITE)` returns
      `false`.
    - **Implemented at**: `isInCheck_slidingAttackBlocked_returnsFalse`

- **IT-CH-04: Move that exposes own king is illegal (discovered check)**
    - **State of the system**: White king on `e1`, white rook on `e4`, black
      rook on `e8`. White attempts to move the rook from `e4` to `a4`,
      exposing the king.
    - **Expected output**: `RulesEngine.isLegalMove` returns `false`.
    - **Implemented at**: `pinnedPiece_movementExposesKing_isIllegal`

- **IT-CH-05: `GameModel.getStatus` returns `CHECK` when king is in check with legal escapes**
    - **State of the system**: Drive the game to a position where white's king
      is in check but has at least one escape square. Use `applyMove` to reach
      this state.
    - **Expected output**: `GameModel.getStatus()` returns `GameStatus.CHECK`
      after the move that delivers check.
    - **Implemented at**: `applyMove_deliversCheck_statusIsCheck`

---

### Checkmate

- **IT-CM-01: Back-rank checkmate (Fool's Mate analog)**
    - **State of the system**: White king on `g1` (cannot move: `f1`, `h1`
      blocked by own pieces; `f2`, `g2`, `h2` covered by black queen; `g1`
      attacked). Construct the simplest two-queen or queen+rook back-rank mate
      via board placement rather than move sequence.
    - **Expected output**: `RulesEngine.isCheckmate(state, WHITE)` returns
      `true`; `GameModel.getStatus()` returns `GameStatus.CHECKMATE`.
    - **Implemented at**: `isCheckmate_backRankMate_returnsTrue`

- **IT-CM-02: King in check with one escape — not checkmate**
    - **State of the system**: White king on `e1` is attacked by a black queen
      on `e8` but can step to `d1`.
    - **Expected output**: `RulesEngine.isCheckmate(state, WHITE)` returns
      `false`.
    - **Implemented at**: `isCheckmate_kingCanEscapeToD1_returnsFalse`

- **IT-CM-03: Check resolved by blocking piece — not checkmate**
    - **State of the system**: White king on `e1` is attacked by black rook on
      `e8`; white rook on `a4` can interpose on `e4`.
    - **Expected output**: `RulesEngine.isCheckmate(state, WHITE)` returns
      `false`.
    - **Implemented at**: `isCheckmate_checkCanBeBlocked_returnsFalse`

- **IT-CM-04: Check resolved by capturing the attacker — not checkmate**
    - **State of the system**: White king on `e1`, black knight on `f3`
      (checking), white bishop on `g4` can capture the knight.
    - **Expected output**: `RulesEngine.isCheckmate(state, WHITE)` returns
      `false`.
    - **Implemented at**: `isCheckmate_attackerCanBeCaptured_returnsFalse`

- **IT-CM-05: `GameModel.getStatus` returns `CHECKMATE` and game locks**
    - **State of the system**: A checkmate position is set up via board
      placement, then `getGameStatus` is called through `GameModel`.
    - **Expected output**: `GameModel.getStatus()` returns
      `GameStatus.CHECKMATE`. A subsequent call to `applyMove` throws
      `IllegalStateException`.
    - **Implemented at**: `checkmate_gameLocksAndThrowsOnFurtherMove`

---

### Castling

- **IT-CS-01: Valid white kingside castling**
    - **State of the system**: White king on `e1` (`hasMoved == false`), white
      rook on `h1` (`hasMoved == false`), `f1` and `g1` empty, none of `e1`,
      `f1`, `g1` attacked.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `true`; after
      simulating `applyMove` mutations king is on `g1` and rook is on `f1`.
    - **Implemented at**: `castling_whiteKingside_isLegalAndBoardCorrect`

- **IT-CS-02: Valid white queenside castling**
    - **State of the system**: White king on `e1` (`hasMoved == false`), white
      rook on `a1` (`hasMoved == false`), `b1`, `c1`, `d1` empty, none of
      `e1`, `d1`, `c1` attacked.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `true`; after
      simulating `applyMove` mutations king is on `c1` and rook is on `d1`.
    - **Implemented at**: `castling_whiteQueenside_isLegalAndBoardCorrect`

- **IT-CS-03: Valid black kingside castling**
    - **State of the system**: Black king on `e8` (`hasMoved == false`), black
      rook on `h8` (`hasMoved == false`), `f8` and `g8` empty, none of `e8`,
      `f8`, `g8` attacked.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `true`.
    - **Implemented at**: `castling_blackKingside_isLegal`

- **IT-CS-04: Castling blocked — pieces between king and rook (kingside)**
    - **State of the system**: White king on `e1` (`hasMoved == false`), white
      rook on `h1` (`hasMoved == false`), white bishop on `f1`.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `false`.
      `RulesEngine.getLegalMoves` does not include `g1` as a legal destination.
    - **Implemented at**: `castling_kingsidePathBlocked_isIllegal`

- **IT-CS-05: Castling blocked — pieces between king and rook (queenside)**
    - **State of the system**: White king on `e1` (`hasMoved == false`), white
      rook on `a1` (`hasMoved == false`), white queen on `d1`.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `false`.
      `RulesEngine.getLegalMoves` does not include `c1`.
    - **Implemented at**: `castling_queensidePathBlocked_isIllegal`

- **IT-CS-06: King has moved — castling disallowed**
    - **State of the system**: White king on `e1` (`hasMoved == true`), white
      rook on `h1` (`hasMoved == false`), `f1` and `g1` empty.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `false`.
    - **Implemented at**: `castling_kingHasMoved_isIllegal`

- **IT-CS-07: Rook has moved — castling disallowed**
    - **State of the system**: White king on `e1` (`hasMoved == false`), white
      rook on `h1` (`hasMoved == true`), `f1` and `g1` empty.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `false`.
    - **Implemented at**: `castling_rookHasMoved_isIllegal`

- **IT-CS-08: Rook absent from board — castling disallowed**
    - **State of the system**: White king on `e1` (`hasMoved == false`), no
      piece on `h1`.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `false`.
    - **Implemented at**: `castling_rookMissing_isIllegal`

- **IT-CS-09: King currently in check — castling disallowed**
    - **State of the system**: White king on `e1` (`hasMoved == false`), white
      rook on `h1` (`hasMoved == false`), `f1`/`g1` empty, but black rook on
      `e8` puts the king in check.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `false`.
    - **Implemented at**: `castling_kingInCheck_isIllegal`

- **IT-CS-10: King would pass through attacked square (kingside) — castling disallowed**
    - **State of the system**: White king on `e1` (`hasMoved == false`), white
      rook on `h1` (`hasMoved == false`), `f1`/`g1` empty, black rook on `f8`
      attacks `f1`.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `false`.
    - **Implemented at**: `castling_kingsidePassesThroughAttackedSquare_isIllegal`

- **IT-CS-11: King would land on attacked square (kingside) — castling disallowed**
    - **State of the system**: White king on `e1` (`hasMoved == false`), white
      rook on `h1` (`hasMoved == false`), `f1`/`g1` empty, black rook on `g8`
      attacks `g1`.
    - **Expected output**: `RulesEngine.isCastlingLegal` returns `false`.
    - **Implemented at**: `castling_kingsideLandsOnAttackedSquare_isIllegal`

- **IT-CS-12: `King.getLegalMoveDestinationSquares` includes castling candidate despite blocked path (gap documentation)**
    - **State of the system**: White king on `e1` (`hasMoved == false`), white
      bishop on `f1`, white rook on `h1`. King's `getLegalMoveDestinationSquares`
      is called directly.
    - **Expected output**: The returned list **does** include `g1` (the King
      generates it as a candidate without path checking). However,
      `RulesEngine.getLegalMoves` for the same position does **not** include
      `g1` (the engine filters it via `isCastlingLegal`). This test documents
      the design gap: path validation lives entirely in the engine, not in the
      piece.
    - **Implemented at**: `castling_kingsideBlocked_kingStillGeneratesCandidate_butEngineFilters`

- **IT-CS-13: `King.getLegalMoveDestinationSquares` includes castling candidate when rook is absent (gap documentation)**
    - **State of the system**: White king on `e1` (`hasMoved == false`), `h1`
      empty (no rook present).
    - **Expected output**: The returned list **does** include `g1` (candidate
      generated). `RulesEngine.getLegalMoves` does **not** include `g1`.
    - **Implemented at**: `castling_rookAbsent_kingStillGeneratesCandidate_butEngineFilters`