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

		return true;
	}
}