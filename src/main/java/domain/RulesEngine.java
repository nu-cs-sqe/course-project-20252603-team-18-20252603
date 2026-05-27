package domain;

public class RulesEngine {

	public boolean isLegalMove(Move move, GameModel model) {
		if (move == null) {
			return false;
		}

		if (model == null) {
			return false;
		}

		Board board = model.getBoard();

		Square from = move.getFrom();
		Square fromBoardSquare = board.getSquare(
				from.getFile(),
				from.getRank()
		);

		Piece sourcePiece = fromBoardSquare.getOccupant();

		if (sourcePiece == null) {
			return false;
		}

		Color currentTurn = model.getCurrentTurn();

		if (sourcePiece.getColor() != currentTurn) {
			return false;
		}

		if (isCastlingLegal(move, model)) {
			return true;
		}

		if (isPromotionLegal(move, model)) {
			return true;
		}

		if (isEnpassantLegal(move,model)){
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

		boolean kingInCheck = isInCheck(model, currentTurn);

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

	protected boolean isInCheck(GameModel model, Color color) {
		return false;
		// TODO
		// Given model and color, return if king is in check
	}

	protected boolean isCastlingLegal(Move move, GameModel model) {
		return false;
		// TODO
		// Given Move, validate if move is castling shaped
		// Are all Castling conditions legal?
	}

	protected boolean isPromotionLegal(Move move, GameModel model) {
		return false;
		// TODO
	}

	protected boolean isEnpassantLegal(Move move, GameModel model) {
		return false;
		// TODO
	}


}