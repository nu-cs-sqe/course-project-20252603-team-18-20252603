# RulesEngine Boundary Value Analysis (BVA)

## Intermediate Analysis (Steps 1–3)

### Input Domains & Variables

#### `state` / `gameState` (`GameState`)

The complete current state of the chess game.

**Valid values:**

* A non-null `GameState` containing:

    * a non-null `Board`
    * valid `currentTurn`
    * both kings present
    * legal piece placement
    * move history, possibly empty
    * castling rights
    * en passant target, possibly null

**Invalid values:**

* `null`
* `GameState` with null `Board`
* `GameState` with missing white King
* `GameState` with missing black King
* `GameState` with invalid `currentTurn`
* `GameState` with impossible board coordinates

---

#### `board` (`Board`)

The chess board used by the rules engine.

**Valid values:**

* Non-null 8x8 board
* Squares from file `'a'` through `'h'`
* Ranks from `1` through `8`
* Each square contains either `null` or one `Piece`

**Invalid values:**

* `null`
* Board with fewer or more than 8 files
* Board with fewer or more than 8 ranks
* Board containing out-of-bounds squares
* Board with duplicate kings of the same color
* Board with pieces placed outside `'a'`–`'h'` or `1`–`8`

**Boundary values:**

* Minimum valid file: `'a'`
* Maximum valid file: `'h'`
* Invalid file below minimum: `` ` ``
* Invalid file above maximum: `'i'`
* Minimum valid rank: `1`
* Maximum valid rank: `8`
* Invalid rank below minimum: `0`
* Invalid rank above maximum: `9`

---

#### `from` (`Square`)

The square from which a move begins.

**Valid values:**

* Non-null `Square`
* File between `'a'` and `'h'`
* Rank between `1` and `8`
* Occupied by a piece whose color matches `currentTurn`

**Invalid values:**

* `null`
* Out-of-bounds square
* Empty square
* Square occupied by opponent piece

**Boundary values:**

* Valid corner squares: `a1`, `a8`, `h1`, `h8`
* Invalid near-boundary squares: `` `1 ``, `i1`, `a0`, `a9`

---

#### `to` (`Square`)

The destination square of a candidate move.

**Valid values:**

* Non-null `Square`
* File between `'a'` and `'h'`
* Rank between `1` and `8`
* Not the same square as `from`
* Empty square or occupied by opponent piece
* Reachable by the moving piece according to chess rules

**Invalid values:**

* `null`
* Out-of-bounds square
* Same square as `from`
* Occupied by same-color piece
* Unreachable according to piece movement rules

**Boundary values:**

* Valid destination corners: `a1`, `a8`, `h1`, `h8`
* Invalid just outside board: `` `1 ``, `i1`, `a0`, `a9`

---

#### `move` (`Move`)

The candidate move being validated.

**Valid values:**

* Non-null `Move`
* Contains non-null `piece`, `from`, and `to`
* `from` is occupied by `move.piece`
* `to` is different from `from`
* Special move flags are consistent with the piece and position

**Invalid values:**

* `null`
* Move with null `piece`
* Move with null `from`
* Move with null `to`
* Move where `from == to`
* Move where `piece` does not match `from.occupant`
* Move where destination contains same-color piece
* Move that captures a King
* Move that leaves own King in check

---

#### `color` (`Color`)

The side being evaluated.

**Valid values:**

* `WHITE`
* `BLACK`

**Invalid values:**

* `null`
* Any non-enum color value

---

#### `piece` (`Piece`)

The piece whose movement is being validated.

**Valid values:**

* `King`
* `Queen`
* `Rook`
* `Bishop`
* `Knight`
* `Pawn`

**Invalid values:**

* `null`
* Piece not present on the board
* Piece on a square inconsistent with `from`

---

#### `currentTurn` (`Color`)

The side whose turn it is.

**Valid values:**

* `WHITE`
* `BLACK`

**Invalid values:**

* `null`

**Constraints:**

* A legal move must move a piece whose color equals `currentTurn`.
* Moving the opponent's piece must be rejected.

---

#### `castlingRights`

Tracks whether each side can castle.

**Valid values:**

* White can castle kingside
* White can castle queenside
* Black can castle kingside
* Black can castle queenside
* Any combination of the above
* No castling rights

**Invalid values:**

* Castling allowed even though King has moved
* Castling allowed even though involved Rook has moved
* Castling allowed when involved Rook is missing
* Castling allowed through check
* Castling allowed while King is currently in check

**Boundary cases:**

* King on original square and Rook on original square
* King moved exactly once
* Rook moved exactly once
* One square between King and Rook blocked
* All squares between King and Rook clear
* One castling path square attacked
* No castling path squares attacked

---

#### `enPassantTarget` (`Square`)

The square available for en passant capture.

**Valid values:**

* `null` when no en passant is available
* Valid square on rank `3` or rank `6` after a two-square pawn move

**Invalid values:**

* Out-of-bounds square
* Square not on rank `3` or rank `6`
* Stale en passant target from more than one move ago
* En passant target when previous move was not a two-square pawn move

**Boundary values:**

* Valid white en passant target rank: `6`
* Valid black en passant target rank: `3`
* Invalid ranks: `2`, `4`, `5`, `7`

---

#### `moveHistory` (`Move[]`)

The list of prior moves.

**Valid values:**

* Empty list at start of game
* Non-empty list of valid completed moves

**Invalid values:**

* `null`, if implementation requires an empty list instead
* List containing null moves
* List containing moves inconsistent with the current board

**Boundary values:**

* `0` moves
* `1` move
* Previous move was a two-square pawn move
* Previous move was not a pawn move
* Previous move was a pawn move but not two squares

---

#### `promotionPiece` (`PieceType`)

The selected promotion piece for a pawn reaching the final rank.

**Valid values:**

* `QUEEN`
* `ROOK`
* `BISHOP`
* `KNIGHT`
* `null` when no promotion is required

**Invalid values:**

* `KING`
* `PAWN`
* Non-null `promotionPiece` when moving piece is not a Pawn
* Null `promotionPiece` when a Pawn reaches the final rank and promotion is required

**Boundary values:**

* White promotion rank: `8`
* Black promotion rank: `1`
* Just before promotion: white pawn on rank `7`, black pawn on rank `2`
* Promotion destination: white pawn moves to rank `8`, black pawn moves to rank `1`

---

## Boundary Values Identified

### Board Coordinate Boundaries

* Valid files:

    * Minimum: `'a'`
    * Maximum: `'h'`
* Invalid files:

    * Below minimum: `` ` ``
    * Above maximum: `'i'`
* Valid ranks:

    * Minimum: `1`
    * Maximum: `8`
* Invalid ranks:

    * Below minimum: `0`
    * Above maximum: `9`

---

### Piece Movement Boundaries

#### King

**Valid:**

* Moves exactly 1 square in any direction
* Castles exactly 2 squares horizontally under valid castling conditions

**Invalid:**

* Moves 0 squares
* Moves more than 1 square, except castling
* Moves into check
* Castles while in check
* Castles through check
* Castles into check

#### Queen

**Valid:**

* Moves any number of clear squares horizontally
* Moves any number of clear squares vertically
* Moves any number of clear squares diagonally

**Invalid:**

* Non-straight and non-diagonal movement
* Path blocked

#### Rook

**Valid:**

* Moves horizontally
* Moves vertically

**Invalid:**

* Diagonal movement
* Path blocked

#### Bishop

**Valid:**

* Moves diagonally

**Invalid:**

* Horizontal movement
* Vertical movement
* Path blocked

#### Knight

**Valid:**

* Moves in 2-by-1 or 1-by-2 L-shape
* May jump over pieces

**Invalid:**

* Any non-L-shaped move

#### Pawn

**Valid:**

* Moves forward 1 square if empty
* Moves forward 2 squares from starting rank if both squares are empty
* Captures diagonally forward 1 square
* Captures en passant when immediately available
* Promotes on final rank

**Invalid:**

* Moves backward
* Moves sideways without en passant
* Moves forward into occupied square
* Moves two squares after leaving starting rank
* Captures forward
* Promotes to King or Pawn

---

### Check and Checkmate Boundaries

**Valid:**

* King not attacked → not in check
* King attacked by at least one opponent piece → in check
* King in check and no legal moves → checkmate
* King not in check and no legal moves → stalemate

**Invalid:**

* Move that leaves own King in check
* Move that captures opponent King
* Checkmate reported when King is not in check
* Stalemate reported when King is in check

---

# Step 4: Test Cases

## Method under test: `isLegalMove(Move move, GameModel model)`

- **TC1: Move is null** ( :x: )
  - **State of the system**: System calls `isLegalMove(...)` with `move = null`, and a valid `GameModel`.
  - **Expected output**: Move is rejected because there is no move to validate; method returns `false`.
  - **Test name**: `isLegalMove_nullMove_returnsFalse`

- **TC2: Game model is null** ( :x: )
  - **State of the system**: System calls `isLegalMove(...)` with a valid `Move`, but `model = null`.
  - **Expected output**: Move is rejected because board and game state cannot be accessed; method returns `false`.
  - **Test name**: `isLegalMove_nullModel_returnsFalse`

- **TC3: Source square exists but contains no piece** ( :x: )
  - **State of the system**: `model.getBoard().getSquare(from.getFile(), from.getRank() + 1)` returns a valid square, but that square has no piece.
  - **Expected output**: Move is rejected because there is no piece to move; method returns `false`.
  - **Test name**: `isLegalMove_emptyFromSquare_returnsFalse`

- **TC4: Source square contains opponent piece** ( :x: )
  - **State of the system**: The source square contains a piece whose color does not match the current player.
  - **Expected output**: Move is rejected because the player cannot move the opponent’s piece; method returns `false`.
  - **Test name**: `isLegalMove_fromSquareHasOpponentPiece_returnsFalse`

- **TC5: Destination contains own piece** ( :x: )
  - **State of the system**: Source square contains a current-player piece and the destination square contains another piece of the same color.
  - **Expected output**: Move is rejected because a piece cannot move onto its own piece; method returns `false`.
  - **Test name**: `isLegalMove_destinationHasOwnPiece_returnsFalse`

- **TC6: Valid move to empty destination** ( :x: )
  - **state of the system**: Source square contains a current-player piece and the destination square does not contain another piece
  - **Expected output**: Move is accepted as a valid move; method returns `true`.
  - **Test name**: `isLegalMove_emptyDestinationSquare_validMove`

- **TC7: Destination contains opponent piece** ( :white_check_mark: )
  - **State of the system**: Source square contains a current-player piece and the destination square contains an opponent piece. The move is otherwise legal for that piece.
  - **Expected output**: Move is accepted as a valid capture; method returns `true`.
  - **Test name**: `isLegalMove_destinationHasOpponentPiece_validCapture`

- **TC8: Sliding piece path is blocked before destination** ( :x: )
  - **State of the system**: Source square contains a rook, bishop, or queen. The destination is in the generated destination list, but another piece is located between the source and destination.
  - **Expected output**: Move is rejected because sliding pieces cannot jump over blockers; method returns `false`.
  - **Test name**: `isLegalMove_slidingPathBlocked_returnsFalse`

- **TC9: Sliding piece path is clear** ( :white_check_mark: )
  - **State of the system**: Source square contains a rook, bishop, or queen. The destination is in the generated destination list, and all squares between source and destination are empty.
  - **Expected output**: Move is accepted if all other validations pass; method returns `true`.
  - **Test name**: `isLegalMove_slidingPathClear_validMove`

- **TC10: Move exposes own king to check** ( :x: )
  - **State of the system**: Source square contains a current-player piece that is currently blocking an opponent attack line. Moving that piece would leave the current player’s king in check.
  - **Expected output**: Move is rejected because a player cannot make a move that leaves their own king in check; method returns `false`.
  - **Test name**: `isLegalMove_moveExposesOwnKing_returnsFalse`

- **TC11: Move keeps own king safe** ( :white_check_mark: )
  - **State of the system**: Source square contains a current-player piece. After the move is hypothetically applied, the current player’s king is not in check.
  - **Expected output**: Move is accepted if all other validations pass; method returns `true`.
  - **Test name**: `isLegalMove_moveKeepsKingSafe_validMove`

---

## Special move delegation

- **TC12: Castling helper returns true** ( :white_check_mark: )
  - **State of the system**: Source square contains the current player’s piece. `isCastlingLegal(move, model)` returns `true`.
  - **Expected output**: Move is accepted as legal castling; method returns `true`.
  - **Test name**: `isLegalMove_castlingHelperTrue_returnsTrue`
  - 
- **TC13: Promotion helper returns true** ( :white_check_mark: )
  - **State of the system**: Source square contains the current player’s pawn. `isPromotionLegal(move, model)` returns `true`.
  - **Expected output**: Move is accepted as legal promotion; method returns `true`.
  - **Test name**: `isLegalMove_promotionHelperTrue_returnsTrue`
  - 
- **TC14: En passant helper returns true** ( :white_check_mark: )
  - **State of the system**: Source square contains the current player’s pawn. `isEnpassantLegal(move, model)` returns `true`.
  - **Expected output**: Move is accepted as legal en passant; method returns `true`.
  - **Test name**: `isLegalMove_enPassantHelperTrue_returnsTrue`---

## Method under test: `RulesEngine.getLegalMoves(GameState state, Square from)`

### TC34: Legal Moves From Null State

* **State of the system**: `state = null`, valid `from`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `getLegalMoves_nullState_throwsException`

### TC35: Legal Moves From Null Square

* **State of the system**: Valid `GameState`, `from = null`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `getLegalMoves_nullFrom_throwsException`

### TC36: Legal Moves From Empty Square

* **State of the system**: Valid board, `from = e4`, no piece on `e4`.
* **Expected output**: Returns empty list.
* **Implemented at**: `getLegalMoves_emptySquare_returnsEmptyList`

### TC37: Legal Moves For Opponent Piece

* **State of the system**: `currentTurn = WHITE`, black bishop on `c8`.
* **Expected output**: Returns empty list.
* **Implemented at**: `getLegalMoves_opponentPiece_returnsEmptyList`

### TC38: Legal Moves At Corner Minimum Boundary

* **State of the system**: White rook on `a1`, clear rank and file.
* **Expected output**: Returns legal moves along file `a` and rank `1`, all within board bounds.
* **Implemented at**: `getLegalMoves_pieceAtA1_staysWithinBounds`

### TC39: Legal Moves At Corner Maximum Boundary

* **State of the system**: White rook on `h8`, clear rank and file.
* **Expected output**: Returns legal moves along file `h` and rank `8`, all within board bounds.
* **Implemented at**: `getLegalMoves_pieceAtH8_staysWithinBounds`

### TC40: Pinned Piece Has Restricted Legal Moves

* **State of the system**: White king on `e1`, white rook on `e2`, black rook on `e8`.
* **Expected output**: Rook legal moves exclude moves that expose the White king.
* **Implemented at**: `getLegalMoves_pinnedPiece_excludesIllegalMoves`

### TC41: King In Check Only Returns Check-Escaping Moves

* **State of the system**: White king on `e1`, black rook on `e8`, and White is in check.
* **Expected output**: Returns only moves that remove check.
* **Implemented at**: `getLegalMoves_whenInCheck_returnsOnlyEscapeMoves`

---

## Method under test: `RulesEngine.isInCheck(GameState state, Color color)`

### TC42: King Not In Check

* **State of the system**: White king on `e1`, black pieces do not attack `e1`.
* **Expected output**: Returns `false`.
* **Implemented at**: `isInCheck_kingNotAttacked_returnsFalse`

### TC43: King In Check By Rook

* **State of the system**: White king on `e1`, black rook on `e8`, clear path between them.
* **Expected output**: Returns `true`.
* **Implemented at**: `isInCheck_attackedByRook_returnsTrue`

### TC44: King In Check By Bishop

* **State of the system**: White king on `e1`, black bishop on `h4`, clear diagonal path.
* **Expected output**: Returns `true`.
* **Implemented at**: `isInCheck_attackedByBishop_returnsTrue`

### TC45: King In Check By Knight

* **State of the system**: White king on `e1`, black knight on `f3`.
* **Expected output**: Returns `true`.
* **Implemented at**: `isInCheck_attackedByKnight_returnsTrue`

### TC46: King In Check By Pawn

* **State of the system**: White king on `e4`, black pawn on `d5`.
* **Expected output**: Returns `true`.
* **Implemented at**: `isInCheck_attackedByPawn_returnsTrue`

### TC47: King In Check By Adjacent King

* **State of the system**: White king on `e4`, black king on `e5`.
* **Expected output**: Returns `true` or invalid board state, depending on implementation.
* **Implemented at**: `isInCheck_attackedByAdjacentKing_returnsTrue`

### TC48: Blocked Sliding Attack Does Not Count As Check

* **State of the system**: White king on `e1`, black rook on `e8`, white bishop on `e4` blocks the path.
* **Expected output**: Returns `false`.
* **Implemented at**: `isInCheck_blockedSlidingAttack_returnsFalse`

### TC49: Null Color

* **State of the system**: Valid `GameState`, `color = null`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `isInCheck_nullColor_throwsException`

### TC50: Missing King

* **State of the system**: Board contains no White king, method checks `WHITE`.
* **Expected output**: Throws `IllegalStateException` or returns invalid state error.
* **Implemented at**: `isInCheck_missingKing_throwsException`

---

## Method under test: `RulesEngine.isSquareAttacked(GameState state, Square square, Color byColor)`

### TC51: Square Attacked By Knight

* **State of the system**: Black knight on `f3`, target square `e1`.
* **Expected output**: Returns `true`.
* **Implemented at**: `isSquareAttacked_byKnight_returnsTrue`

### TC52: Square Not Attacked

* **State of the system**: Target square `e4`, no opponent piece attacks it.
* **Expected output**: Returns `false`.
* **Implemented at**: `isSquareAttacked_noAttackers_returnsFalse`

### TC53: Attacked Square At Lower Boundary

* **State of the system**: Target square `a1`, black bishop on `b2`.
* **Expected output**: Returns `true`.
* **Implemented at**: `isSquareAttacked_targetA1_returnsTrue`

### TC54: Attacked Square At Upper Boundary

* **State of the system**: Target square `h8`, black bishop on `g7`.
* **Expected output**: Returns `true`.
* **Implemented at**: `isSquareAttacked_targetH8_returnsTrue`

### TC55: Null Target Square

* **State of the system**: Valid `GameState`, `square = null`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `isSquareAttacked_nullSquare_throwsException`

### TC56: Null Attacking Color

* **State of the system**: Valid `GameState`, `byColor = null`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `isSquareAttacked_nullColor_throwsException`

---

## Method under test: `RulesEngine.isCheckmate(GameState state, Color color)`

### TC57: Checkmate Position

* **State of the system**: King is in check and has no legal move, no piece can block, and no piece can capture the
  attacker.
* **Expected output**: Returns `true`.
* **Implemented at**: `isCheckmate_kingInCheckNoLegalMoves_returnsTrue`

### TC58: In Check But Can Move King

* **State of the system**: King is in check, but at least one safe king move exists.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCheckmate_kingCanEscape_returnsFalse`

### TC59: In Check But Can Block

* **State of the system**: King is in rook, bishop, or queen line check, and friendly piece can block the attack.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCheckmate_checkCanBeBlocked_returnsFalse`

### TC60: In Check But Can Capture Attacker

* **State of the system**: King or friendly piece can legally capture the checking piece.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCheckmate_attackerCanBeCaptured_returnsFalse`

### TC61: Not In Check

* **State of the system**: King is not attacked.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCheckmate_notInCheck_returnsFalse`

---

## Method under test: `RulesEngine.isStalemate(GameState state, Color color)`

### TC62: Stalemate Position

* **State of the system**: Player is not in check, but has no legal moves.
* **Expected output**: Returns `true`.
* **Implemented at**: `isStalemate_noCheckNoLegalMoves_returnsTrue`

### TC63: No Legal Moves But In Check

* **State of the system**: Player has no legal moves, but King is in check.
* **Expected output**: Returns `false`; this is checkmate, not stalemate.
* **Implemented at**: `isStalemate_inCheck_returnsFalse`

### TC64: Has At Least One Legal Move

* **State of the system**: Player is not in check and has at least one legal move.
* **Expected output**: Returns `false`.
* **Implemented at**: `isStalemate_hasLegalMove_returnsFalse`

---

## Method under test: `RulesEngine.isCastlingLegal(GameState state, Color color, CastleSide side)`

### TC65: Valid White Kingside Castling

* **State of the system**: White king on `e1`, white rook on `h1`, neither has moved, squares `f1` and `g1` are empty,
  and `e1`, `f1`, `g1` are not attacked.
* **Expected output**: Returns `true`.
* **Implemented at**: `isCastlingLegal_whiteKingsideValid_returnsTrue`

### TC66: Valid White Queenside Castling

* **State of the system**: White king on `e1`, white rook on `a1`, neither has moved, squares `b1`, `c1`, and `d1` are
  empty, and `e1`, `d1`, `c1` are not attacked.
* **Expected output**: Returns `true`.
* **Implemented at**: `isCastlingLegal_whiteQueensideValid_returnsTrue`

### TC67: Invalid Castling Because King Has Moved

* **State of the system**: White king on `e1`, white rook on `h1`, but King has previously moved.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCastlingLegal_kingMoved_returnsFalse`

### TC68: Invalid Castling Because Rook Has Moved

* **State of the system**: White king on `e1`, white rook on `h1`, but rook has previously moved.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCastlingLegal_rookMoved_returnsFalse`

### TC69: Invalid Castling Because Path Is Blocked

* **State of the system**: White king on `e1`, white rook on `h1`, piece on `f1`.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCastlingLegal_pathBlocked_returnsFalse`

### TC70: Invalid Castling While In Check

* **State of the system**: White king on `e1` is currently attacked.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCastlingLegal_kingCurrentlyInCheck_returnsFalse`

### TC71: Invalid Castling Through Check

* **State of the system**: White king on `e1`, white rook on `h1`, and `f1` is attacked by a black piece.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCastlingLegal_pathSquareAttacked_returnsFalse`

### TC72: Invalid Castling Into Check

* **State of the system**: White king on `e1`, white rook on `h1`, and `g1` is attacked by a black piece.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCastlingLegal_destinationAttacked_returnsFalse`

### TC73: Invalid Castling When Rook Missing

* **State of the system**: White king on `e1`, no rook on `h1`.
* **Expected output**: Returns `false`.
* **Implemented at**: `isCastlingLegal_rookMissing_returnsFalse`

---

## Method under test: `RulesEngine.isEnPassantLegal(GameState state, Move move)`

### TC74: Valid White En Passant

* **State of the system**: White pawn on `e5`, black pawn just moved from `d7` to `d5`, en passant target is `d6`, and
  White attempts `exd6 en passant`.
* **Expected output**: Returns `true`.
* **Implemented at**: `isEnPassantLegal_whiteValid_returnsTrue`

### TC75: Valid Black En Passant

* **State of the system**: Black pawn on `e4`, white pawn just moved from `d2` to `d4`, en passant target is `d3`, and
  Black attempts `exd3 en passant`.
* **Expected output**: Returns `true`.
* **Implemented at**: `isEnPassantLegal_blackValid_returnsTrue`

### TC76: Invalid En Passant When Previous Move Was Not Pawn Double Move

* **State of the system**: White pawn on `e5`, black pawn on `d5`, but previous move was not a two-square pawn move.
* **Expected output**: Returns `false`.
* **Implemented at**: `isEnPassantLegal_previousMoveNotDoublePawn_returnsFalse`

### TC77: Invalid En Passant After One Turn Has Passed

* **State of the system**: En passant target would have existed earlier, but another move has already occurred.
* **Expected output**: Returns `false`.
* **Implemented at**: `isEnPassantLegal_staleTarget_returnsFalse`

### TC78: Invalid En Passant By Non-Pawn

* **State of the system**: White knight attempts an en passant move.
* **Expected output**: Returns `false`.
* **Implemented at**: `isEnPassantLegal_nonPawn_returnsFalse`

### TC79: Invalid En Passant To Wrong Rank

* **State of the system**: White pawn attempts en passant to a square not on rank `6`, or black pawn attempts to a
  square not on rank `3`.
* **Expected output**: Returns `false`.
* **Implemented at**: `isEnPassantLegal_wrongTargetRank_returnsFalse`

---

## Method under test: `RulesEngine.isPromotionLegal(GameState state, Move move)`

### TC80: Valid White Promotion To Queen

* **State of the system**: White pawn on `e7`, move to `e8`, `promotionPiece = QUEEN`.
* **Expected output**: Returns `true`.
* **Implemented at**: `isPromotionLegal_whiteQueenPromotion_returnsTrue`

### TC81: Valid Black Promotion To Knight

* **State of the system**: Black pawn on `e2`, move to `e1`, `promotionPiece = KNIGHT`.
* **Expected output**: Returns `true`.
* **Implemented at**: `isPromotionLegal_blackKnightPromotion_returnsTrue`

### TC82: Invalid Promotion To King

* **State of the system**: White pawn on `e7`, move to `e8`, `promotionPiece = KING`.
* **Expected output**: Returns `false`.
* **Implemented at**: `isPromotionLegal_promotionToKing_returnsFalse`

### TC83: Invalid Promotion To Pawn

* **State of the system**: White pawn on `e7`, move to `e8`, `promotionPiece = PAWN`.
* **Expected output**: Returns `false`.
* **Implemented at**: `isPromotionLegal_promotionToPawn_returnsFalse`

### TC84: Invalid Null Promotion On Final Rank

* **State of the system**: White pawn on `e7`, move to `e8`, `promotionPiece = null`.
* **Expected output**: Returns `false`.
* **Implemented at**: `isPromotionLegal_missingPromotionPiece_returnsFalse`

### TC85: Invalid Promotion Before Final Rank

* **State of the system**: White pawn on `e6`, move to `e7`, `promotionPiece = QUEEN`.
* **Expected output**: Returns `false`.
* **Implemented at**: `isPromotionLegal_promotionBeforeFinalRank_returnsFalse`

### TC86: Invalid Promotion By Non-Pawn

* **State of the system**: White rook moves to `e8`, `promotionPiece = QUEEN`.
* **Expected output**: Returns `false`.
* **Implemented at**: `isPromotionLegal_nonPawnPromotion_returnsFalse`

---

## Method under test: `RulesEngine.getGameStatus(GameState state)`

### TC87: Ongoing Game

* **State of the system**: Current player is not in check and has legal moves.
* **Expected output**: Returns `GameStatus.ONGOING`.
* **Implemented at**: `getGameStatus_ongoing_returnsOngoing`

### TC88: Check Status

* **State of the system**: Current player is in check but has at least one legal move.
* **Expected output**: Returns `GameStatus.CHECK`.
* **Implemented at**: `getGameStatus_inCheckHasLegalMove_returnsCheck`

### TC89: Checkmate Status

* **State of the system**: Current player is in check and has no legal moves.
* **Expected output**: Returns `GameStatus.CHECKMATE`.
* **Implemented at**: `getGameStatus_checkmate_returnsCheckmate`

### TC90: Stalemate Status

* **State of the system**: Current player is not in check and has no legal moves.
* **Expected output**: Returns `GameStatus.STALEMATE`.
* **Implemented at**: `getGameStatus_stalemate_returnsStalemate`

### TC91: Invalid Null State

* **State of the system**: `state = null`.
* **Expected output**: Throws `IllegalArgumentException`.
* **Implemented at**: `getGameStatus_nullState_throwsException`