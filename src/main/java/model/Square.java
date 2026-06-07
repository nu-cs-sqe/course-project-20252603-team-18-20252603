package model;

public class Square {
	final private char file;
	final private int rank;
	private Piece occupant;

	private static int minrank = 1;
	private static int maxrank = 8;
	private static char minfile = 'a';
	private static char maxfile = 'h';

	private Square(char file, int rank) {
		this.file = file;
		this.rank = rank;
	}

	public static Square create(char file, int rank) {
		if (file < minfile || file > maxfile) {
			throw new IllegalArgumentException("File must be between 'a' and 'h'.");
		}
		if (rank < minrank || rank > maxrank){
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