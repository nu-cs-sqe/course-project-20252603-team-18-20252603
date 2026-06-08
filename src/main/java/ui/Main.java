package ui;

import controller.GameController;
import model.Color;
import model.GameModel;
import model.Player;
import view.BoardView;
import view.CapturedPiecesView;
import view.NotificationView;
import view.PromotionView;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;

public final class Main {
	private Main() {
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(Main::createAndShow);
	}

	private static void createAndShow() {
		UIManager.put("Button.select", new java.awt.Color(246, 246, 105));

		JFrame frame = new JFrame("Chess");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		BoardView boardView = new BoardView();
		NotificationView notificationView = new NotificationView();
		CapturedPiecesView capturedPiecesView = new CapturedPiecesView();
		PromotionView promotionView = new PromotionView(frame);

		GameModel model = GameModel.newGame(
				new Player(Color.WHITE, true),
				new Player(Color.BLACK, true)
		);
		GameController controller = new GameController(
				model,
				boardView,
				notificationView,
				promotionView,
				capturedPiecesView
		);
		boardView.setSquareClickHandler(controller::onSquareClick);

		JPanel content = new JPanel(new BorderLayout(12, 12));
		content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		content.add(notificationView.getComponent(), BorderLayout.NORTH);
		content.add(boardView.getComponent(), BorderLayout.CENTER);
		content.add(capturedPiecesView.getComponent(), BorderLayout.SOUTH);

		frame.setContentPane(content);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		controller.init();
		frame.setVisible(true);
	}
}
