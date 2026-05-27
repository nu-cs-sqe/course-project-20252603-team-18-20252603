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
		if (from.getOccupant() != this) {
			throw new IllegalArgumentException("'from' Square is not occupied by this Bishop");
		}

		List<Square> legalMoves = new ArrayList<Square>();

		addDiagonalMoves(legalMoves, from, 1, 1);
		addDiagonalMoves(legalMoves, from, 1, -1);
		addDiagonalMoves(legalMoves, from, -1, 1);
		addDiagonalMoves(legalMoves, from, -1, -1);

		return legalMoves;
	}

	private void addDiagonalMoves(List<Square> legalMoves, Square from, int fileDirection, int rankDirection) {
		char file = (char) (from.getFile() + fileDirection);
		int rank = from.getRank() + rankDirection;

		while (file >= MINFILE && file <= MAXFILE && rank >= MINRANK && rank <= MAXRANK) {
			legalMoves.add(Square.create(file, rank));
			file = (char) (file + fileDirection);
			rank += rankDirection;
		}
	}
}