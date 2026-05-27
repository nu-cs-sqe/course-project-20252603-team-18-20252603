# knight.md

## Intermediate Analysis (Steps 1–3)

**Input Domains & Variables:**

* `from` (Square): The square currently occupied by this `Knight` instance.
    * Valid values: Any valid, non-null `Square` with file `'a'`–`'h'` and rank `1`–`8`, whose `occupant` is this `Knight`.
    * Invalid values: `null`; a `Square` whose `occupant` is not this `Knight`; a `Square` with out-of-bounds coordinates.

* `color` (Color) — derived from `this.getColor()`: Does **not** affect the geometry of candidate squares for a Knight. Both colors produce the same eight L-shaped jump offsets. Color is validated at construction time via `Piece.requireColor` and cannot be `null` at `getLegalMoveDestinationSquares` call time.

* `from.file` (char) — the file of the `from` square: Together with `from.rank`, determines how many of the eight L-shaped jump targets fall within board bounds.
    * Valid values: `'a'`–`'h'`.
    * Boundary values:
        * `'a'` (minimum file): All offsets with a negative file delta (`-1` or `-2`) produce out-of-bounds candidates and are excluded.
        * `'b'` (one inside the minimum): Offsets with a `-2` file delta are excluded; offsets with a `-1` file delta reach file `'a'` and remain valid.
        * `'g'` (one inside the maximum): Offsets with a `+2` file delta are excluded; offsets with a `+1` file delta reach file `'h'` and remain valid.
        * `'h'` (maximum file): All offsets with a positive file delta (`+1` or `+2`) produce out-of-bounds candidates and are excluded.
        * `'c'`–`'f'` (fully interior files): All file deltas (`±1`, `±2`) remain within bounds — candidates are only clipped by rank boundaries.

* `from.rank` (int) — the rank of the `from` square: Together with `from.file`, determines how many of the eight L-shaped jump targets fall within board bounds.
    * Valid values: `1`–`8`.
    * Boundary values:
        * `1` (minimum rank): All offsets with a negative rank delta (`-1` or `-2`) are excluded.
        * `2` (one inside the minimum): Offsets with a `-2` rank delta are excluded; offsets with a `-1` rank delta reach rank `1` and remain valid.
        * `7` (one inside the maximum): Offsets with a `+2` rank delta reach rank `9` and are excluded; offsets with a `+1` rank delta reach rank `8` and remain valid.
        * `8` (maximum rank): All offsets with a positive rank delta (`+1` or `+2`) are excluded.
        * `3`–`6` (fully interior ranks): All rank deltas (`±1`, `±2`) remain within bounds — candidates are only clipped by file boundaries.

**Candidate Count by Board Region:**

The total number of in-bounds L-shaped jump candidates depends on the interaction between file and rank boundaries. The Knight's eight offset vectors are: `(±1, ±2)` and `(±2, ±1)`.

* **Corner squares** — `a1`, `a8`, `h1`, `h8`: **2 candidates each.**
    * Both the file and rank boundaries clip the jump set heavily. Only the two offsets that move away from both edges survive.
    * `a1` → `b3`, `c2`.

* **Near-corner edge squares** — `a2`, `a7`, `b1`, `g1`, `b8`, `g8`, `h2`, `h7`: **3 candidates each.**
    * One axis is at the extreme boundary, the other is one step inside. One additional offset survives compared to a corner.
    * `a2` → `b4`, `c3`, `c1`.
    * `b1` → `a3`, `c3`, `d2`.

* **Outer-ring non-corner squares** — all squares on the `a`/`h` files (ranks `3`–`6`) and the `1`/`8` ranks (files `c`–`f`), plus `b2`, `b7`, `g2`, `g7`: **4 candidates each.**
    * One axis is at the extreme boundary, the other is fully interior; or both axes are one step inside the boundary simultaneously (`b2`, `g2`, `b7`, `g7`).
    * `a4` → `b6`, `b2`, `c5`, `c3`.
    * `b2` → `a4`, `c4`, `d3`, `d1`.

* **Second-ring squares** — `b`/`g` files (ranks `3`–`6`) and `c`/`f` files (ranks `2` and `7`): **6 candidates each.**
    * One axis is one step inside the boundary, the other is fully interior. Two offsets are clipped.
    * `b4` → `a2`, `a6`, `c2`, `c6`, `d3`, `d5`.

* **Fully interior squares** — files `c`–`f`, ranks `3`–`6`: **8 candidates each.**
    * No boundary clips any of the eight offsets. All jump targets are within board bounds.
    * `d4` → `b3`, `b5`, `c2`, `c6`, `e2`, `e6`, `f3`, `f5`.

**Boundary Values Identified:**

* `from` Boundaries:
    * Valid state: Non-null `Square` with file `'a'`–`'h'`, rank `1`–`8`, occupied by this `Knight`.
    * Invalid state: `null`; square not occupied by this `Knight`; out-of-bounds coordinates.

* File Boundaries (number of reachable candidates):
    * Minimum file `'a'`: Left-side offsets (`df = -1`, `df = -2`) always out-of-bounds; 2–4 candidates depending on rank.
    * One inside minimum `'b'`: Offsets with `df = -2` out-of-bounds; offsets with `df = -1` reach file `'a'`; 3–6 candidates depending on rank.
    * One inside maximum `'g'`: Offsets with `df = +2` out-of-bounds; offsets with `df = +1` reach file `'h'`; 3–6 candidates depending on rank.
    * Maximum file `'h'`: Right-side offsets (`df = +1`, `df = +2`) always out-of-bounds; 2–4 candidates depending on rank.
    * Interior files `'c'`–`'f'`: All file deltas produce in-bounds file targets; candidate count is determined by rank boundary alone.

* Rank Boundaries (number of reachable candidates):
    * Minimum rank `1`: Downward offsets (`dr = -1`, `dr = -2`) always out-of-bounds; 2–4 candidates depending on file.
    * One inside minimum `2`: Offsets with `dr = -2` out-of-bounds; offsets with `dr = -1` reach rank `1`; 3–6 candidates depending on file.
    * One inside maximum `7`: Offsets with `dr = +2` out-of-bounds; offsets with `dr = +1` reach rank `8`; 3–6 candidates depending on file.
    * Maximum rank `8`: Upward offsets (`dr = +1`, `dr = +2`) always out-of-bounds; 2–4 candidates depending on file.
    * Interior ranks `3`–`6`: All rank deltas produce in-bounds rank targets; candidate count is determined by file boundary alone.

---

## Step 4: Test Cases

### Method under test: `Knight.getLegalMoveDestinationSquares(Square from)`

---

- **TC1: Null From Square**
    - **State of the system**: `from = null`.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_nullFrom_throwsException`

---

- **TC2: From Square Not Occupied By This Knight**
    - **State of the system**: `from` is a valid square whose `occupant` is a different `Piece` (or `null`), not this `Knight` instance.
    - **Expected output**: Throws `IllegalArgumentException`.
    - **Implemented at**: `getLegalMoveDestinationSquares_fromNotOccupiedByThisKnight_throwsException`

---

- **TC3: Fully Interior Square — All Eight Candidates Generated**
    - **State of the system**: `Knight` on `d4` (file `'d'`, rank `4` — fully interior).
    - **Expected output**: Returned list contains exactly 8 squares: `b3`, `b5`, `c2`, `c6`, `e2`, `e6`, `f3`, `f5`. No out-of-bounds square is present.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorSquare_returnsAllEightCandidates`

---

- **TC4: Corner Square — Minimum File, Minimum Rank (`a1`) — Two Candidates**
    - **State of the system**: `Knight` on `a1` (minimum file `'a'`, minimum rank `1`).
    - **Expected output**: Returned list contains exactly 2 squares: `b3`, `c2`. All returned squares have file within `'a'`–`'h'` and rank within `1`–`8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerA1_returnsTwoCandidates`

---

- **TC5: Corner Square — Minimum File, Maximum Rank (`a8`) — Two Candidates**
    - **State of the system**: `Knight` on `a8` (minimum file `'a'`, maximum rank `8`).
    - **Expected output**: Returned list contains exactly 2 squares: `b6`, `c7`. All returned squares are within board bounds.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerA8_returnsTwoCandidates`

---

- **TC6: Corner Square — Maximum File, Minimum Rank (`h1`) — Two Candidates**
    - **State of the system**: `Knight` on `h1` (maximum file `'h'`, minimum rank `1`).
    - **Expected output**: Returned list contains exactly 2 squares: `g3`, `f2`. All returned squares are within board bounds.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerH1_returnsTwoCandidates`

---

- **TC7: Corner Square — Maximum File, Maximum Rank (`h8`) — Two Candidates**
    - **State of the system**: `Knight` on `h8` (maximum file `'h'`, maximum rank `8`).
    - **Expected output**: Returned list contains exactly 2 squares: `g6`, `f7`. All returned squares are within board bounds.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerH8_returnsTwoCandidates`

---

- **TC8: Near-Corner — Minimum File, One Inside Minimum Rank (`a2`) — Three Candidates**
    - **State of the system**: `Knight` on `a2` (minimum file `'a'`, rank `2` — one step inside the minimum rank boundary).
    - **Expected output**: Returned list contains exactly 3 squares: `b4`, `c3`, `c1`. All returned squares are within board bounds.
    - **Implemented at**: `getLegalMoveDestinationSquares_nearCornerA2_returnsThreeCandidates`

---

- **TC9: Near-Corner — One Inside Minimum File, Minimum Rank (`b1`) — Three Candidates**
    - **State of the system**: `Knight` on `b1` (file `'b'` — one step inside the minimum file boundary, minimum rank `1`).
    - **Expected output**: Returned list contains exactly 3 squares: `a3`, `c3`, `d2`. All returned squares are within board bounds.
    - **Implemented at**: `getLegalMoveDestinationSquares_nearCornerB1_returnsThreeCandidates`

---

- **TC10: One Inside Each Boundary — Minimum-Side (`b2`) — Four Candidates**
    - **State of the system**: `Knight` on `b2` (file `'b'` — one inside minimum file; rank `2` — one inside minimum rank).
    - **Expected output**: Returned list contains exactly 4 squares: `a4`, `c4`, `d3`, `d1`. All returned squares are within board bounds.
    - **Implemented at**: `getLegalMoveDestinationSquares_oneInsideEachMinBoundaryB2_returnsFourCandidates`

---

- **TC11: One Inside Each Boundary — Maximum-Side (`g7`) — Four Candidates**
    - **State of the system**: `Knight` on `g7` (file `'g'` — one inside maximum file; rank `7` — one inside maximum rank).
    - **Expected output**: Returned list contains exactly 4 squares: `h5`, `f5`, `e6`, `e8`. All returned squares are within board bounds.
    - **Implemented at**: `getLegalMoveDestinationSquares_oneInsideEachMaxBoundaryG7_returnsFourCandidates`

---

- **TC12: Minimum File, Interior Rank (`a4`) — Four Candidates**
    - **State of the system**: `Knight` on `a4` (minimum file `'a'`, interior rank `4`).
    - **Expected output**: Returned list contains exactly 4 squares: `b6`, `c5`, `c3`, `b2`. No square has a file less than `'a'`.
    - **Implemented at**: `getLegalMoveDestinationSquares_minFileInteriorRankA4_returnsFourCandidates`

---

- **TC13: Maximum File, Interior Rank (`h5`) — Four Candidates**
    - **State of the system**: `Knight` on `h5` (maximum file `'h'`, interior rank `5`).
    - **Expected output**: Returned list contains exactly 4 squares: `g7`, `f6`, `f4`, `g3`. No square has a file greater than `'h'`.
    - **Implemented at**: `getLegalMoveDestinationSquares_maxFileInteriorRankH5_returnsFourCandidates`

---

- **TC14: Interior File, Minimum Rank (`d1`) — Four Candidates**
    - **State of the system**: `Knight` on `d1` (interior file `'d'`, minimum rank `1`).
    - **Expected output**: Returned list contains exactly 4 squares: `b2`, `c3`, `e3`, `f2`. No square has a rank less than `1`.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorFileMinRankD1_returnsFourCandidates`

---

- **TC15: Interior File, Maximum Rank (`e8`) — Four Candidates**
    - **State of the system**: `Knight` on `e8` (interior file `'e'`, maximum rank `8`).
    - **Expected output**: Returned list contains exactly 4 squares: `c7`, `d6`, `f6`, `g7`. No square has a rank greater than `8`.
    - **Implemented at**: `getLegalMoveDestinationSquares_interiorFileMaxRankE8_returnsFourCandidates`

---

- **TC16: Second-Ring Square — One Inside Minimum File, Interior Rank (`b4`) — Six Candidates**
    - **State of the system**: `Knight` on `b4` (file `'b'` — one inside minimum file; interior rank `4`).
    - **Expected output**: Returned list contains exactly 6 squares: `a2`, `a6`, `c2`, `c6`, `d3`, `d5`. No out-of-bounds square generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_secondRingB4_returnsSixCandidates`

---

- **TC17: Second-Ring Square — Interior File, One Inside Minimum Rank (`d2`) — Six Candidates**
    - **State of the system**: `Knight` on `d2` (interior file `'d'`; rank `2` — one inside minimum rank).
    - **Expected output**: Returned list contains exactly 6 squares: `b1`, `b3`, `c4`, `e4`, `f1`, `f3`. No out-of-bounds square generated.
    - **Implemented at**: `getLegalMoveDestinationSquares_secondRingD2_returnsSixCandidates`

---

- **TC18: No Candidate Matches the `from` Square Itself**
    - **State of the system**: `Knight` on `d4` (interior square).
    - **Expected output**: Returned list does not contain `d4`. (The Knight cannot land on its own square.)
    - **Implemented at**: `getLegalMoveDestinationSquares_interior_doesNotContainFromSquare`

---

- **TC19: Color Does Not Affect Candidate Set — White vs. Black Knight on Same Square**
    - **State of the system**: White `Knight` on `d4`; separately, Black `Knight` on `d4`.
    - **Expected output**: Both returned lists contain the same 8 squares. Color has no bearing on the geometric candidates.
    - **Implemented at**: `getLegalMoveDestinationSquares_colorDoesNotAffectCandidates`

---

- **TC20: All Returned Squares Are Within Board Bounds — Corner Stress Check**
    - **State of the system**: `Knight` on `a1` (worst-case corner where the most offsets fall out of bounds).
    - **Expected output**: Every square in the returned list has file within `'a'`–`'h'` and rank within `1`–`8`. No negative-rank or pre-`'a'`-file square is present.
    - **Implemented at**: `getLegalMoveDestinationSquares_cornerA1_allCandidatesWithinBounds`