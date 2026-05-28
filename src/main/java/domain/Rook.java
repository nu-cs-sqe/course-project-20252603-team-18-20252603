package domain;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
	public Rook(Color color) {
		super(requireColor(color), PieceType.ROOK);
	}

	@Override
	public List<Square> getLegalMoveDestinationSquares(Square from) {
		if (from == null) {
			throw new IllegalArgumentException("'from' Square cannot be null");
		}

		return new ArrayList<Square>();
	}
}