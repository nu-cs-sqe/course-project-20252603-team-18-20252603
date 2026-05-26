package domain;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
	public Bishop(Color color) {
		super(requireColor(color), PieceType.BISHOP);
	}

	@Override
	public List<Square> getLegalMoveDestinationSquares(Square from) {
		if (from == null) {
			throw new IllegalArgumentException("'from' Square cannot be null");
		}

		return new ArrayList<Square>();
	}
}