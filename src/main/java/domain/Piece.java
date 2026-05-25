package domain;

public class Piece {
	private final Color color;
	private final PieceType type;
	private boolean hasMoved;

	private Piece(Color color, PieceType type) {
		this.color = color;
		this.type = type;
		this.hasMoved = false;
	}

	public static Piece create(Color color, PieceType type) {
		if (color == null) {
			throw new IllegalArgumentException("Color can't be null.");
		}
		if (type == null) {
			throw new IllegalArgumentException("Piece type can't be null.");
		}
		return new Piece(color, type);
	}

	public Color getColor() {
		return color;
	}

	public PieceType getType() {
		return type;
	}

	public boolean hasMoved() {
		return hasMoved;
	}

	public void markMoved() {
		this.hasMoved = true;
	}

	Square[] getMoves(Board board) {
		return null;
	}
}