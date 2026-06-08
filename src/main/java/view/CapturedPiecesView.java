package view;

import model.Piece;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.util.List;
import java.util.stream.Collectors;

public class CapturedPiecesView {
	private final JPanel panel;
	private final JLabel whitePieces;
	private final JLabel blackPieces;

	public CapturedPiecesView() {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Captured pieces"));
		whitePieces = new JLabel("White: none");
		blackPieces = new JLabel("Black: none");
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 22);
		whitePieces.setFont(font);
		blackPieces.setFont(font);
		panel.add(whitePieces);
		panel.add(blackPieces);
	}

	public JPanel getComponent() {
		return panel;
	}

	public void update(List<Piece> white, List<Piece> black) {
		whitePieces.setText("White: " + pieceList(white));
		blackPieces.setText("Black: " + pieceList(black));
	}

	private String pieceList(List<Piece> pieces) {
		if (pieces == null || pieces.isEmpty()) {
			return "none";
		}
		return pieces.stream()
				.map(piece -> piece.getType().name().toLowerCase())
				.collect(Collectors.joining(", "));
	}
}
