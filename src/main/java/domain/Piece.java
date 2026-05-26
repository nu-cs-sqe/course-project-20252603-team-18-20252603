package domain;

import java.util.List;

public abstract class Piece {
	private final Color color;
	private final PieceType type;
	private boolean hasMoved;

	protected Piece(Color color, PieceType type) {
		this.color = color;
		this.type = type;
		this.hasMoved = false;
	}

	protected static Color requireColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException("Color can't be null.");
		}
		return color;
	}

	protected static PieceType requireType(PieceType type) {
		if (type == null) {
			throw new IllegalArgumentException("Piece type can't be null.");
		}
		return type;
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

	public abstract List<Square> getLegalMoves(Square from);
}