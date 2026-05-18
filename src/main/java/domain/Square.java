package domain;

public class Square {
	final private char file;
	final private int rank;
	private Piece occupant;

	private Square(char file, int rank) {
		this.file = file;
		this.rank = rank;
	}

	public static Square create(char file, int rank) {
		if (file < 'a' || file > 'h') {
			throw new IllegalArgumentException("File must be between 'a' and 'h'.");
		}
		if (rank < 1 || rank > 8){
			throw new IllegalArgumentException("Rank must be between 1 and 8");
		}

		return new Square(file, rank);
	}

	public char getFile() {
		return file;
	}

	public int getRank() {
		return rank;
	}

	public boolean isEmpty() {
		return this.occupant == null;
	}

	public void setOccupant(Piece piece) {
		this.occupant = piece;
	}

	public Piece getOccupant() {
		return occupant;
	}
}