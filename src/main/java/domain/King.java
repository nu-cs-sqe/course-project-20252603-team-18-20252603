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

		final int MOVEMENTDELTA = 1;
		char file = from.getFile();
		int rank = from.getRank();

		for (int fileDelta = -MOVEMENTDELTA; fileDelta <= MOVEMENTDELTA; fileDelta++) {
			for (int rankDelta = -MOVEMENTDELTA; rankDelta <= MOVEMENTDELTA; rankDelta++) {
				if (fileDelta == 0 && rankDelta == 0) {
					continue;
				}
				char candidateFile = (char) (file + fileDelta);
				int candidateRank = rank + rankDelta;

				if (candidateFile >= MINFILE && candidateFile <= MAXFILE && candidateRank >= MINRANK && candidateRank <= MAXRANK) {
					legalDestinationSquares.add(Square.create(candidateFile, candidateRank));
				}
			}
		}

		if (hasMoved() == false) {
			final char LEFTCASTLEFILE = 'c';
			final char RIGHTCASTLEFILE = 'g';
			final int BOTTOMCASTLERANK = 1;
			legalDestinationSquares.add(Square.create(LEFTCASTLEFILE, BOTTOMCASTLERANK));
			legalDestinationSquares.add(Square.create(RIGHTCASTLEFILE, BOTTOMCASTLERANK));
		}

		return legalDestinationSquares;
	}
}
