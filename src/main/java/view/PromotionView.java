package view;

import model.Bishop;
import model.Color;
import model.Knight;
import model.Piece;
import model.Queen;
import model.Rook;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.util.concurrent.CompletableFuture;

public class PromotionView {
	private final Component parent;
	private CompletableFuture<Piece> currentSelection;

	public PromotionView() {
		this(null);
	}

	public PromotionView(Component parent) {
		this.parent = parent;
	}

	/**
	 * Displays the promotion selection menu for the given player color and
	 * returns a CompletableFuture that resolves to the Piece the player chose.
	 *
	 * The controller must not advance the game turn until this future resolves
	 * with a non-null, valid promotion Piece (Queen, Rook, Bishop, or Knight).
	 */
	public CompletableFuture<Piece> show(String color) {
		currentSelection = new CompletableFuture<>();
		Runnable showDialog = () -> {
			Color pieceColor = Color.valueOf(color.toUpperCase());
			String[] options = {"Queen", "Rook", "Bishop", "Knight"};
			int choice = JOptionPane.showOptionDialog(
					parent,
					"Choose a piece for pawn promotion:",
					"Pawn Promotion",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]
			);
			if (choice < 0) {
				choice = 0;
			}
			currentSelection.complete(createPiece(choice, pieceColor));
		};

		if (SwingUtilities.isEventDispatchThread()) {
			showDialog.run();
		} else {
			SwingUtilities.invokeLater(showDialog);
		}
		return currentSelection;
	}

	public void hide() {
		currentSelection = null;
	}

	private Piece createPiece(int choice, Color color) {
		switch (choice) {
			case 1:
				return new Rook(color);
			case 2:
				return new Bishop(color);
			case 3:
				return new Knight(color);
			default:
				return new Queen(color);
		}
	}
}
