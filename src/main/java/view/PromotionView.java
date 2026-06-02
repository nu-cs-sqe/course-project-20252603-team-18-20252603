package view;

import model.Piece;

import java.util.concurrent.CompletableFuture;

public class PromotionView {

	/**
	 * Displays the promotion selection menu for the given player color and
	 * returns a CompletableFuture that resolves to the Piece the player chose.
	 *
	 * The controller must not advance the game turn until this future resolves
	 * with a non-null, valid promotion Piece (Queen, Rook, Bishop, or Knight).
	 */
	public CompletableFuture<Piece> show(String color) {
		// TODO: implement promotion menu display logic
		return new CompletableFuture<>();
	}

	public void hide() {
		// TODO: implement promotion menu hide logic
	}
}