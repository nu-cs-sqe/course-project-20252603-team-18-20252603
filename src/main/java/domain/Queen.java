package domain;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
	public Queen(Color color) {
		super(requireColor(color), PieceType.QUEEN);
	}

	@Override
	public List<Square> getLegalMoveDestinationSquares(Square from) {
		if (from == null) {
			throw new IllegalArgumentException("'from' Square cannot be null");
		}

		return new ArrayList<Square>();
	}
}