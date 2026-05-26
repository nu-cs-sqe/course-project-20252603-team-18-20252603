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
		return legalMoves;
	}
}
