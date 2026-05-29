# Rook.md

## Intermediate Analysis (Steps 1–3)

**Input Domains & Variables:**

* `from` (Square): The square currently occupied by this `Rook` instance.
    * Valid values: Any valid, non-null `Square` with file `'a'`–`'h'` and rank `1`–`8`, whose `occupant` is this `Rook`.
    * Invalid values: `null`; a `Square` whose `occupant` is not this `Rook`; a `Square` with out-of-bounds coordinates.

* `color` (Color) — derived from `this.getColor()`.
    * Valid values: `WHITE`, `BLACK`.
    * Invalid values: `null` guarded by `Piece.requireColor` at construction time; cannot arise at `getLegalMoveDestinationSquares` call time.
    * Note: Rook movement does not depend on color.

* `from.file` (char): The file of the `from` square.
    * Valid values: `'a'`–`'h'`.
    * Boundary values: `'a'` minimum file, `'h'` maximum file, and interior files `'b'`–`'g'`.

* `from.rank` (int): The rank of the `from` square.
    * Valid values: `1`–`8`.
    * Boundary values: `1` minimum rank, `8` maximum rank, and interior ranks `2`–`7`.

**Boundary Values Identified:**

* `from` Boundaries:
    * Valid state: Non-null `Square` with file `'a'`–`'h'`, rank `1`–`8`, occupied by this `Rook`.
    * Invalid state: `null`; square not occupied by this `Rook`; out-of-bounds coordinates.

* File Boundaries:
    * Minimum file `'a'`: No left candidates exist.
    * Maximum file `'h'`: No right candidates exist.
    * Interior files `'b'`–`'g'`: Horizontal candidates may exist in both left and right directions.

* Rank Boundaries:
    * Minimum rank `1`: No downward candidates exist.
    * Maximum rank `8`: No upward candidates exist.
    * Interior ranks `2`–`7`: Vertical candidates may exist in both upward and downward directions.

* Straight-Line Movement Boundaries:
    * Corner squares such as `a1` and `h8`: Rook has exactly two straight directions available.
    * Interior squares such as `d4`: Rook has all four straight directions available.
    * Starting square is not included as a candidate.
    * Diagonal squares are not included as candidates.

---

## Step 4: Test Cases

### Method under test: `Rook.getLegalMoveDestinationSquares(Square from)`

- **TC1: Null From Square**
    - **State of the system**: `from = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_nullFrom_throwsException`

- **TC2: From Square Not Occupied By This Rook**
    - **State of the system**: `from` is a valid square whose `occupant` is a different `Piece` or `null`, not this `Rook` instance.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_fromNotOccupiedByThisRook_throwsException`

- **TC3: Rook From Center Contains All Four Straight Directions**
    - **State of the system**: Rook on `d4`.
    - **Expected output**: Returned list contains at least one square in each straight direction: `e4`, `c4`, `d5`, and `d3`.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromCenter_containsAllFourStraightDirections`

- **TC4: Rook From Center Excludes Diagonal Squares**
    - **State of the system**: Rook on `d4`.
    - **Expected output**: Returned list does not contain diagonal squares `e5`, `e3`, `c5`, or `c3`.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromCenter_excludesDiagonalSquares`

- **TC5: Rook From Center Returns Correct Candidate Count**
    - **State of the system**: Rook on `d4`.
    - **Expected output**: Returned list contains exactly 14 straight-line candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromCenter_returnsCorrectCandidateCount`

- **TC6: Rook From Minimum Corner `a1` Contains Only Up And Right Lines**
    - **State of the system**: Rook on `a1`.
    - **Expected output**: Returned list contains all squares on rank `1` to the right (`b1`–`h1`) and all squares on file `a` above (`a2`–`a8`), for exactly 14 candidates.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromA1_containsOnlyUpAndRightLines`

- **TC7: Rook From Maximum Corner `h8` Contains Only Down And Left Lines**
    - **State of the system**: Rook on `h8`.
    - **Expected output**: Returned list contains all squares on rank `8` to the left (`a8`–`g8`) and all squares on file `h` below (`h1`–`h7`), for exactly 14 candidates.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromH8_containsOnlyDownAndLeftLines`

- **TC8: Rook From Minimum File Interior Square Has No Left Candidates**
    - **State of the system**: Rook on `a4`.
    - **Expected output**: Returned list contains `b4`, `a5`, and `a3`; contains no out-of-bounds files; contains exactly 14 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromFileA_hasNoLeftCandidates`

- **TC9: Rook From Maximum File Interior Square Has No Right Candidates**
    - **State of the system**: Rook on `h4`.
    - **Expected output**: Returned list contains `g4`, `h5`, and `h3`; contains no out-of-bounds files; contains exactly 14 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromFileH_hasNoRightCandidates`

- **TC10: Rook From Minimum Rank Interior Square Has No Downward Candidates**
    - **State of the system**: Rook on `d1`.
    - **Expected output**: Returned list contains `e1`, `c1`, and `d2`; contains no out-of-bounds ranks; contains exactly 14 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromRank1_hasNoDownwardCandidates`

- **TC11: Rook From Maximum Rank Interior Square Has No Upward Candidates**
    - **State of the system**: Rook on `d8`.
    - **Expected output**: Returned list contains `e8`, `c8`, and `d7`; contains no out-of-bounds ranks; contains exactly 14 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromRank8_hasNoUpwardCandidates`

- **TC12: Rook From Center Does Not Include Starting Square**
    - **State of the system**: Rook on `d4`.
    - **Expected output**: Returned list does not contain `d4`.
    - **Implemented at**: `getLegalMoveDestinationSquares_rookFromCenter_excludesStartingSquare`