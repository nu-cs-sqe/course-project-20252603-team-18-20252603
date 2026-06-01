package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RulesEngine {
	protected static final char MINFILE = 'a';
	protected static final char MAXFILE = 'h';
	protected static final int MINRANK = 1;
	protected static final int MAXRANK = 8;

	public boolean isLegalMove(Move move, GameState gameState) {
		if (move == null) {
			return false;
		}

		if (gameState == null) {
			return false;
		}

		Board board = gameState.getBoard();

		Square from = move.getFrom();
		Square fromBoardSquare = board.getSquare(
				from.getFile(),
				from.getRank()
		);

		Piece sourcePiece = fromBoardSquare.getOccupant();

		if (sourcePiece == null) {
			return false;
		}

		Color currentTurn = gameState.getCurrentTurn();

		if (sourcePiece.getColor() != currentTurn) {
			return false;
		}

		if (isCastlingLegal(move, gameState)) {
			return true;
		}

		if (isPromotionLegal(move, gameState)) {
			return true;
		}

		if (isEnpassantLegal(move, gameState)) {
			return true;
		}

		Square to = move.getTo();

		Square toBoardSquare = board.getSquare(
				to.getFile(),
				to.getRank()
		);

		Piece destinationPiece = toBoardSquare.getOccupant();

		if (sourcePiece.getType() == PieceType.PAWN
				&& !isNormalPawnMoveLegal(sourcePiece, from, to, toBoardSquare, board)) {
			return false;
		}

		if (destinationPiece != null && destinationPiece.getColor() == currentTurn) {
			return false;
		}

		if (isSlidingPiece(sourcePiece) && isPathBlocked(from, to, board)) {
			return false;
		}

		Piece capturedPiece = toBoardSquare.getOccupant();

		fromBoardSquare.setOccupant(null);
		toBoardSquare.setOccupant(sourcePiece);

		boolean kingInCheck = isInCheck(gameState, currentTurn);

		fromBoardSquare.setOccupant(sourcePiece);
		toBoardSquare.setOccupant(capturedPiece);

		if (kingInCheck) {
			return false;
		}

		return true;
	}

	private boolean isSlidingPiece(Piece piece) {
		PieceType type = piece.getType();

		return type == PieceType.ROOK
				|| type == PieceType.BISHOP
				|| type == PieceType.QUEEN;
	}

	private boolean isPathBlocked(Square from, Square to, Board board) {
		int fileStep = Integer.compare(to.getFile(), from.getFile());
		int rankStep = Integer.compare(to.getRank(), from.getRank());

		char currentFile = (char) (from.getFile() + fileStep);
		int currentRank = from.getRank() + rankStep;

		while (currentFile != to.getFile() || currentRank != to.getRank()) {
			Square currentSquare = board.getSquare(currentFile, currentRank);

			if (!currentSquare.isEmpty()) {
				return true;
			}

			currentFile = (char) (currentFile + fileStep);
			currentRank += rankStep;
		}

		return false;
	}

	private static final int WHITE_PAWN_DIRECTION = 1;
	private static final int BLACK_PAWN_DIRECTION = -1;
	private static final int SAME_FILE_DISTANCE = 0;
	private static final int PAWN_SINGLE_MOVE_DISTANCE = 1;
	private static final int PAWN_INITIAL_MOVE_DISTANCE = 2;
	private static final int PAWN_DIAGONAL_FILE_DISTANCE = 1;

	private boolean isNormalPawnMoveLegal(
			Piece pawn,
			Square from,
			Square to,
			Square toBoardSquare,
			Board board
	) {
		int direction = pawn.getColor() == Color.WHITE
				? WHITE_PAWN_DIRECTION
				: BLACK_PAWN_DIRECTION;

		int fileDifference = to.getFile() - from.getFile();
		int rankDifference = to.getRank() - from.getRank();

		boolean isStraightMove = fileDifference == SAME_FILE_DISTANCE;
		boolean isDiagonalMove = Math.abs(fileDifference) == PAWN_DIAGONAL_FILE_DISTANCE;
		boolean isOneForward = rankDifference == direction * PAWN_SINGLE_MOVE_DISTANCE;
		boolean isTwoForward = rankDifference == direction * PAWN_INITIAL_MOVE_DISTANCE;

		if (isStraightMove && isOneForward) {
			return toBoardSquare.isEmpty();
		}

		if (isStraightMove && isTwoForward) {
			if (pawn.hasMoved()) {
				return false;
			}

			Square intermediateSquare = board.getSquare(
					from.getFile(),
					from.getRank() + direction
			);

			return intermediateSquare.isEmpty() && toBoardSquare.isEmpty();
		}

		if (isDiagonalMove && isOneForward) {
			return !toBoardSquare.isEmpty()
					&& toBoardSquare.getOccupant().getColor() != pawn.getColor();
		}

		return false;
	}

	protected boolean isInCheck(GameState gameState, Color color) {
		if (color == null) {
			throw new IllegalArgumentException("Opponent Color cannot be null");
		}

		Board board = gameState.getBoard();

		for (char file = MINFILE; file <= MAXFILE; file++) {
			for (int rank = MINRANK; rank <= MAXRANK; rank++) {
				Square square = board.getSquare(file, rank);
				Piece piece = square.getOccupant();
				if (piece != null
						&& piece.getType() == PieceType.KING
						&& piece.getColor() == color) {
					Color opponent = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
					return isSquareAttacked(gameState, square, opponent);
				}
			}
		}

		throw new IllegalStateException("No " + color + " king found on the board");
	}

	public boolean isCheckmate(GameState gameState, Color color) {
		if (!isInCheck(gameState, color)) {
			return false;
		}

		Board board = gameState.getBoard();

		for (char file = MINFILE; file <= MAXFILE; file++) {
			for (int rank = MINRANK; rank <= MAXRANK; rank++) {
				Square square = board.getSquare(file, rank);
				if (!getLegalMoves(gameState, square).isEmpty()) {
					return false;
				}
			}
		}

		return true;
	}

	protected boolean isSquareAttacked(GameState gameState, Square square, Color byColor) {
		if (square == null) {
			throw new IllegalArgumentException("'square' cannot be null");
		}
		if (byColor == null) {
			throw new IllegalArgumentException("'byColor' cannot be null");
		}

		Board board = gameState.getBoard();

		final int SHORT_L_LEG = 1;
		final int LONG_L_LEG = 2;
		final int MAX_KING_DELTA = 1;

		for (char file = MINFILE; file <= MAXFILE; file++) {
			for (int rank = MINRANK; rank <= MAXRANK; rank++) {
				Square candidateSquare = board.getSquare(file, rank);
				Piece piece = candidateSquare.getOccupant();

				if (piece == null || piece.getColor() != byColor) {
					continue;
				}

				int direction = piece.getColor() == Color.WHITE ? 1 : -1;
				int fileDiff = Math.abs(file - square.getFile());
				int rankDiff = Math.abs(rank - square.getRank());
				int pawnRankDiff = square.getRank() - rank;

				boolean isPawnAttack = fileDiff == 1 && pawnRankDiff == direction;
				boolean isLShape = (fileDiff == LONG_L_LEG && rankDiff == SHORT_L_LEG)
						|| (fileDiff == SHORT_L_LEG && rankDiff == LONG_L_LEG);
				boolean isDiagonal = fileDiff == rankDiff && fileDiff != 0;
				boolean isStraight = (fileDiff == 0) != (rankDiff == 0);
				boolean isAdjacent = fileDiff <= MAX_KING_DELTA && rankDiff <= MAX_KING_DELTA
						&& !(fileDiff == 0 && rankDiff == 0);

				if (piece.getType() == PieceType.PAWN) {
					if (isPawnAttack) {
						return true;
					}
				} else if (piece.getType() == PieceType.KNIGHT) {
					if (isLShape) {
						return true;
					}
				} else if (piece.getType() == PieceType.BISHOP) {
					if (isDiagonal && !isPathBlocked(candidateSquare, square, board)) {
						return true;
					}
				} else if (piece.getType() == PieceType.ROOK) {
					if (isStraight && !isPathBlocked(candidateSquare, square, board)) {
						return true;
					}
				} else if (piece.getType() == PieceType.QUEEN) {
					if ((isDiagonal || isStraight) && !isPathBlocked(candidateSquare, square, board)) {
						return true;
					}
				} else if (piece.getType() == PieceType.KING) {
					if (isAdjacent) {
						return true;
					}
				}
			}
		}

		return false;
	}

	protected boolean isCastlingLegal(Move move, GameState gameState) {
		if (move == null || gameState == null) {
			return false;
		}

		if (move.getClass() != Move.class) {
			return false;
		}

		Board board = gameState.getBoard();
		if (board == null) {
			return false;
		}

		Square from = move.getFrom();
		Square to = move.getTo();
		if (from == null || to == null) {
			return false;
		}

		Square fromBoardSquare = board.getSquare(from.getFile(), from.getRank());
		Piece king = fromBoardSquare.getOccupant();

		if (king == null || king.getType() != PieceType.KING || king.hasMoved()) {
			return false;
		}

		Color color = king.getColor();
		int homeRank = (color == Color.WHITE) ? MINRANK : MAXRANK;

		if (from.getFile() != 'e' || from.getRank() != homeRank || to.getRank() != homeRank) {
			return false;
		}

		boolean isKingside = to.getFile() == 'g';
		boolean isQueenside = to.getFile() == 'c';

		if (!isKingside && !isQueenside) {
			return false;
		}

		char rookFile = isKingside ? 'h' : 'a';
		Square rookSquare = board.getSquare(rookFile, homeRank);
		Piece rook = rookSquare.getOccupant();

		if (rook == null
				|| rook.getType() != PieceType.ROOK
				|| rook.getColor() != color
				|| rook.hasMoved()) {
			return false;
		}

		char firstFileBetweenKingAndRook = (char) (Math.min(from.getFile(), rookFile) + 1);
		char lastFileBetweenKingAndRook = (char) (Math.max(from.getFile(), rookFile) - 1);

		for (char file = firstFileBetweenKingAndRook; file <= lastFileBetweenKingAndRook; file++) {
			if (!board.getSquare(file, homeRank).isEmpty()) {
				return false;
			}
		}

		if (isInCheck(gameState, color)) {
			return false;
		}

		Color opponentColor = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
		int fileStep = isKingside ? 1 : -1;

		for (char file = (char) (from.getFile() + fileStep);
			file != (char) (to.getFile() + fileStep);
			file = (char) (file + fileStep)) {
			Square square = board.getSquare(file, homeRank);
			if (isSquareAttacked(gameState, square, opponentColor)) {
				return false;
			}
		}

		return true;
	}

	protected boolean isPromotionLegal(Move move, GameState gameState) {
		if (move == null || gameState == null) {
			return false;
		}
		if (move.getClass() != Move.class) {
			return false;
		}

		Board board = gameState.getBoard();
		Square from = move.getFrom();
		Square to = move.getTo();
		if (board == null || from == null || to == null) {
			return false;
		}

		Square fromBoardSquare = board.getSquare(from.getFile(), from.getRank());
		Piece sourcePiece = fromBoardSquare.getOccupant();
		if (!(sourcePiece instanceof Pawn)) {
			return false;
		}

		Square toBoardSquare = board.getSquare(to.getFile(), to.getRank());
		if (!isNormalPawnMoveLegal(sourcePiece, from, to, toBoardSquare, board)) {
			return false;
		}
		if (sourcePiece.getColor() == Color.WHITE && to.getRank() != MAXRANK) {
			return false;
		}
		if (sourcePiece.getColor() == Color.BLACK && to.getRank() != MINRANK) {
			return false;
		}

		Piece promotionPiece = move.getPromotionPiece();
		if (promotionPiece == null
				|| promotionPiece.getColor() != sourcePiece.getColor()
				|| promotionPiece.getType() == PieceType.KING
				|| promotionPiece.getType() == PieceType.PAWN) {
			return false;
		}

		Piece capturedPiece = toBoardSquare.getOccupant();
		fromBoardSquare.setOccupant(null);
		toBoardSquare.setOccupant(sourcePiece);

		boolean kingInCheck = isInCheck(gameState, sourcePiece.getColor());

		fromBoardSquare.setOccupant(sourcePiece);
		toBoardSquare.setOccupant(capturedPiece);

		return !kingInCheck;
	}

	protected boolean isEnpassantLegal(Move move, GameState gameState) {
		if (move == null || gameState == null) {
			return false;
		}

		if (move.getClass() != Move.class) {
			return false;
		}

		Board board = gameState.getBoard();
		if (board == null) {
			return false;
		}

		Square from = move.getFrom();
		Square to = move.getTo();
		if (from == null || to == null) {
			return false;
		}

		Square fromBoardSquare = board.getSquare(from.getFile(), from.getRank());
		Square toBoardSquare = board.getSquare(to.getFile(), to.getRank());
		Piece sourcePiece = fromBoardSquare.getOccupant();

		if (sourcePiece == null
				|| sourcePiece != move.getPiece()
				|| sourcePiece.getType() != PieceType.PAWN) {
			return false;
		}

		if (!toBoardSquare.isEmpty()) {
			return false;
		}

		Move previousMove = gameState.getLastMove();
		if (previousMove == null) {
			return false;
		}

		Piece previousPiece = previousMove.getPiece();
		Square previousFrom = previousMove.getFrom();
		Square previousTo = previousMove.getTo();

		if (previousPiece == null
				|| previousFrom == null
				|| previousTo == null
				|| previousPiece.getType() != PieceType.PAWN
				|| previousPiece.getColor() == sourcePiece.getColor()) {
			return false;
		}

		int previousPawnDirection = previousPiece.getColor() == Color.WHITE
				? WHITE_PAWN_DIRECTION
				: BLACK_PAWN_DIRECTION;

		boolean previousMoveWasDoublePawnMove =
				previousFrom.getFile() == previousTo.getFile()
						&& previousTo.getRank() - previousFrom.getRank()
						== previousPawnDirection * PAWN_INITIAL_MOVE_DISTANCE;

		if (!previousMoveWasDoublePawnMove) {
			return false;
		}

		int sourcePawnDirection = sourcePiece.getColor() == Color.WHITE
				? WHITE_PAWN_DIRECTION
				: BLACK_PAWN_DIRECTION;

		int fileDifference = to.getFile() - from.getFile();
		int rankDifference = to.getRank() - from.getRank();

		boolean isDiagonalPawnMove =
				Math.abs(fileDifference) == PAWN_DIAGONAL_FILE_DISTANCE
						&& rankDifference == sourcePawnDirection * PAWN_SINGLE_MOVE_DISTANCE;

		if (!isDiagonalPawnMove) {
			return false;
		}

		int enPassantTargetRank = sourcePiece.getColor() == Color.WHITE ? 6 : 3;
		if (to.getRank() != enPassantTargetRank) {
			return false;
		}

		int expectedTargetRank = (previousFrom.getRank() + previousTo.getRank()) / 2;
		if (to.getFile() != previousTo.getFile() || to.getRank() != expectedTargetRank) {
			return false;
		}

		if (previousTo.getRank() != from.getRank()) {
			return false;
		}

		Square capturedPawnSquare = board.getSquare(previousTo.getFile(), previousTo.getRank());
		if (capturedPawnSquare.getOccupant() != previousPiece) {
			return false;
		}

		Piece capturedPiece = capturedPawnSquare.getOccupant();

		fromBoardSquare.setOccupant(null);
		capturedPawnSquare.setOccupant(null);
		toBoardSquare.setOccupant(sourcePiece);

		boolean kingInCheck = isInCheck(gameState, sourcePiece.getColor());

		fromBoardSquare.setOccupant(sourcePiece);
		capturedPawnSquare.setOccupant(capturedPiece);
		toBoardSquare.setOccupant(null);

		return !kingInCheck;
	}

	public GameStatus getGameStatus(GameState gameState) {
		if (gameState == null) {
			throw new IllegalArgumentException("GameState cannot be null");
		}
		Color turn = gameState.getCurrentTurn();
		if (isInCheck(gameState, turn)) {
			if (isCheckmate(gameState, turn)) {
				return GameStatus.CHECKMATE;
			}
			return GameStatus.CHECK;
		}
		if (isStalemate(gameState, turn)) {
			return GameStatus.STALEMATE;
		}
		return GameStatus.ONGOING;
	}

	protected boolean isStalemate(GameState gameState, Color color) {
		if (isInCheck(gameState, color)) {
			return false;
		}

		Board board = gameState.getBoard();

		for (char file = MINFILE; file <= MAXFILE; file++) {
			for (int rank = MINRANK; rank <= MAXRANK; rank++) {
				Square square = board.getSquare(file, rank);
				Piece piece = square.getOccupant();

				if (piece != null && piece.getColor() == color
						&& !getLegalMoves(gameState, square).isEmpty()) {
					return false;
				}
			}
		}

		return true;
	}

	public List<Square> getLegalMoves(GameState gameState, Square from) {
		if (gameState == null) {
			throw new IllegalArgumentException("GameState cannot be null");
		}
		if (from == null) {
			throw new IllegalArgumentException("'from' Square cannot be null");
		}

		Piece piece = from.getOccupant();

		if (piece == null || piece.getColor() != gameState.getCurrentTurn()) {
			return new ArrayList<>();
		}

		return piece.getLegalMoveDestinationSquares(from)
				.stream()
				.filter(to -> isLegalMove(Move.create(piece, from, to), gameState))
				.collect(Collectors.toList());
	}
}
