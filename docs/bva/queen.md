# Queen.md

## Intermediate Analysis (Steps 1–3)

**Input Domains & Variables:**

* `from` (Square): The square currently occupied by this `Queen` instance.
    * Valid values: Any valid, non-null `Square` with file `'a'`–`'h'` and rank `1`–`8`, whose `occupant` is this `Queen`.
    * Invalid values: `null`; a `Square` whose `occupant` is not this `Queen`; a `Square` with out-of-bounds coordinates.

* `color` (Color) — derived from `this.getColor()`.
    * Valid values: `WHITE`, `BLACK`.
    * Invalid values: `null` guarded by `Piece.requireColor` at construction time; cannot arise at `getLegalMoveDestinationSquares` call time.
    * Note: Queen movement does not depend on color.

* `hasMoved` (boolean) — derived from `this.hasMoved()`.
    * Valid values: `false`, `true`.
    * Note: Queen movement does not depend on whether the Queen has moved.

* `from.file` (char): The file of the `from` square.
    * Valid values: `'a'`–`'h'`.
    * Boundary values: `'a'` minimum file, `'h'` maximum file, and interior files `'b'`–`'g'`.

* `from.rank` (int): The rank of the `from` square.
    * Valid values: `1`–`8`.
    * Boundary values: `1` minimum rank, `8` maximum rank, and interior ranks `2`–`7`.

**Boundary Values Identified:**

* `from` Boundaries:
    * Valid state: Non-null `Square` with file `'a'`–`'h'`, rank `1`–`8`, occupied by this `Queen`.
    * Invalid state: `null`; square not occupied by this `Queen`; out-of-bounds coordinates.

* File Boundaries:
    * Minimum file `'a'`: No left or left-diagonal candidates exist.
    * Maximum file `'h'`: No right or right-diagonal candidates exist.
    * Interior files `'b'`–`'g'`: Horizontal and diagonal candidates may exist in both left and right directions.

* Rank Boundaries:
    * Minimum rank `1`: No downward or downward-diagonal candidates exist.
    * Maximum rank `8`: No upward or upward-diagonal candidates exist.
    * Interior ranks `2`–`7`: Vertical and diagonal candidates may exist in both upward and downward directions.

* Queen Movement Boundaries:
    * Corner squares such as `a1`, `a8`, `h1`, and `h8`: Queen has three directions available.
    * Interior squares such as `d4`: Queen has all eight directions available.
    * Starting square is not included as a candidate.
    * Knight-like movement squares are not included as candidates.
    * Queen movement combines Rook-style straight movement and Bishop-style diagonal movement.

---

## Step 4: Test Cases

### Method under test: `Queen.getLegalMoveDestinationSquares(Square from)`

- **TC1: Null From Square**
    - **State of the system**: `from = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_nullFrom_throwsException`

- **TC2: From Square Not Occupied By This Queen**
    - **State of the system**: `from` is a valid square whose `occupant` is a different `Piece` or `null`, not this `Queen` instance.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_fromNotOccupiedByThisQueen_throwsException`

- **TC3: Queen at Interior Square — All Eight Directions Generated**
    - **State of the system**: Queen on `d4`.
    - **Expected output**: Returned list contains at least one square in each direction: `e4`, `c4`, `d5`, `d3`, `e5`, `e3`, `c5`, and `c3`.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorSquare_containsAllEightDirections`

- **TC4: Queen at Interior Square — Straight Candidates Present**
    - **State of the system**: Queen on `d4`.
    - **Expected output**: Returned list contains straight-line candidates `e4`, `c4`, `d5`, and `d3`.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorSquare_containsStraightCandidates`

- **TC5: Queen at Interior Square — Diagonal Candidates Present**
    - **State of the system**: Queen on `d4`.
    - **Expected output**: Returned list contains diagonal candidates `e5`, `e3`, `c5`, and `c3`.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorSquare_containsDiagonalCandidates`

- **TC6: Queen at Interior Square — Knight-Like Squares Absent**
    - **State of the system**: Queen on `d4`.
    - **Expected output**: Returned list does not contain knight-like squares `f5`, `f3`, `b5`, or `b3`.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorSquare_excludesKnightLikeSquares`

- **TC7: Queen at Interior Square — Correct Total Candidate Count**
    - **State of the system**: Queen on `d4`.
    - **Expected output**: Returned list contains exactly 27 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorSquare_returnsTwentySevenCandidates`

- **TC8: Queen at Minimum-File Edge — Left Candidates Absent**
    - **State of the system**: Queen on `a4`.
    - **Expected output**: Returned list contains right, vertical, and right-diagonal candidates; contains no out-of-bounds files; contains exactly 21 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_fileA_interiorRank_returnsTwentyOneCandidates`

- **TC9: Queen at Maximum-File Edge — Right Candidates Absent**
    - **State of the system**: Queen on `h4`.
    - **Expected output**: Returned list contains left, vertical, and left-diagonal candidates; contains no out-of-bounds files; contains exactly 21 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_fileH_interiorRank_returnsTwentyOneCandidates`

- **TC10: Queen at Minimum-Rank Edge — Downward Candidates Absent**
    - **State of the system**: Queen on `d1`.
    - **Expected output**: Returned list contains horizontal, upward, and upward-diagonal candidates; contains no out-of-bounds ranks; contains exactly 21 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_rank1_interiorFile_returnsTwentyOneCandidates`

- **TC11: Queen at Maximum-Rank Edge — Upward Candidates Absent**
    - **State of the system**: Queen on `d8`.
    - **Expected output**: Returned list contains horizontal, downward, and downward-diagonal candidates; contains no out-of-bounds ranks; contains exactly 21 candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_rank8_interiorFile_returnsTwentyOneCandidates`

- **TC12: Queen at Corner `a1` — Minimum Corner Candidate Count**
    - **State of the system**: Queen on `a1`.
    - **Expected output**: Returned list contains candidates along rank `1`, file `a`, and the `a1`–`h8` diagonal, for exactly 21 candidates.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerA1_returnsTwentyOneCandidates`

- **TC13: Queen at Corner `a8` — Minimum Corner Candidate Count**
    - **State of the system**: Queen on `a8`.
    - **Expected output**: Returned list contains candidates along rank `8`, file `a`, and the `a8`–`h1` diagonal, for exactly 21 candidates.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerA8_returnsTwentyOneCandidates`

- **TC14: Queen at Corner `h1` — Minimum Corner Candidate Count**
    - **State of the system**: Queen on `h1`.
    - **Expected output**: Returned list contains candidates along rank `1`, file `h`, and the `h1`–`a8` diagonal, for exactly 21 candidates.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerH1_returnsTwentyOneCandidates`

- **TC15: Queen at Corner `h8` — Minimum Corner Candidate Count**
    - **State of the system**: Queen on `h8`.
    - **Expected output**: Returned list contains candidates along rank `8`, file `h`, and the `h8`–`a1` diagonal, for exactly 21 candidates.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerH8_returnsTwentyOneCandidates`

- **TC16: `from` Square Is Not A Candidate**
    - **State of the system**: Queen on `d4`.
    - **Expected output**: Returned list does not contain `d4`.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorSquare_excludesFromSquare`

- **TC17: All Returned Squares Are Within Board Bounds**
    - **State of the system**: Queen on `a1`.
    - **Expected output**: Every returned square has file within `'a'`–`'h'` and rank within `1`–`8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerA1_allCandidatesInBounds`

- **TC18: Queen Movement Does Not Depend On Color**
    - **State of the system**: White Queen and Black Queen are each placed on `d4`.
    - **Expected output**: Both returned lists contain the same number of candidate squares and the same representative straight and diagonal candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_colorDoesNotAffectCandidates`

- **TC19: Queen Movement Does Not Depend On `hasMoved`**
    - **State of the system**: One Queen has `hasMoved = false`; another Queen has `hasMoved = true`; both are placed on `d4`.
    - **Expected output**: Both returned lists contain the same number of candidate squares and the same representative straight and diagonal candidate squares.
    - **Implemented at**: `getLegalMoveDestinationSquares_hasMovedDoesNotAffectCandidates`