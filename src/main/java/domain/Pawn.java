package domain;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{
	public Pawn(Color color) {
		super(requireColor(color), PieceType.PAWN);
	}

	@Override
	public List<Square> getLegalMoves(Square from) {
		if (from == null) {
			throw new IllegalArgumentException("'from' Square cannot be null");
		}
		if (from.getOccupant() != this) {
			throw new IllegalArgumentException("'from' Square is not occupied by this Pawn");
		}

		List<Square> legalMoves = new ArrayList<Square>();

		int direction = (this.getColor() == Color.WHITE) ? 1 : -1;
		char file = from.getFile();
		int rank = from.getRank();

		int oneStepRank = rank + direction;
		if (oneStepRank >= MINRANK && oneStepRank <= MAXRANK) {
			legalMoves.add(Square.create(file, oneStepRank));
		}

		return legalMoves;
	}
}
