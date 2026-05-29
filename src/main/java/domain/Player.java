package domain;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private final Color color;
	private final boolean isHuman;
	private final List<Piece> capturedPieces;

	public Player(Color color, boolean isHuman) {
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
}