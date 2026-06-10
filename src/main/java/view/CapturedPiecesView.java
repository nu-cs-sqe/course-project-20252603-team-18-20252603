package view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import i18n.Localization;
import model.Piece;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.stream.Collectors;

public class CapturedPiecesView {
	private final JPanel panel;
	private final JLabel whitePieces;
	private final JLabel blackPieces;
	private final Localization localization;

	public CapturedPiecesView() {
		this(new Localization());
	}

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP2",
			justification = "The view shares localization state so language changes are reflected immediately."
	)
	public CapturedPiecesView(Localization localization) {
		this.localization = localization;
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(localization.text("captured.title")));
		whitePieces = new JLabel(buildHtml(localization.text("color.white"), localization.text("captured.none")));
		blackPieces = new JLabel(buildHtml(localization.text("color.black"), localization.text("captured.none")));
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 22);
		whitePieces.setFont(font);
		blackPieces.setFont(font);
		whitePieces.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		blackPieces.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		panel.add(whitePieces);
		panel.add(blackPieces);
	}

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP",
			justification = "The application layout must use the live Swing captured-pieces component."
	)
	public JPanel getComponent() {
		return panel;
	}

	public void update(List<Piece> white, List<Piece> black) {
		panel.setBorder(BorderFactory.createTitledBorder(localization.text("captured.title")));
		whitePieces.setText(buildHtml(localization.text("color.white"), pieceList(white)));
		blackPieces.setText(buildHtml(localization.text("color.black"), pieceList(black)));
	}

	private String buildHtml(String color, String pieces) {
		String raw = localization.format("captured.color", color, pieces);
		return "<html>" + raw + "</html>";
	}

	private String pieceList(List<Piece> pieces) {
		if (pieces == null || pieces.isEmpty()) {
			return localization.text("captured.none");
		}
		return pieces.stream()
				.map(piece -> localization.text("piece." + piece.getType().name().toLowerCase()))
				.collect(Collectors.joining(", "));
	}
}