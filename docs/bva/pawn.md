# Pawn.md

## Intermediate Analysis (Steps 1–3)

**Input Domains & Variables:**

* `board` (Board): The current chessboard passed to `getLegalMoves`. Used only to resolve square references by coordinate — not to inspect occupancy.
    * Valid values: A non-null `Board` instance containing a valid 8×8 grid of `Square` objects, with files `'a'`–`'h'` and ranks `1`–`8`.
    * Invalid values: `null`.

* `from` (Square): The square currently occupied by this `Pawn` instance.
    * Valid values: Any valid, non-null `Square` with file `'a'`–`'h'` and rank `1`–`8`, whose `occupant` is this `Pawn`.
    * Invalid values: `null`; a `Square` whose `occupant` is not this `Pawn`; a `Square` with out-of-bounds coordinates.

* `color` (Color) — derived from `this.getColor()`: Determines the direction of forward movement.
    * Valid values: `WHITE` (advances toward increasing ranks), `BLACK` (advances toward decreasing ranks).
    * Invalid values: `null` (guarded by `Piece.requireColor` at construction time; cannot arise at `getLegalMoves` call time).

* `hasMoved` (boolean) — derived from `this.hasMoved()`: Controls whether the two-square advance is included as a geometric candidate.
    * Valid values: `false` (Pawn has not yet moved; two-square advance candidate is included), `true` (Pawn has moved at least once; two-square advance candidate is excluded).
    * Boundary: The exact transition from `false` to `true` after `markMoved()` is called following the first move.

* `from.file` (char) — the file of the `from` square: Determines how many diagonal candidate squares exist.
    * Valid values: `'a'`–`'h'`.
    * Boundary: `'a'` (minimum file — no left diagonal candidate exists), `'h'` (maximum file — no right diagonal candidate exists), `'b'`–`'g'` (both diagonal candidates exist).

* `from.rank` (int) — the rank of the `from` square: Determines which candidate squares are within board bounds, and whether the two-square advance candidate is geometrically possible.
    * Valid values: `1`–`8`.
    * Boundary values for White: Starting rank `2` (`hasMoved == false`, two-square advance is a candidate); rank `7` (one-square advance reaches the promotion rank `8`); rank `8` (Pawn is already on final rank — no forward candidates exist; this state should not arise in a legal game but is a structural boundary).
    * Boundary values for Black: Starting rank `7` (`hasMoved == false`, two-square advance is a candidate); rank `2` (one-square advance reaches promotion rank `1`); rank `1` (already on final rank — no forward candidates).

**Boundary Values Identified:**

* `board` Boundaries:
    * Valid state: Non-null `Board` with a complete 8×8 grid.
    * Invalid state: `null`.

* `from` Boundaries:
    * Valid state: Non-null `Square` with file `'a'`–`'h'`, rank `1`–`8`, occupied by this `Pawn`.
    * Invalid state: `null`; square not occupied by this `Pawn`; out-of-bounds coordinates.

* `hasMoved` Boundaries:
    * `false` — two-square advance candidate is included.
    * `true` — two-square advance candidate is excluded.

* File Boundaries (diagonal candidates):
    * Minimum file `'a'`: Left diagonal candidate does not exist; only right diagonal (`'b'`) is generated.
    * Maximum file `'h'`: Right diagonal candidate does not exist; only left diagonal (`'g'`) is generated.
    * Interior files `'b'`–`'g'`: Both left and right diagonal candidates are generated.

* Rank Boundaries (forward candidates):
    * White rank `2` with `hasMoved == false`: Both one-square (`rank 3`) and two-square (`rank 4`) candidates generated.
    * White rank `3`–`6` with `hasMoved == true`: Only one-square forward candidate generated.
    * White rank `7`: One-square forward candidate is `rank 8` (promotion rank) — still generated as a geometric candidate.
    * Black rank `7` with `hasMoved == false`: Both one-square (`rank 6`) and two-square (`rank 5`) candidates generated.
    * Black rank `2`–`6` with `hasMoved == true`: Only one-square forward candidate generated.
    * Black rank `2`: One-square forward candidate is `rank 1` (promotion rank) — still generated as a geometric candidate.

---

## Step 4: Test Cases

### Method under test: `Pawn.getLegalMoves(Board board, Square from)`

- **TC1: Null Board**
    - **State of the system**: `board = null`, valid `from` square with this `Pawn` as occupant.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoves_nullBoard_throwsException`

- **TC2: Null From Square**
    - **State of the system**: Valid `Board`, `from = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoves_nullFrom_throwsException`

- **TC3: From Square Not Occupied By This Pawn**
    - **State of the system**: Valid `Board`, `from` is a valid square whose `occupant` is a different `Piece` (or `null`), not this `Pawn` instance.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoves_fromNotOccupiedByThisPawn_throwsException`

- **TC4: White Pawn — Single-Square Forward Candidate**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e4`.
    - **Expected output**: Returned list contains `e5`. Does not contain `e3` (backward).
    - **Implemented at**: `getLegalMoves_whitePawnHasMoved_containsOneSquareForward`

- **TC5: Black Pawn — Single-Square Forward Candidate**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `e5`.
    - **Expected output**: Returned list contains `e4`. Does not contain `e6` (backward for Black).
    - **Implemented at**: `getLegalMoves_blackPawnHasMoved_containsOneSquareForward`

- **TC6: White Pawn — Two-Square Advance Candidate From Starting Rank**
    - **State of the system**: White `Pawn` (`hasMoved = false`) on `e2`.
    - **Expected output**: Returned list contains both `e3` and `e4`.
    - **Implemented at**: `getLegalMoves_whitePawnHasNotMoved_containsTwoSquareForward`

- **TC7: Black Pawn — Two-Square Advance Candidate From Starting Rank**
    - **State of the system**: Black `Pawn` (`hasMoved = false`) on `e7`.
    - **Expected output**: Returned list contains both `e6` and `e5`.
    - **Implemented at**: `getLegalMoves_blackPawnHasNotMoved_containsTwoSquareForward`

- **TC8: White Pawn — Two-Square Advance Candidate Absent After Having Moved**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e3`.
    - **Expected output**: Returned list contains `e4` but does not contain `e5`.
    - **Implemented at**: `getLegalMoves_whitePawnHasMoved_excludesTwoSquareForward`

- **TC9: Black Pawn — Two-Square Advance Candidate Absent After Having Moved**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `e6`.
    - **Expected output**: Returned list contains `e5` but does not contain `e4`.
    - **Implemented at**: `getLegalMoves_blackPawnHasMoved_excludesTwoSquareForward`

- **TC10: White Pawn — Both Diagonal Candidates Present (Interior File)**
    - **State of the system**: White `Pawn` on `e4` (interior file `'e'`).
    - **Expected output**: Returned list contains `d5` and `f5`.
    - **Implemented at**: `getLegalMoves_whitePawnInteriorFile_containsBothDiagonals`

- **TC11: Black Pawn — Both Diagonal Candidates Present (Interior File)**
    - **State of the system**: Black `Pawn` on `e5` (interior file `'e'`).
    - **Expected output**: Returned list contains `d4` and `f4`.
    - **Implemented at**: `getLegalMoves_blackPawnInteriorFile_containsBothDiagonals`

- **TC12: White Pawn On Minimum File (`'a'`) — Only Right Diagonal Candidate Exists**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `a4`.
    - **Expected output**: Returned list contains `a5` and `b5`. Does not contain any square on file `` ` `` (out of bounds).
    - **Implemented at**: `getLegalMoves_whitePawnOnFileA_onlyRightDiagonalCandidate`

- **TC13: White Pawn On Maximum File (`'h'`) — Only Left Diagonal Candidate Exists**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `h4`.
    - **Expected output**: Returned list contains `h5` and `g5`. Does not contain any square on file `'i'` (out of bounds).
    - **Implemented at**: `getLegalMoves_whitePawnOnFileH_onlyLeftDiagonalCandidate`

- **TC14: Black Pawn On Minimum File (`'a'`) — Only Right Diagonal Candidate Exists**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `a5`.
    - **Expected output**: Returned list contains `a4` and `b4`. Does not contain any square on file `` ` `` (out of bounds).
    - **Implemented at**: `getLegalMoves_blackPawnOnFileA_onlyRightDiagonalCandidate`

- **TC15: Black Pawn On Maximum File (`'h'`) — Only Left Diagonal Candidate Exists**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `h5`.
    - **Expected output**: Returned list contains `h4` and `g4`. Does not contain any square on file `'i'` (out of bounds).
    - **Implemented at**: `getLegalMoves_blackPawnOnFileH_onlyLeftDiagonalCandidate`

- **TC16: White Pawn — Forward Candidate Reaches Promotion Rank**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e7`.
    - **Expected output**: Returned list contains `e8` (promotion rank is a valid geometric candidate). Also contains `d8` and `f8` (diagonal promotion candidates).
    - **Implemented at**: `getLegalMoves_whitePawnOnRank7_containsPromotionRankCandidates`

- **TC17: Black Pawn — Forward Candidate Reaches Promotion Rank**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `e2`.
    - **Expected output**: Returned list contains `e1` (promotion rank is a valid geometric candidate). Also contains `d1` and `f1` (diagonal promotion candidates).
    - **Implemented at**: `getLegalMoves_blackPawnOnRank2_containsPromotionRankCandidates`

- **TC18: White Pawn — Backward Square Not A Candidate**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e4`.
    - **Expected output**: Returned list does not contain `e3`.
    - **Implemented at**: `getLegalMoves_whitePawn_excludesBackwardSquare`

- **TC19: Black Pawn — Backward Square Not A Candidate**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `e5`.
    - **Expected output**: Returned list does not contain `e6`.
    - **Implemented at**: `getLegalMoves_blackPawn_excludesBackwardSquare`

- **TC20: White Pawn — Correct Total Candidate Count From Interior File After Having Moved**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e4` (interior file, non-boundary rank).
    - **Expected output**: Returned list contains exactly 3 squares: `e5` (forward), `d5` (left diagonal), `f5` (right diagonal).
    - **Implemented at**: `getLegalMoves_whitePawnInteriorFileHasMoved_returnsThreeCandidates`

- **TC21: White Pawn — Correct Total Candidate Count From Interior File Without Having Moved**
    - **State of the system**: White `Pawn` (`hasMoved = false`) on `e2` (interior file, starting rank).
    - **Expected output**: Returned list contains exactly 4 squares: `e3`, `e4` (forward candidates), `d3` (left diagonal), `f3` (right diagonal).
    - **Implemented at**: `getLegalMoves_whitePawnInteriorFileHasNotMoved_returnsFourCandidates`