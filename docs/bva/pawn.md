# Pawn.md

## Intermediate Analysis (Steps 1–3)

**Input Domains & Variables:**

* `from` (Square): The square currently occupied by this `Pawn` instance.
    * Valid values: Any valid, non-null `Square` with file `'a'`–`'h'` and rank `1`–`8`, whose `occupant` is this `Pawn`.
    * Invalid values: `null`; a `Square` whose `occupant` is not this `Pawn`; a `Square` with out-of-bounds coordinates.

* `color` (Color) — derived from `this.getColor()`: Determines the direction of forward movement.
    * Valid values: `WHITE` (advances toward increasing ranks), `BLACK` (advances toward decreasing ranks).
    * Invalid values: `null` (guarded by `Piece.requireColor` at construction time; cannot arise at `getLegalMoveDestinationSquares` call time).

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

### Method under test: `Pawn.getLegalMoveDestinationSquares(Square from)`

- **TC1: Null From Square**
    - **State of the system**: `from = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_nullFrom_throwsException`

- **TC2: From Square Not Occupied By This Pawn**
    - **State of the system**: `from` is a valid square whose `occupant` is a different `Piece` (or `null`), not this `Pawn` instance.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_fromNotOccupiedByThisPawn_throwsException`

- **TC3: White Pawn — Single-Square Forward Candidate**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e4`.
    - **Expected output**: Returned list contains `e5`. Does not contain `e3` (backward).
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawnHasMoved_containsOneSquareForward`

- **TC4: Black Pawn — Single-Square Forward Candidate**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `e5`.
    - **Expected output**: Returned list contains `e4`. Does not contain `e6` (backward for Black).
    - **Implemented at**: `getLegalMoveDestinationSquares_blackPawnHasMoved_containsOneSquareForward`

- **TC5: White Pawn — Two-Square Advance Candidate From Starting Rank**
    - **State of the system**: White `Pawn` (`hasMoved = false`) on `e2`.
    - **Expected output**: Returned list contains both `e3` and `e4`.
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawnHasNotMoved_containsTwoSquareForward`

- **TC6: Black Pawn — Two-Square Advance Candidate From Starting Rank**
    - **State of the system**: Black `Pawn` (`hasMoved = false`) on `e7`.
    - **Expected output**: Returned list contains both `e6` and `e5`.
    - **Implemented at**: `getLegalMoveDestinationSquares_blackPawnHasNotMoved_containsTwoSquareForward`

- **TC7: White Pawn — Two-Square Advance Candidate Absent After Having Moved**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e3`.
    - **Expected output**: Returned list contains `e4` but does not contain `e5`.
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawnHasMoved_excludesTwoSquareForward`

- **TC8: Black Pawn — Two-Square Advance Candidate Absent After Having Moved**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `e6`.
    - **Expected output**: Returned list contains `e5` but does not contain `e4`.
    - **Implemented at**: `getLegalMoveDestinationSquares_blackPawnHasMoved_excludesTwoSquareForward`

- **TC9: White Pawn — Both Diagonal Candidates Present (Interior File)**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e4` (interior file `'e'`).
    - **Expected output**: Returned list contains `d5` and `f5`.
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawnInteriorFile_containsBothDiagonals`

- **TC10: Black Pawn — Both Diagonal Candidates Present (Interior File)**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `e5` (interior file `'e'`).
    - **Expected output**: Returned list contains `d4` and `f4`.
    - **Implemented at**: `getLegalMoveDestinationSquares_blackPawnInteriorFile_containsBothDiagonals`

- **TC11: White Pawn On Minimum File (`'a'`) — Only Right Diagonal Candidate Exists**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `a4`.
    - **Expected output**: Returned list contains `a5` and `b5`. All returned squares have file within `'a'`–`'h'` and rank within `1`–`8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawnOnFileA_onlyRightDiagonalCandidate`

- **TC12: White Pawn On Maximum File (`'h'`) — Only Left Diagonal Candidate Exists**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `h4`.
    - **Expected output**: Returned list contains `h5` and `g5`. All returned squares have file within `'a'`–`'h'` and rank within `1`–`8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawnOnFileH_onlyLeftDiagonalCandidate`

- **TC13: Black Pawn On Minimum File (`'a'`) — Only Right Diagonal Candidate Exists**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `a5`.
    - **Expected output**: Returned list contains `a4` and `b4`. All returned squares have file within `'a'`–`'h'` and rank within `1`–`8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_blackPawnOnFileA_onlyRightDiagonalCandidate`

- **TC14: Black Pawn On Maximum File (`'h'`) — Only Left Diagonal Candidate Exists**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `h5`.
    - **Expected output**: Returned list contains `h4` and `g4`. All returned squares have file within `'a'`–`'h'` and rank within `1`–`8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_blackPawnOnFileH_onlyLeftDiagonalCandidate`

- **TC15: White Pawn — Forward Candidate Reaches Promotion Rank**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e7`.
    - **Expected output**: Returned list contains `e8`, `d8`, and `f8` (forward and diagonal promotion rank candidates).
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawnOnRank7_containsPromotionRankCandidates`

- **TC16: Black Pawn — Forward Candidate Reaches Promotion Rank**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `e2`.
    - **Expected output**: Returned list contains `e1`, `d1`, and `f1` (forward and diagonal promotion rank candidates).
    - **Implemented at**: `getLegalMoveDestinationSquares_blackPawnOnRank2_containsPromotionRankCandidates`

- **TC17: White Pawn — Backward Square Not A Candidate**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e4`.
    - **Expected output**: Returned list does not contain `e3`.
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawn_excludesBackwardSquare`

- **TC18: Black Pawn — Backward Square Not A Candidate**
    - **State of the system**: Black `Pawn` (`hasMoved = true`) on `e5`.
    - **Expected output**: Returned list does not contain `e6`.
    - **Implemented at**: `getLegalMoveDestinationSquares_blackPawn_excludesBackwardSquare`

- **TC19: White Pawn — Correct Total Candidate Count From Interior File After Having Moved**
    - **State of the system**: White `Pawn` (`hasMoved = true`) on `e4` (interior file, non-boundary rank).
    - **Expected output**: Returned list contains exactly 3 squares: `e5` (forward), `d5` (left diagonal), `f5` (right diagonal).
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawnInteriorFileHasMoved_returnsThreeCandidates`

- **TC20: White Pawn — Correct Total Candidate Count From Interior File Without Having Moved**
    - **State of the system**: White `Pawn` (`hasMoved = false`) on `e2` (interior file, starting rank).
    - **Expected output**: Returned list contains exactly 4 squares: `e3`, `e4` (forward candidates), `d3` (left diagonal), `f3` (right diagonal).
    - **Implemented at**: `getLegalMoveDestinationSquares_whitePawnInteriorFileHasNotMoved_returnsFourCandidates`