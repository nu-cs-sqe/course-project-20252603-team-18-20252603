package domain;

public class RulesEngine {

	public boolean isLegalMove(Move move, GameModel model) {
		if (move == null) {
			return false;
		}

		if (model == null) {
			return false;
		}

		Square from = move.getFrom();

		Square fromBoardSquare = model.getBoard().getSquare(
				from.getFile(),
				from.getRank()
		);

		Piece piece = fromBoardSquare.getOccupant();

		if (piece == null) {
			return false;
		}

		return false;
	}
}