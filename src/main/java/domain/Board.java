package domain;

import java.util.ArrayList;
import java.util.List;

public class Board {
	private static char FIRSTFILE = 'a';
	private static char LASTFILE = 'h';
	private static int FIRSTRANK = 1;
	private static int LASTRANK = 8;

	private List<Character> files;
	List<List<Square>> grid;

	public Board() {
		this.files = new ArrayList<>();
		for (int i = 0; i < LASTRANK; i++) {
			this.files.add((char) (FIRSTFILE + i));
		}

		this.grid = new ArrayList<>();
		for (int rank = FIRSTRANK; rank < LASTRANK + 1; rank++) {
			ArrayList<Square> row = new ArrayList<>();
			for (char file : this.files) {
				row.add(Square.create(file, rank));
			}
			this.grid.add(row);
		}
	}

	private int fileToIndex(char file) {
		return file - FIRSTFILE;
	}

	Square getSquare(char file, int rank) {
		if (file < FIRSTFILE || file > LASTFILE) {
			throw new IllegalArgumentException("File in Board must be between 'a' and 'h'.");
		}
		if (rank < FIRSTRANK || rank > LASTRANK) {
			throw new IllegalArgumentException("Rank must be between 1 and 8");
		}
		return grid.get(rank-1).get(fileToIndex(file));
	}

	void placePiece(Piece piece, Square square) {
		if (piece == null) {
			throw new IllegalArgumentException("Piece must not be null.");
		}
		if (square == null) {
			throw new IllegalArgumentException("Target Square must not be null.");
		}
		if (square.isEmpty() == false) {
			throw new IllegalStateException("Target Square is already occupied.");
		}
		square.setOccupant(piece);
	}

	void removePiece(Square square) {
		if (square == null) {
			throw new IllegalArgumentException("Target Square must not be null.");
		}
		if (square.isEmpty()) {
			throw new IllegalStateException("Target Square is empty, cannot remove piece from it.");
		}
		square.setOccupant(null);
	}

	void movePiece(Square from, Square to) {
		if (from == null) {
			throw new IllegalArgumentException("'from' Square must not be null.");
		}
		if (to == null) {
			throw new IllegalArgumentException("'to' Square must not be null.");
		}
		if (from.equals(to)) {
			throw new IllegalArgumentException("'from' and 'to' should not be the same.");
		}

		if (from.isEmpty()) {
			throw new IllegalStateException("'from' Square must not be empty when moving piece.");
		}
		Piece movedPiece = from.getOccupant();

		Piece replacedPiece = null;
		if (to.isEmpty() == false) {
			replacedPiece = to.getOccupant();
		}

		if (replacedPiece != null && movedPiece.getColor() == replacedPiece.getColor()) {
			throw new IllegalArgumentException("Cannot move piece to a square with a piece of the same color.");
		}

		from.setOccupant(null);
		to.setOccupant(movedPiece);
	}
}
