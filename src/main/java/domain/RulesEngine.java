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

		Square to = move.getTo();
		Square toBoardSquare = board.getSquare(
				to.getFile(),
				to.getRank()
		);

		Piece destinationPiece = toBoardSquare.getOccupant();

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

	protected boolean isInCheck(GameModel model, Color color) {
		return false;
		// TODO
	}
}