# Bishop.md

## Intermediate Analysis (Steps 1–3)

**Input Domains & Variables:**

* `from` (Square): The square currently occupied by this `Bishop` instance.
    * Valid values: Any valid, non-null `Square` with file `'a'`–`'h'` and rank `1`–`8`, whose `occupant` is this `Bishop`.
    * Invalid values: `null`; a `Square` whose `occupant` is not this `Bishop`; a `Square` with out-of-bounds coordinates.

* `color` (Color) — derived from `this.getColor()`.
    * Valid values: `WHITE`, `BLACK`.
    * Invalid values: `null` guarded by `Piece.requireColor` at construction time; cannot arise at `getLegalMoveDestinationSquares` call time.

* `from.file` (char): The file of the `from` square.
    * Valid values: `'a'`–`'h'`.
    * Boundary values: `'a'` minimum file, `'h'` maximum file, and interior files `'b'`–`'g'`.

* `from.rank` (int): The rank of the `from` square.
    * Valid values: `1`–`8`.
    * Boundary values: `1` minimum rank, `8` maximum rank, and interior ranks `2`–`7`.

**Boundary Values Identified:**

* `from` Boundaries:
    * Valid state: Non-null `Square` with file `'a'`–`'h'`, rank `1`–`8`, occupied by this `Bishop`.
    * Invalid state: `null`; square not occupied by this `Bishop`; out-of-bounds coordinates.

* File Boundaries:
    * Minimum file `'a'`: No left diagonals exist.
    * Maximum file `'h'`: No right diagonals exist.
    * Interior files `'b'`–`'g'`: Diagonal candidates may exist in both left and right directions.

* Rank Boundaries:
    * Minimum rank `1`: No downward diagonals exist.
    * Maximum rank `8`: No upward diagonals exist.
    * Interior ranks `2`–`7`: Diagonal candidates may exist in both upward and downward directions.

* Diagonal Movement Boundaries:
    * Corner squares such as `a1` and `h8`: Bishop has exactly one diagonal direction available.
    * Interior squares such as `d4`: Bishop has all four diagonal directions available.
    * Starting square is not included as a candidate.
    * Orthogonal squares are not included as candidates.

---

## Step 4: Test Cases

### Method under test: `Bishop.getLegalMoveDestinationSquares(Square from)`
- **TC1: Null From Square**
    - **State of the system**: `from = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_nullFrom_throwsException`

- **TC2: From Square Not Occupied By This Bishop**
    - **State of the system**: `from` is a valid square whose `occupant` is a different `Piece` or `null`, not this `Bishop` instance.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_fromNotOccupiedByThisBishop_throwsException`

- **TC3: Bishop From Center Contains All Four Diagonal Directions**
    - **State of the system**: Bishop on `d4`.
    - **Expected output**: Returned list contains at least one square in each diagonal direction: `e5`, `e3`, `c5`, and `c3`.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromCenter_containsAllFourDiagonalDirections`

- **TC4: Bishop From Center Excludes Orthogonal Squares**
    - **State of the system**: Bishop on `d4`.
    - **Expected output**: Returned list does not contain orthogonal squares `d5`, `d3`, `e4`, or `c4`.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromCenter_excludesOrthogonalSquares`

- **TC5: Bishop From Center Returns Correct Candidate Count**
    - **State of the system**: Bishop on `d4`.
    - **Expected output**: Returned list contains exactly 13 diagonal candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromCenter_returnsCorrectCandidateCount`

- **TC6: Bishop From Minimum Corner `a1` Contains Only Up-Right Diagonal**
    - **State of the system**: Bishop on `a1`.
    - **Expected output**: Returned list contains exactly `b2`, `c3`, `d4`, `e5`, `f6`, `g7`, and `h8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromA1_containsOnlyUpRightDiagonal`

- **TC7: Bishop From Maximum Corner `h8` Contains Only Down-Left Diagonal**
    - **State of the system**: Bishop on `h8`.
    - **Expected output**: Returned list contains exactly `g7`, `f6`, `e5`, `d4`, `c3`, `b2`, and `a1`.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromH8_containsOnlyDownLeftDiagonal`

- **TC8: Bishop From Minimum File Interior Square Has No Left Diagonals**
    - **State of the system**: Bishop on `a4`.
    - **Expected output**: Returned list contains `b5` and `b3`, contains no out-of-bounds files, and contains exactly 7 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromFileA_hasNoLeftDiagonals`

- **TC9: Bishop From Maximum File Interior Square Has No Right Diagonals**
    - **State of the system**: Bishop on `h4`.
    - **Expected output**: Returned list contains `g5` and `g3`, contains no out-of-bounds files, and contains exactly 7 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromFileH_hasNoRightDiagonals`

- **TC10: Bishop From Minimum Rank Interior Square Has No Downward Diagonals**
    - **State of the system**: Bishop on `d1`.
    - **Expected output**: Returned list contains `e2` and `c2`, contains no out-of-bounds ranks, and contains exactly 7 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromRank1_hasNoDownwardDiagonals`

- **TC11: Bishop From Maximum Rank Interior Square Has No Upward Diagonals**
    - **State of the system**: Bishop on `d8`.
    - **Expected output**: Returned list contains `e7` and `c7`, contains no out-of-bounds ranks, and contains exactly 7 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromRank8_hasNoUpwardDiagonals`

- **TC12: Bishop From Center Does Not Include Starting Square**
    - **State of the system**: Bishop on `d4`.
    - **Expected output**: Returned list does not contain `d4`.
    - **Implemented at**: `getLegalMoveDestinationSquares_bishopFromCenter_excludesStartingSquare`