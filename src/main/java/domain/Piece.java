package domain;

public abstract class Piece {
	Piece(Color color, PieceType type) {
	}

	Color getColor() {
		return null;
	}

	PieceType getType() {
		return null;
	}

	void markMoved() {
	}

	Square[] getMoves(Board board) {
		return null;
	}
}