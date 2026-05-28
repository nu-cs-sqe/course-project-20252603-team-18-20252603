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
		if (from.getOccupant() != this) {
			throw new IllegalArgumentException("'from' Square is not occupied by this Rook");
		}

		List<Square> legalMoves = new ArrayList<Square>();

		char file = from.getFile();
		int rank = from.getRank();

		addSquareIfInBounds(legalMoves, (char) (file + 1), rank);
		addSquareIfInBounds(legalMoves, (char) (file - 1), rank);
		addSquareIfInBounds(legalMoves, file, rank + 1);
		addSquareIfInBounds(legalMoves, file, rank - 1);

		return legalMoves;
	}

	private void addSquareIfInBounds(List<Square> legalMoves, char file, int rank) {
		if (file >= MINFILE && file <= MAXFILE && rank >= MINRANK && rank <= MAXRANK) {
			legalMoves.add(Square.create(file, rank));
		}
	}
}