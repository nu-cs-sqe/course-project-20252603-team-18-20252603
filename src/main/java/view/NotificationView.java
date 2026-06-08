package view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;

public class NotificationView {
	private final JLabel label;

	public NotificationView() {
		label = new JLabel("Chess", SwingConstants.CENTER);
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
		label.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		label.setOpaque(true);
		label.setBackground(new Color(245, 245, 245));
	}

	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP",
			justification = "The application layout must use the live Swing notification component."
	)
	public JLabel getComponent() {
		return label;
	}

	public void showTurn(String color) {
		setMessage(displayColor(color) + " to move", new Color(245, 245, 245));
	}

	public void showCheck(String color) {
		setMessage(displayColor(color) + " is in check", new Color(255, 224, 178));
	}

	public void showCheckmate(String winner) {
		setMessage("Checkmate - " + displayColor(winner) + " wins", new Color(255, 205, 210));
	}

	public void showStalemate() {
		setMessage("Stalemate - draw", new Color(225, 225, 225));
	}

	private void setMessage(String message, Color background) {
		label.setText(message);
		label.setBackground(background);
	}

	private String displayColor(String color) {
		if (color == null || color.isEmpty()) {
			return "";
		}
		return color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase();
	}
}
