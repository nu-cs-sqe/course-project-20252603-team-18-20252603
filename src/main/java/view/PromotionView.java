package view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import i18n.Localization;
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
	private final Localization localization;

	public PromotionView() {
		this(null, new Localization());
	}

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP2",
			justification = "The dialog intentionally retains its owning Swing component."
	)
	public PromotionView(Component parent) {
		this(parent, new Localization());
	}

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP2",
			justification = "The dialog intentionally retains its owning Swing component."
	)
	public PromotionView(Component parent, Localization localization) {
		this.parent = parent;
		this.localization = localization;
	}

	/**
	 * Displays the promotion selection menu for the given player color and
	 * returns a CompletableFuture that resolves to the Piece the player chose.
	 *
	 * The controller must not advance the game turn until this future resolves
	 * with a non-null, valid promotion Piece (Queen, Rook, Bishop, or Knight).
	 */
	public CompletableFuture<Piece> show(String color) {
		CompletableFuture<Piece> selection = new CompletableFuture<>();
		Runnable showDialog = () -> {
			Color pieceColor = Color.valueOf(color.toUpperCase());
			String[] options = {
				localization.text("piece.queen"),
				localization.text("piece.rook"),
				localization.text("piece.bishop"),
				localization.text("piece.knight")
			};
			int choice = JOptionPane.showOptionDialog(
					parent,
					localization.text("promotion.message"),
					localization.text("promotion.title"),
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]
			);
			if (choice < 0) {
				choice = 0;
			}
			selection.complete(createPiece(choice, pieceColor));
		};

		if (SwingUtilities.isEventDispatchThread()) {
			showDialog.run();
		} else {
			SwingUtilities.invokeLater(showDialog);
		}
		return selection;
	}

	public void hide() {
		// JOptionPane closes automatically after the player selects an option.
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
