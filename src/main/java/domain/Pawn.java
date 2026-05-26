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

			// Diagonal moves for en passant
			if (file - 1 >= MINFILE) {
				legalMoves.add(Square.create((char)(file - 1), oneStepRank));
			}
			if (file + 1 <= MAXFILE) {
				legalMoves.add(Square.create((char)(file + 1), oneStepRank));
			}
		}

		if (!this.hasMoved()) {
			int twoStepRank = rank + (2 * direction);
			if (twoStepRank >= MINRANK && twoStepRank <= MAXRANK) {
				legalMoves.add(Square.create(file, twoStepRank));
			}
		}

		return legalMoves;
	}
}
