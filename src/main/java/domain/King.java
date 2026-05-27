package domain;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
	public King(Color color) {
		super(requireColor(color), PieceType.KING);
	}

	@Override
	public List<Square> getLegalMoveDestinationSquares(Square from) {
		if (from == null) {
			throw new IllegalArgumentException("'from' Square cannot be null");
		}
		if (from.getOccupant() != this) {
			throw new IllegalArgumentException("'from' Square is not occupied by this King");
		}

		List<Square> legalDestinationSquares = new ArrayList<Square>();
		return legalDestinationSquares;
	}
}
