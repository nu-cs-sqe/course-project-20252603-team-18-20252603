# Chess Game Use Cases

---

## Use Case 1: Start New Game

**Actor:** Player  
**Preconditions:** The game application is launched.

### Acceptance Criteria
- The game must not start unless there are exactly 2 players (or 1 player and 1 computer opponent).
- The system must initialize a standard 8×8 checkered board.
- Each player must receive exactly 16 pieces of their assigned color (White or Black): 1 King, 1 Queen, 2 Rooks, 2 Knights, 2 Bishops, and 8 Pawns.
- White's pieces must be placed on ranks 1 and 2 in the standard chess starting position.
- Black's pieces must be placed on ranks 7 and 8 in the standard chess starting position.
- The turn order must be initialized so that the player assigned the White pieces moves first.

### Main Flow
1. Player clicks **Start Game**.
2. System assigns White and Black colors to the players.
3. System initializes an empty 8×8 chessboard.
4. System places the 16 White pieces in their designated starting squares (Pawns on rank 2; major/minor pieces on rank 1).
5. System places the 16 Black pieces in their designated starting squares (Pawns on rank 7; major/minor pieces on rank 8).
6. System sets the initial turn order to the White player.

### Alternate Flows
N/A

### Postconditions
- All 32 pieces are arranged on the board in the official standard starting positions.
- The players are assigned their respective colors.
- The game is ready for White's first move.

---

## Use Case 2: Move Piece

**Actor:** Player  
**Preconditions:** The game is in progress, and it is the acting player's turn.

### Acceptance Criteria
- A player can only move pieces of their assigned color.
- A move is only permitted if it is the player's turn.
- The destination square must be a legal move for the selected piece according to standard chess rules (including castling, en passant, and custom rules to be added).
- A piece cannot jump over other pieces unless it is a Knight (or during castling).
- A player cannot make a move that places or leaves their own King in check.

### Main Flow
1. Player selects one of their pieces on the board.
2. Player selects a destination for the piece.
3. System moves the piece to the new square.
4. System records the move in the game history.
5. System changes the turn order to the opponent.

### Alternate Flows
- **1.a / 2.a** : The player clicks on an invalid square: the system cancels the selection and resumes at Step 1.

### Postconditions
- The piece occupies the new square.
- The previous square is empty.
- It is the opponent's turn.

---

## Use Case 3: Capture Piece

**Actor:** Player  
**Preconditions:** The game is in progress, it is the acting player's turn, and an opponent's piece is within the legal movement range of the player's piece.

### Acceptance Criteria
- A capture can only occur if the destination square contains an opponent's piece.
- A capture can only occur if there is a valid path from the player's piece to the opponent's piece.
- A player cannot capture their own pieces.
- The King cannot be captured; it can only be checkmated.
- The captured piece must be immediately removed from the board.
- A player cannot make a move that places or leaves their own King in check.

### Main Flow
1. Player selects one of their pieces.
2. System highlights valid destination squares, including squares occupied by opponent pieces.
3. Player clicks on a highlighted square occupied by an opponent's piece.
4. System removes the opponent's piece from the board.
5. System moves the player's piece to the destination square.
6. System adds the captured piece to the opponent's captured pieces list.
7. System records the capture in the game history.
8. System changes the turn order to the opponent.

### Alternate Flows
- **3.a** : The player attempts an en passant capture with a pawn: the system removes the opponent's pawn from the adjacent square, moves the capturing pawn diagonally, and proceeds to Step 6.

### Postconditions
- The opponent's piece is removed from active play.
- The player's piece occupies the square where the opponent's piece was (except in en passant).
- It is the opponent's turn.

---

## Use Case 4: Check

**Actor:** System (triggered by Player)  
**Preconditions:** A player has just completed a move or capture.

### Acceptance Criteria
- The system must detect a Check state immediately after a move if the opponent's King is under attack by one or more pieces.
- The system must visually or textually notify both players of the Check.
- The player in check is restricted to making only moves that remove the King from check (moving the King, blocking the attack, or capturing the attacking piece).

### Main Flow
1. System evaluates the board state immediately after a piece lands on its new square.
2. System determines that the opposing King is currently in the attack path of the moved piece (or a revealed piece).
3. System triggers the **Check** state.
4. System displays a **Check** notification on the UI.
5. Opponent's turn begins.
6. Opponent selects a piece.
7. System calculates and highlights only the legal moves that resolve the Check condition.

### Alternate Flows
- **2.a** : The system determines the King is not under attack: normal turn transition occurs without any Check notification.
- **7.a** : The opponent selects a piece that has no legal moves to resolve the check: the system highlights zero squares and resumes at Step 6.

### Postconditions
- The defending player is restricted to moves that resolve the check.
- The game state reflects that a check is active.

---

## Use Case 5: Checkmate

**Actor:** System  
**Preconditions:** A player has just completed a move that places the opponent's King in check.

### Acceptance Criteria
- The system must trigger Checkmate if a King is in check and has no legal moves to escape, block, or capture the threat.
- The system must immediately end the game upon detecting Checkmate.
- The system must declare the attacking player as the winner.

### Main Flow
1. System evaluates the board state and detects the opposing King is in Check.
2. System evaluates all possible legal moves for all pieces belonging to the defending player.
3. System determines there are 0 valid moves available that can remove the King from check.
4. System triggers the **Checkmate** state.
5. System displays a **Checkmate** notification on the UI.
6. System declares the attacking player the winner.
7. System disables all further piece movements on the board.
8. System offers post-game options (e.g., Play Again, Review Game, Exit).

### Alternate Flows
- **3.a** : The system determines there is at least 1 valid move to escape the check: the system triggers the standard Check use case instead.

### Postconditions
- The game is permanently locked.
- The game result is recorded.
- The players are prompted with post-game options.

---

## Use Case 6: Pawn Promotion

**Actor:** Player  
**Preconditions:** The game is in progress, and the acting player has a pawn positioned one square away from the final rank (7th rank for White, 2nd rank for Black) with a legal move available.

### Acceptance Criteria
- A pawn must become eligible for promotion the exact moment it moves to the 8th rank (for White) or the 1st rank (for Black).
- The game must pause and the player must be forced to choose a replacement piece before their turn concludes.
- The player can only promote the pawn to a Queen, Rook, Bishop, or Knight of their respective color.
- A pawn cannot remain a pawn, nor can it be promoted to a King.
- The newly promoted piece must immediately be capable of delivering Check or Checkmate upon being placed on the board.

### Main Flow
1. Player moves their pawn to the final rank (either by advancing forward or capturing diagonally).
2. System prompts the player with a promotion menu displaying four options: Queen, Rook, Bishop, and Knight.
3. Player clicks on their desired piece.
4. System removes the pawn and places the newly selected piece on the destination square.
5. System evaluates the new board state (checking if the new piece places the opponent in Check or Checkmate).
6. System records the move and the promotion in the game history.
7. System changes the turn order to the opponent and resumes the game.

### Alternate Flows
- **3.a** : The player attempts to click outside the promotion menu: the system ignores the input, enforcing that the promotion must be resolved before any other action can occur, and resumes at Step 3.

### Postconditions
- The pawn is permanently removed from the board.
- The newly selected piece occupies the promotion square.
- It is the opponent's turn (unless the promotion resulted in Checkmate).