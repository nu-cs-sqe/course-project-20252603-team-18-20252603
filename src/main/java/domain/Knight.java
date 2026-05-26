package domain;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
	public Knight(Color color) {
		super(requireColor(color), PieceType.KNIGHT);
	}

	@Override
	public List<Square> getLegalMoveDestinationSquares(Square from) {
		if (from == null) {
			throw new IllegalArgumentException("'from' Square cannot be null");
		}
		if (from.getOccupant() != this) {
			throw new IllegalArgumentException("'from' Square is not occupied by this Knight");
		}

		List<Square> legalDestinationSquares = new ArrayList<Square>();
		return legalDestinationSquares;
	}
}
