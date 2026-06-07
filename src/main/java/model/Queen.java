package model;

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
		if (from.getOccupant() != this) {
			throw new IllegalArgumentException("'from' Square is not occupied by this Queen");
		}

		List<Square> legalMoves = new ArrayList<Square>();

		addDirectionalMoves(legalMoves, from, 1, 0);
		addDirectionalMoves(legalMoves, from, -1, 0);
		addDirectionalMoves(legalMoves, from, 0, 1);
		addDirectionalMoves(legalMoves, from, 0, -1);
		addDirectionalMoves(legalMoves, from, 1, 1);
		addDirectionalMoves(legalMoves, from, 1, -1);
		addDirectionalMoves(legalMoves, from, -1, 1);
		addDirectionalMoves(legalMoves, from, -1, -1);

		return legalMoves;
	}

	private void addDirectionalMoves(List<Square> legalMoves, Square from, int fileDirection, int rankDirection) {
		char file = (char) (from.getFile() + fileDirection);
		int rank = from.getRank() + rankDirection;

		while (file >= MINFILE && file <= MAXFILE && rank >= MINRANK && rank <= MAXRANK) {
			legalMoves.add(Square.create(file, rank));
			file = (char) (file + fileDirection);
			rank += rankDirection;
		}
	}
}