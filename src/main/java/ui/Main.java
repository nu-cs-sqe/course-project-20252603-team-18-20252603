package ui;

import controller.GameController;
import i18n.Localization;
import model.Color;
import model.GameModel;
import model.Player;
import view.BoardView;
import view.CapturedPiecesView;
import view.NotificationView;
import view.PromotionView;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

		Localization localization = new Localization();
		JFrame frame = new JFrame(localization.text("game.title"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		BoardView boardView = new BoardView();
		NotificationView notificationView = new NotificationView(localization);
		CapturedPiecesView capturedPiecesView = new CapturedPiecesView(localization);
		PromotionView promotionView = new PromotionView(frame, localization);

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

		JButton languageButton = new JButton(localization.text("language.switch"));
		JButton resignButton = new JButton(localization.text("game.resign"));
		resignButton.addActionListener(event -> {
			controller.onResign();
			resignButton.setEnabled(false);
		});
		languageButton.addActionListener(event -> {
			localization.toggleLocale();
			frame.setTitle(localization.text("game.title"));
			languageButton.setText(localization.text("language.switch"));
			resignButton.setText(localization.text("game.resign"));
			controller.refreshViews();
			frame.pack();
		});

		JPanel controls = new JPanel();
		controls.add(resignButton);
		controls.add(languageButton);

		JPanel header = new JPanel(new BorderLayout(12, 0));
		header.add(notificationView.getComponent(), BorderLayout.CENTER);
		header.add(controls, BorderLayout.EAST);

		JPanel content = new JPanel(new BorderLayout(12, 12));
		content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		content.add(header, BorderLayout.NORTH);
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
