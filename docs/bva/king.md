# King.md

## Intermediate Analysis (Steps 1–3)

**Input Domains & Variables:**

* `from` (Square): The square currently occupied by this `King` instance.
    * Valid values: Any valid, non-null `Square` with file `'a'`–`'h'` and rank `1`–`8`, whose `occupant` is this `King`.
    * Invalid values: `null`; a `Square` whose `occupant` is not this `King`; a `Square` with out-of-bounds coordinates.

* `color` (Color) — derived from `this.getColor()`: Determines which castling destination squares are included as candidates when `hasMoved == false`.
    * Valid values: `WHITE` (castling candidates are `c1` and `g1`), `BLACK` (castling candidates are `c8` and `g8`).
    * Invalid values: `null` (guarded by `Piece.requireColor` at construction time; cannot arise at `getLegalMoveDestinationSquares` call time).

* `hasMoved` (boolean) — derived from `this.hasMoved()`: Controls whether the two castling destination squares are included as geometric candidates.
    * Valid values: `false` (King has not yet moved; castling destination candidates `c1`/`g1` for White or `c8`/`g8` for Black are included), `true` (King has moved at least once; castling destination candidates are excluded).
    * Boundary: The exact transition from `false` to `true` after `markMoved()` is called following the first move.

* `from.file` (char) — the file of the `from` square: Determines how many of the eight one-step candidate squares fall within board bounds on the file axis.
    * Valid values: `'a'`–`'h'`.
    * Boundary values: `'a'` (minimum file — no candidates to the left exist; only candidates on files `'a'` and `'b'` are generated), `'h'` (maximum file — no candidates to the right exist; only candidates on files `'g'` and `'h'` are generated), `'b'`–`'g'` (interior files — all three file columns `file-1`, `file`, `file+1` are in bounds).
    * Note: Castling destination squares (`c1`, `g1`, `c8`, `g8`) are fixed and always within board bounds; file boundary clipping does not affect them.

* `from.rank` (int) — the rank of the `from` square: Determines how many of the eight one-step candidate squares fall within board bounds on the rank axis.
    * Valid values: `1`–`8`.
    * Boundary values: `1` (minimum rank — no candidates below exist; only candidates on ranks `1` and `2` are generated), `8` (maximum rank — no candidates above exist; only candidates on ranks `7` and `8` are generated), `2`–`7` (interior ranks — all three rank rows `rank-1`, `rank`, `rank+1` are in bounds).
    * Note: Castling destination squares are fixed and always within board bounds; rank boundary clipping does not affect them.

**Boundary Values Identified:**

* `from` Boundaries:
    * Valid state: Non-null `Square` with file `'a'`–`'h'`, rank `1`–`8`, occupied by this `King`.
    * Invalid state: `null`; square not occupied by this `King`; out-of-bounds coordinates.

* `hasMoved` Boundaries:
    * `false` — castling destination candidates are included (`c1` and `g1` for White; `c8` and `g8` for Black).
    * `true` — castling destination candidates are excluded.

* File Boundaries (left/right one-step candidates):
    * Minimum file `'a'`: No candidates to the left; candidates on files `'a'` and `'b'` are generated (subject to rank bounds).
    * Maximum file `'h'`: No candidates to the right; candidates on files `'g'` and `'h'` are generated (subject to rank bounds).
    * Interior files `'b'`–`'g'`: All three file columns (`file-1`, `file`, `file+1`) are in bounds; all directional candidates are generated (subject to rank bounds).

* Rank Boundaries (above/below one-step candidates):
    * Minimum rank `1`: No candidates below; candidates on ranks `1` and `2` are generated (subject to file bounds).
    * Maximum rank `8`: No candidates above; candidates on ranks `7` and `8` are generated (subject to file bounds).
    * Interior ranks `2`–`7`: All three rank rows (`rank-1`, `rank`, `rank+1`) are in bounds; all directional candidates are generated (subject to file bounds).

* Combined Corner Boundaries (minimum one-step candidate count — 3 candidates):
    * Corner `a1` (minimum file, minimum rank): Only files `'a'`–`'b'` and ranks `1`–`2` in bounds → 3 one-step candidates: `b1`, `a2`, `b2`.
    * Corner `a8` (minimum file, maximum rank): Only files `'a'`–`'b'` and ranks `7`–`8` in bounds → 3 one-step candidates: `b8`, `a7`, `b7`.
    * Corner `h1` (maximum file, minimum rank): Only files `'g'`–`'h'` and ranks `1`–`2` in bounds → 3 one-step candidates: `g1`, `h2`, `g2`.
    * Corner `h8` (maximum file, maximum rank): Only files `'g'`–`'h'` and ranks `7`–`8` in bounds → 3 one-step candidates: `g8`, `h7`, `g7`.

* Combined Edge Boundaries (non-corner edge squares — 5 one-step candidates):
    * Minimum file, interior rank (e.g., `a4`): Files `'a'`–`'b'`, ranks `3`–`5` in bounds → 5 one-step candidates.
    * Maximum file, interior rank (e.g., `h4`): Files `'g'`–`'h'`, ranks `3`–`5` in bounds → 5 one-step candidates.
    * Interior file, minimum rank (e.g., `e1`): Files `'d'`–`'f'`, ranks `1`–`2` in bounds → 5 one-step candidates.
    * Interior file, maximum rank (e.g., `e8`): Files `'d'`–`'f'`, ranks `7`–`8` in bounds → 5 one-step candidates.

* Interior Position (maximum one-step candidate count — 8 candidates):
    * Any square with file `'b'`–`'g'` and rank `2`–`7` (e.g., `e4`): All eight directional candidates are within bounds → 8 one-step candidates.

* Castling Destination Boundaries:
    * `hasMoved == false`, `color == WHITE`: Candidates `c1` (queenside) and `g1` (kingside) are added, giving 10 total candidates from `e1`.
    * `hasMoved == false`, `color == BLACK`: Candidates `c8` (queenside) and `g8` (kingside) are added, giving 10 total candidates from `e8`.
    * `hasMoved == true`: No castling destination candidates are added regardless of color.

---

## Step 4: Test Cases

### Method under test: `King.getLegalMoveDestinationSquares(Square from)`

- **TC1: Null From Square**
    - **State of the system**: `from = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_nullFrom_throwsException`

- **TC2: From Square Not Occupied By This King**
    - **State of the system**: `from` is a valid square whose `occupant` is a different `Piece` (or `null`), not this `King` instance.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_fromNotOccupiedByThisKing_throwsException`

- **TC3: King at Interior Square — All Eight One-Step Candidates Generated**
    - **State of the system**: `King` (`hasMoved = true`) on `e4` (interior file `'e'`, interior rank `4`).
    - **Expected output**: Returned list contains exactly 8 squares: `d3`, `e3`, `f3`, `d4`, `f4`, `d5`, `e5`, `f5`. All returned squares have file within `'a'`–`'h'` and rank within `1`–`8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorSquare_returnsEightCandidates`

- **TC4: King at Minimum-File Edge — Left Candidates Absent**
    - **State of the system**: `King` (`hasMoved = true`) on `a4` (minimum file `'a'`, interior rank `4`).
    - **Expected output**: Returned list contains exactly 5 squares: `a3`, `b3`, `b4`, `a5`, `b5`. No square with file `'`'` (one below `'a'`) is generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_fileA_interiorRank_returnsFiveCandidates`

- **TC5: King at Maximum-File Edge — Right Candidates Absent**
    - **State of the system**: `King` (`hasMoved = true`) on `h4` (maximum file `'h'`, interior rank `4`).
    - **Expected output**: Returned list contains exactly 5 squares: `h3`, `g3`, `g4`, `h5`, `g5`. No square with file `'i'` is generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_fileH_interiorRank_returnsFiveCandidates`

- **TC6: King at Minimum-Rank Edge — Below Candidates Absent**
    - **State of the system**: `King` (`hasMoved = true`) on `e1` (interior file `'e'`, minimum rank `1`).
    - **Expected output**: Returned list contains exactly 5 squares: `d1`, `f1`, `d2`, `e2`, `f2`. No square with rank `0` is generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_rank1_interiorFile_returnsFiveCandidates`

- **TC7: King at Maximum-Rank Edge — Above Candidates Absent**
    - **State of the system**: `King` (`hasMoved = true`) on `e8` (interior file `'e'`, maximum rank `8`).
    - **Expected output**: Returned list contains exactly 5 squares: `d8`, `f8`, `d7`, `e7`, `f7`. No square with rank `9` is generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_rank8_interiorFile_returnsFiveCandidates`

- **TC8: King at Corner `a1` — Minimum Candidate Count**
    - **State of the system**: `King` (`hasMoved = true`) on `a1` (minimum file, minimum rank).
    - **Expected output**: Returned list contains exactly 3 squares: `b1`, `a2`, `b2`. No out-of-bounds squares are generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerA1_returnsThreeCandidates`

- **TC9: King at Corner `a8` — Minimum Candidate Count**
    - **State of the system**: `King` (`hasMoved = true`) on `a8` (minimum file, maximum rank).
    - **Expected output**: Returned list contains exactly 3 squares: `b8`, `a7`, `b7`. No out-of-bounds squares are generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerA8_returnsThreeCandidates`

- **TC10: King at Corner `h1` — Minimum Candidate Count**
    - **State of the system**: `King` (`hasMoved = true`) on `h1` (maximum file, minimum rank).
    - **Expected output**: Returned list contains exactly 3 squares: `g1`, `h2`, `g2`. No out-of-bounds squares are generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerH1_returnsThreeCandidates`

- **TC11: King at Corner `h8` — Minimum Candidate Count**
    - **State of the system**: `King` (`hasMoved = true`) on `h8` (maximum file, maximum rank).
    - **Expected output**: Returned list contains exactly 3 squares: `g8`, `h7`, `g7`. No out-of-bounds squares are generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerH8_returnsThreeCandidates`

- **TC12: `from` Square Is Not A Candidate**
    - **State of the system**: `King` (`hasMoved = true`) on `e4` (interior square).
    - **Expected output**: Returned list does not contain a square with file `'e'` and rank `4` (the `from` square itself).
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorSquare_excludesFromSquare`

- **TC13: All Returned Squares Are Within Board Bounds**
    - **State of the system**: `King` (`hasMoved = true`) on `a1` (worst-case corner for out-of-bounds generation).
    - **Expected output**: Every square in the returned list has file within `'a'`–`'h'` and rank within `1`–`8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerA1_allCandidatesInBounds`

- **TC14: White King — Castling Destination Candidates Present When Not Moved**
    - **State of the system**: White `King` (`hasMoved = false`) on `e1`.
    - **Expected output**: Returned list contains `c1` (queenside castling destination) and `g1` (kingside castling destination).
    - **Implemented at**: `getLegalMoveDestinationSquares_whiteKingHasNotMoved_containsCastlingDestinations`

- **TC15: Black King — Castling Destination Candidates Present When Not Moved**
    - **State of the system**: Black `King` (`hasMoved = false`) on `e8`.
    - **Expected output**: Returned list contains `c8` (queenside castling destination) and `g8` (kingside castling destination).
    - **Implemented at**: `getLegalMoveDestinationSquares_blackKingHasNotMoved_containsCastlingDestinations`

- **TC16: White King — Castling Destination Candidates Absent After Having Moved**
    - **State of the system**: White `King` (`hasMoved = true`) on `e1`.
    - **Expected output**: Returned list does not contain `c1` or `g1`.
    - **Implemented at**: `getLegalMoveDestinationSquares_whiteKingHasMoved_excludesCastlingDestinations`

- **TC17: Black King — Castling Destination Candidates Absent After Having Moved**
    - **State of the system**: Black `King` (`hasMoved = true`) on `e8`.
    - **Expected output**: Returned list does not contain `c8` or `g8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_blackKingHasMoved_excludesCastlingDestinations`

- **TC18: White King — Correct Total Candidate Count From Starting Square Without Having Moved**
    - **State of the system**: White `King` (`hasMoved = false`) on `e1` (starting square, minimum rank, interior file).
    - **Expected output**: Returned list contains exactly 10 squares: `d1`, `f1`, `d2`, `e2`, `f2` (one-step candidates) and `c1`, `g1` (castling destinations).
    - **Implemented at**: `getLegalMoveDestinationSquares_whiteKingHasNotMoved_returnsTenCandidates`

- **TC19: White King — Correct Total Candidate Count From Starting Square After Having Moved**
    - **State of the system**: White `King` (`hasMoved = true`) on `e1` (starting square, minimum rank, interior file).
    - **Expected output**: Returned list contains exactly 5 squares: `d1`, `f1`, `d2`, `e2`, `f2`. Does not contain `c1` or `g1`.
    - **Implemented at**: `getLegalMoveDestinationSquares_whiteKingHasMoved_returnsFiveCandidates`