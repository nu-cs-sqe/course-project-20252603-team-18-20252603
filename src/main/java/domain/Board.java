package domain;

import java.util.ArrayList;
import java.util.List;

public class Board {
	private List<Character> files;
	List<List<Square>> grid;

	public Board() {
		this.files = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			this.files.add((char) ('a' + i));
		}

		this.grid = new ArrayList<>();
		for (int rank = 1; rank < 9; rank++) {
			ArrayList<Square> row = new ArrayList<>();
			for (char file : this.files) {
				row.add(Square.create(file, rank));
			}
			this.grid.add(row);
		}
	}

	private int fileToIndex(char file) {
		char firstFile = 'a';
		return file - firstFile;
	}

	Square getSquare(char file, int rank) {
		if (file < 'a' || file > 'h') {
			throw new IllegalArgumentException("File in Board must be between 'a' and 'h'.");
		}
		if (rank < 1 || rank > 8){
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

		Piece movedPiece = null;
		if (from.isEmpty() == false) {
			movedPiece = from.getOccupant();
		}

		Piece replacedPiece = null;
		if (to.isEmpty() == false) {
			replacedPiece = to.getOccupant();
		}

		from.setOccupant(null);
		to.setOccupant(movedPiece);
	}
}
