package domain;

import java.util.ArrayList;
import java.util.List;

public final class Player {
	private final Color color;
	private final boolean isHuman;
	private final List<Piece> capturedPieces;

	public Player(Color color, boolean isHuman) {
		if (color == null) {
			throw new IllegalArgumentException("Color can't be null.");
		}

		this.color = color;
		this.isHuman = isHuman;
		this.capturedPieces = new ArrayList<Piece>();
	}

	public Color getColor() {
		return color;
	}

	public boolean isHuman() {
		return isHuman;
	}

	public List<Piece> getCapturedPieces() {
		return new ArrayList<Piece>(capturedPieces);
	}

	public void addCapturedPiece(Piece piece) {
		capturedPieces.add(piece);
	}
}