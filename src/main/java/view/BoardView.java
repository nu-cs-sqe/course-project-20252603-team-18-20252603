package view;

import model.Board;
import model.Color;
import model.Piece;
import model.Square;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BoardView {
	private static final java.awt.Color LIGHT_SQUARE = new java.awt.Color(240, 217, 181);
	private static final java.awt.Color DARK_SQUARE = new java.awt.Color(181, 136, 99);
	private static final java.awt.Color HIGHLIGHT = new java.awt.Color(246, 246, 105);
	private static final java.awt.Color CHECK = new java.awt.Color(220, 80, 80);
	private final JPanel panel;
	private final Map<String, JButton> buttons;
	private Board board;
	private Consumer<Square> squareClickHandler;

	public BoardView() {
		panel = new JPanel(new GridLayout(8, 8));
		panel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(65, 48, 35), 4));
		buttons = new HashMap<>();
		createSquares();
	}

	public JPanel getComponent() {
		return panel;
	}

	public void setSquareClickHandler(Consumer<Square> handler) {
		squareClickHandler = handler;
	}

	public void render(Board board) {
		if (board == null) {
			throw new IllegalArgumentException("Board must not be null");
		}
		this.board = board;
		for (int rank = 1; rank <= 8; rank++) {
			for (char file = 'a'; file <= 'h'; file++) {
				Square square = board.getSquare(file, rank);
				JButton button = buttons.get(key(file, rank));
				button.setText(pieceSymbol(square.getOccupant()));
				button.setToolTipText(file + Integer.toString(rank));
			}
		}
	}

	public void highlightSquares(List<Square> squares) {
		clearHighlights();
		if (squares == null) {
			return;
		}
		for (Square square : squares) {
			JButton button = buttons.get(key(square));
			if (button != null) {
				button.setBackground(HIGHLIGHT);
			}
		}
	}

	public void clearHighlights() {
		for (int rank = 1; rank <= 8; rank++) {
			for (char file = 'a'; file <= 'h'; file++) {
				buttons.get(key(file, rank)).setBackground(squareColor(file, rank));
			}
		}
	}

	public void showCheckIndicator(Square square) {
		if (square == null && board != null) {
			for (int rank = 1; rank <= 8; rank++) {
				for (char file = 'a'; file <= 'h'; file++) {
					Square candidate = board.getSquare(file, rank);
					Piece piece = candidate.getOccupant();
					if (piece != null && piece.getType() == model.PieceType.KING) {
						buttons.get(key(candidate)).setBackground(CHECK);
					}
				}
			}
			return;
		}
		JButton button = buttons.get(key(square));
		if (button != null) {
			button.setBackground(CHECK);
		}
	}

	public void clearCheckIndicator() {
		clearHighlights();
	}

	private void createSquares() {
		Font pieceFont = new Font(Font.SANS_SERIF, Font.PLAIN, 44);
		for (int rank = 8; rank >= 1; rank--) {
			for (char file = 'a'; file <= 'h'; file++) {
				final char squareFile = file;
				final int squareRank = rank;
				JButton button = new JButton();
				button.setFont(pieceFont);
				button.setFocusPainted(false);
				button.setOpaque(true);
				button.setBorder(BorderFactory.createEmptyBorder());
				button.setBackground(squareColor(file, rank));
				button.setPreferredSize(new Dimension(72, 72));
				button.addActionListener(event -> notifySquareClick(squareFile, squareRank));
				buttons.put(key(file, rank), button);
				panel.add(button, BorderLayout.CENTER);
			}
		}
	}

	private void notifySquareClick(char file, int rank) {
		if (board != null && squareClickHandler != null) {
			squareClickHandler.accept(board.getSquare(file, rank));
		}
	}

	private java.awt.Color squareColor(char file, int rank) {
		return ((file - 'a') + rank) % 2 == 0 ? DARK_SQUARE : LIGHT_SQUARE;
	}

	private String key(Square square) {
		return key(square.getFile(), square.getRank());
	}

	private String key(char file, int rank) {
		return Character.toString(file) + rank;
	}

	private String pieceSymbol(Piece piece) {
		if (piece == null) {
			return "";
		}

		boolean white = piece.getColor() == Color.WHITE;
		switch (piece.getType()) {
			case KING:
				return white ? "\u2654" : "\u265A";
			case QUEEN:
				return white ? "\u2655" : "\u265B";
			case ROOK:
				return white ? "\u2656" : "\u265C";
			case BISHOP:
				return white ? "\u2657" : "\u265D";
			case KNIGHT:
				return white ? "\u2658" : "\u265E";
			case PAWN:
				return white ? "\u2659" : "\u265F";
			default:
				return "";
		}
	}
}
